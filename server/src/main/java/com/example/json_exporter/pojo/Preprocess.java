package com.example.json_exporter.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("preprocess")
public class Preprocess {
    @TableId(type = IdType.AUTO)
    public Integer id;

    public String name;

    public Integer serverId;
}
