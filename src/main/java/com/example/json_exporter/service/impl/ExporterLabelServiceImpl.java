package com.example.json_exporter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.json_exporter.mapper.LabelMapper;
import com.example.json_exporter.pojo.Label;
import com.example.json_exporter.service.ExporterLabelService;
import org.springframework.stereotype.Service;

@Service
public class ExporterLabelServiceImpl extends ServiceImpl<LabelMapper, Label> implements ExporterLabelService {
}
