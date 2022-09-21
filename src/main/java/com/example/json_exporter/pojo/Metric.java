package com.example.json_exporter.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * 指标类
 *
 */
@Getter
@Setter
public class Metric {
    /** 指标名称 */
    public String name;
    /** 指标对应的JSONPath */
    public String path;
    /** 指标对应prometheus的metric帮助信息 */
    public String help;
    /** JSONPath解析类型 */
    public JSONMetric.ParseType type;
    /** 指标的label键值对 值为label的JSONPath */
//    public HashMap<String, String> labels;
    public ArrayList<Label> labels;
    /** 指标对应prometheus的metric类型 */
    public ValueType valueType;
    /** 解析Object类型的指标值 健为value的名字 值为value的JSONPath */
//    public HashMap<String, String> values;
    public ArrayList<Value> values;

    @Override
    public String toString() {
        return "Metric{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", help='" + help + '\'' +
                ", type=" + type +
                ", labels=" + labels +
                ", valueType=" + valueType +
                ", values=" + values +
                '}';
    }

    public static enum ValueType {
        ValueTypeGauge,
        ValueTypeCounter,
        ValueTypeUntyped;
    }

}
