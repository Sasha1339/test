package ru.mpei.spring;

import org.h2.tools.Console;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import ru.mpei.spring.dao.StudentDao;
import ru.mpei.spring.domain.Student;

@SpringBootApplication
public class Main {

    public static void main(String[] args) throws Exception {
        ApplicationContext context =
                SpringApplication.run(Main.class);

      StudentDao dao = context.getBean(StudentDao.class);
        System.out.println(dao.getAverageGrade(3));
        Console.main(args);
    }


}
