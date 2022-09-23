package com.example.json_exporter.common.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ValueType {
    ValueTypeGauge(0, "gauge"),
    ValueTypeCounter(1, "counter"),
    ValueTypeUntyped(2, "unkonwn");

    @EnumValue
    private final  int code;

    @JsonValue
    private final String name;

    ValueType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
