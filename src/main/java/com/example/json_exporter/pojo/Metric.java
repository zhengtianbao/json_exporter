package com.example.json_exporter.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.json_exporter.common.constant.ParseType;
import com.example.json_exporter.common.constant.ValueType;
import lombok.Data;

import java.util.ArrayList;

/**
 * 指标类
 *
 */
@Data
@TableName("metric")
public class Metric {
    @TableId(type = IdType.AUTO)
    public Integer id;

    /** 指标名称 */
    public String name;

    /** 指标对应的JSONPath */
    public String path;

    /** 指标对应prometheus的metric帮助信息 */
    public String help;

    /** JSONPath解析类型 */
    public ParseType type;

    /** 指标的label键值对 值为label的JSONPath */
    @TableField(exist = false)
    public ArrayList<Label> labels;

    /** 指标对应prometheus的metric类型 */
    public ValueType valueType;

    /** 解析Object类型的指标值 健为value的名字 值为value的JSONPath */
    @TableField(exist = false)
    public ArrayList<Value> values;

    public Integer serverId;


}
