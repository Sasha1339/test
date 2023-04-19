package ru.mpei.spring.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.mpei.spring.domain.Course;
import ru.mpei.spring.domain.Grade;
import ru.mpei.spring.domain.Groups;
import ru.mpei.spring.domain.Student;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class StudentDaoJdbc implements StudentDao{


    private final JdbcOperations jdbc;

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    public StudentDaoJdbc(JdbcOperations jdbc, NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.jdbc = jdbc;
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
    }

    @Override
    public int count() {
        Integer count = jdbc.queryForObject("select count(*) from student", Integer.class);
        return count == null? 0: count;
    }

    @Override
    public Student getById(long id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        return namedParameterJdbcOperations.queryForObject("select id, name from persons where id = :id", params, new StudentMapper()
        );
    }

    @Override
    public List<Student> getAll() {
        return jdbc.query("select id, name, groups_id from student", new StudentMapper());
    }
    public List<Course> getAllCourse() {
        return jdbc.query("select id, name from course", new CourseMapper());
    }

    public List<Groups> getAllToGroup() {
        return jdbc.query("select id, name from groups", new GroupsMapper());
    }
    public List<Grade> getAllToGrade() {
        return jdbc.query("select id, grade from grade", new GradeMapper());
    }

    @Override
    public List<Student> getAllStugentWithGroup(){
        List<Student> students = getAll();
        List<Groups> groups = getAllToGroup();

        for(Student student: students){

            student.setGroup(String.valueOf(groups.get(Integer.parseInt(student.getGroup())-1).getName()));
        }
        return students;
    }

    @Override
    public List<List> getSCG() {
        return jdbc.query("select student_id, course_id, grade_id from STUDENT_COURSE_GRADE", new SCGMapper());
    }


    @Override
    public List<List> getStudentByGroup(int groups_id){
        List<List> scg = getSCG();
        List<Student> students = getAll();
        List<List> result = new ArrayList<>();
        for(List scg1: scg){
            for(Student student: students){
                if (student.getId() == Integer.parseInt(String.valueOf(scg1.get(0)))){
                    if (Integer.parseInt(student.getGroup()) == groups_id){
                        result.add(List.of(student.getId(), student.getName(), scg1.get(1), scg1.get(2)));
                    }
                }
            }
        }
        return result;
    }
    @Override
    public  List<List> getStudentByGroupWifhGrade(int grade_id){
        List<List> studentWithGrade = getStudentByGroup(grade_id);
        List<List> result = new ArrayList<>();
        int gradeint = 0;
        List<Grade> grade = getAllToGrade();
        for (List student: studentWithGrade){
            gradeint = grade.get(Integer.parseInt(String.valueOf(student.get(3)))-1).getGrade();
            result.add(List.of(student.get(0), student.get(1), student.get(2), Integer.parseInt(String.valueOf(gradeint))));
        }
        return result;
    }

    public List<List> getGradeToCourse(int group_id){
        List<List> studentToCourses = getStudentByGroupWifhGrade(group_id);
        List<List> result = new ArrayList<>();
        List<Course> course = getAllCourse();
        boolean flag ;
        int numberCourse = 0;
        int gradeSum = 0;
        int gradeCount = 0;
        while (studentToCourses.size() > 0){
            flag = false;
            for(List student: studentToCourses) {
                if (studentToCourses.indexOf(student) == 0 && numberCourse == 0) {
                    numberCourse = Integer.parseInt(String.valueOf(student.get(2)));
                }
                if (Integer.parseInt(String.valueOf(student.get(2))) == numberCourse) {
                    gradeSum += Integer.parseInt(String.valueOf(student.get(3)));
                    gradeCount++;
                    studentToCourses.remove(student);
                    flag = true;
                }
              if (studentToCourses.size() == 0)  {
                  result.add(List.of(course.get(numberCourse-1).getName(), ((float)gradeSum)/((float) gradeCount)));
                  numberCourse = 0;
                  gradeSum = 0;
                  gradeCount = 0;
                  break;
            }else if (studentToCourses.get(studentToCourses.size()-1) == student ){
                    result.add(List.of(course.get(numberCourse-1).getName(), ((float)gradeSum)/((float) gradeCount)));
                    numberCourse = 0;
                    gradeSum = 0;
                    gradeCount = 0;
                  break;
                } else if (flag) {
                    break;
                }
            }



        }
        return result;
    }

    @Override
    public String getAverageGrade(int group_id){
        List<List> info = getGradeToCourse(group_id);
        List<Groups> group = getAllToGroup();
        String result = "";
        for(List i: info){
            result = result + "По курсу "+String.valueOf(i.get(0))+" в группе "+String.valueOf(group.get(group_id-1).getName())
                    +" средний бал равен - "+ String.valueOf(i.get(1))+"\n";
        }
        return result;
    }


    private static class CourseMapper implements RowMapper<Course>{
        @Override
        public Course mapRow(ResultSet resultSet, int i) throws SQLException{
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            return new Course(id, name);
        }

    }
    private static class GradeMapper implements RowMapper<Grade>{
        @Override
        public Grade mapRow(ResultSet resultSet, int i) throws SQLException{
            long id = resultSet.getLong("id");
            int grade = resultSet.getInt("grade");
            return new Grade(id, grade);
        }

    }

    private static class GroupsMapper implements RowMapper<Groups>{
        @Override
        public Groups mapRow(ResultSet resultSet, int i) throws SQLException{
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            return new Groups(id, name);
        }

    }
    private static class SCGMapper implements RowMapper<List>{
        @Override
        public List mapRow(ResultSet resultSet, int i) throws SQLException{
            long student_id= resultSet.getLong("student_id");
            long course_id = resultSet.getLong("course_id");
            long grade_id = resultSet.getLong("grade_id");
            return List.of(student_id, course_id, grade_id);
        }

    }

    private static class StudentMapper implements RowMapper<Student>{
        @Override
        public Student mapRow(ResultSet resultSet, int i) throws SQLException{
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            String group = resultSet.getString("groups_id");
            return new Student(id, name, group);
        }

    }

}
