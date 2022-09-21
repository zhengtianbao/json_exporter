package com.example.json_exporter.service;

import com.example.json_exporter.pojo.JSONMetric;
import com.example.json_exporter.pojo.Metric;
import com.example.json_exporter.pojo.Server;

import java.util.ArrayList;

public interface ExporterService {

    Server getServerByID(Integer id);

    ArrayList<Metric> getMetricsByServerID(String serverName);

    ArrayList<JSONMetric> getJSONMetricsByServerID(String serverName);

}
