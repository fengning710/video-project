package com.example.videoapp;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.example.videoapp.mapper")
@SpringBootApplication
public class VideoappApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoappApplication.class, args);
    }

}
