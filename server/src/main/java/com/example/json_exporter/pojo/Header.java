package com.example.json_exporter.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("header")
public class Header {
    @TableId(type = IdType.AUTO)
    public Integer id;

    public String value;

    public Integer serverId;
}
