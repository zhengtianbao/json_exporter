package com.example.json_exporter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.json_exporter.pojo.*;

import java.util.ArrayList;
import java.util.List;

public interface ExporterServerService extends IService<Server> {

    ArrayList<Metric> getMetricsByServerID(Integer id);

    List<Server> listDetail();

    void saveWithDetail(Server server);

    void removeDetailById(Integer id);

    void updateDetailById(Integer id, Server server);

    ArrayList<JSONMetric> getJSONMetricsByServerID(Integer id);

    ArrayList<Header> getHeadersByServerID(Integer id);

    ArrayList<Preprocess> getPreprocessesByServerID(Integer id);
}
