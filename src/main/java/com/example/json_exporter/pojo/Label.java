package com.example.json_exporter.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Label {
    @TableId(type = IdType.AUTO)
    public Integer id;

    public String name;
    public String path;
    public Integer metricId;
}
