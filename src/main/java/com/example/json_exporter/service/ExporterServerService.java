package com.example.json_exporter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.json_exporter.pojo.JSONMetric;
import com.example.json_exporter.pojo.Metric;
import com.example.json_exporter.pojo.Server;

import java.util.ArrayList;

public interface ExporterServerService extends IService<Server> {

    ArrayList<Metric> getMetricsByServerID(Integer id);

    ArrayList<JSONMetric> getJSONMetricsByServerID(Integer id);

}
