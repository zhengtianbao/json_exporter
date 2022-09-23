package com.example.json_exporter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.json_exporter.mapper.ValueMapper;
import com.example.json_exporter.pojo.Value;
import com.example.json_exporter.service.ExporterValueService;
import org.springframework.stereotype.Service;

@Service
public class ExporterValueServiceImpl extends ServiceImpl<ValueMapper, Value> implements ExporterValueService {
}
