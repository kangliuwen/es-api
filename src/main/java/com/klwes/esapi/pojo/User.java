package com.klwes.esapi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: klw
 * @time: 2020-10-12 11:08
 */
@Data
@Component
public class User {
    private String name;
    private int age;
    private String desc;

    public User() {
    }

    public User(String name, int age, String desc) {
        this.name = name;
        this.age = age;
        this.desc = desc;
    }
}
