package com.example.json_exporter.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.ArrayList;

/**
 * 需要抓取的服务信息
 *
 */
@Data
@TableName("server")
public class Server {
    @TableId(type = IdType.AUTO)
    public Integer id;

    /** 服务名称 */
    public String name;

    /** 服务暴露的指标地址 */
    public String url;

    /** 需要抓取的指标 */
    @TableField(exist = false)
    public ArrayList<Metric> metrics;

}
