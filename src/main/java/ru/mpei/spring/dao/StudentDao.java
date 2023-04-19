package ru.mpei.spring.dao;

import ru.mpei.spring.domain.Student;

import java.util.List;

public interface StudentDao {

    int count();

    Student getById(long id);

    List<Student> getAll();
    public List<List> getSCG();
    public  List<List> getStudentByGroupWifhGrade(int grade_id);

    public List<List> getStudentByGroup(int groups_id);
    public List<Student> getAllStugentWithGroup();
    public String getAverageGrade(int group_id);


}
