package com.example.json_exporter.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * 需要抓取的服务信息
 *
 */
@Getter
@Setter
public class Server {
    /** 需要抓取的指标 */
    public ArrayList<Metric> metrics;
    /** 服务暴露的指标地址 */
    public String url;
    /** 服务名称 */
    public String name;

    @Override
    public String toString() {
        return "Server{" +
                "metrics=" + metrics +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
