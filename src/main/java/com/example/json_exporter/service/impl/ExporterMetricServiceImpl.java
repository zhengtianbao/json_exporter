package com.example.json_exporter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.json_exporter.mapper.MetricMapper;
import com.example.json_exporter.pojo.Metric;
import com.example.json_exporter.service.ExporterMetricService;
import org.springframework.stereotype.Service;

@Service
public class ExporterMetricServiceImpl extends ServiceImpl<MetricMapper, Metric> implements ExporterMetricService {
}
