package ru.mpei.spring.domain;


import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class Groups {

    private final long id;

    private final String name;

}
