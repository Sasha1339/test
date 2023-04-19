package ru.mpei.spring.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Data
public class Student {

    private final long id;

    private final String name;

    private String group;

}
