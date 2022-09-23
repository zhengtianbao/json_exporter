package com.example.json_exporter.common.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ParseType {
    VALUE(0, "value"),
    OBJECT(1, "object");
    @EnumValue
    private final int code;

    @JsonValue
    private final String name;

    ParseType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
