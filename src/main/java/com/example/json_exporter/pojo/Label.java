package com.example.json_exporter.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Label {
    public String name;
    public String path;

    @Override
    public String toString() {
        return "Label{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
