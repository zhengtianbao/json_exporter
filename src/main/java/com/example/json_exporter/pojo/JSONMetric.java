package com.example.json_exporter.pojo;

import io.prometheus.client.Collector;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * JSON格式的指标 对应sampler
 */
@Getter
@Setter
public class JSONMetric {
    /** prometheus metric的名称 */
    public String name;
    /** prometheus metric的帮助 */
    public String help;
    /** prometheus metric的label列表 */
    public ArrayList<String> variableLabels;
    /** label的JSONPath */
    public ArrayList<String> labelsJSONPath;
    /** JSONPath解析类型 */
    public ParseType type;
    /** 指标对应的JSONPath */
    public String keyJSONPath;
    /** Object类型值的JSONPath */
    public String valueJSONPath;
    /** 指标对应prometheus的metric类型 */
    public Collector.Type valueType;
    public static enum ParseType {
        VALUE,
        OBJECT;
    }

}
