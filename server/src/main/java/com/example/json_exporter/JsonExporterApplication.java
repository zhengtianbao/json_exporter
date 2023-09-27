package com.example.json_exporter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.json_exporter.mapper")
public class JsonExporterApplication {

    public static void main(String[] args) {
        SpringApplication.run(JsonExporterApplication.class, args);
    }

}
