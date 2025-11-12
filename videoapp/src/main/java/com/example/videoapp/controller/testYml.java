package com.example.videoapp.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testYml {
    @Value("${testDemo.name}")
    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @GetMapping("/testyml")
    public String testYml(){
        return this.name;
    }
}
