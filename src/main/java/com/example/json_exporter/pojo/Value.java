package com.example.json_exporter.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Value {
    public String name;
    public String path;

    @Override
    public String toString() {
        return "Value{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
