package com.example.json_exporter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.json_exporter.mapper.LabelMapper;
import com.example.json_exporter.mapper.MetricMapper;
import com.example.json_exporter.mapper.ServerMapper;
import com.example.json_exporter.mapper.ValueMapper;
import com.example.json_exporter.pojo.*;
import com.example.json_exporter.service.ExporterServerService;
import io.prometheus.client.Collector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.json_exporter.common.constant.ParseType.OBJECT;
import static com.example.json_exporter.common.constant.ParseType.VALUE;


@Slf4j
@Service
@Qualifier("exporterServerService")
public class ExporterServerServiceImpl extends ServiceImpl<ServerMapper, Server> implements ExporterServerService {

    @Autowired
    private ServerMapper serverMapper;

    @Autowired
    private MetricMapper metricMapper;

    @Autowired
    private LabelMapper labelMapper;

    @Autowired
    private ValueMapper valueMapper;

    @Override
    public ArrayList<Metric> getMetricsByServerID(Integer id) {
        ArrayList<Metric> rst = new ArrayList<>();
        Server server = serverMapper.selectById(id);
        QueryWrapper<Metric> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("server_id", server.getId());
        List<Metric> metrics = metricMapper.selectList(queryWrapper);
        log.info(metrics.toString());
        for (Metric metric: metrics) {
            QueryWrapper<Label> labelQueryWrapper = new QueryWrapper<>();
            labelQueryWrapper.eq("metric_id", metric.getId());
            List<Label> labels = labelMapper.selectList(labelQueryWrapper);
            metric.setLabels(new ArrayList<>(labels));
            QueryWrapper<Value> valueQueryWrapper = new QueryWrapper<>();
            valueQueryWrapper.eq("metric_id", metric.getId());
            List<Value> values = valueMapper.selectList(valueQueryWrapper);
            metric.setValues(new ArrayList<>(values));
        }
        return new ArrayList<>(metrics);
    }

    @Override
    public ArrayList<JSONMetric> getJSONMetricsByServerID(Integer id) {
        ArrayList<Metric> metrics = this.getMetricsByServerID(id);
        return this.convertMetricsToJSONMetrics(metrics);
    }

    public ArrayList<JSONMetric> convertMetricsToJSONMetrics(ArrayList<Metric> metrics) {
        ArrayList<JSONMetric> jmetrics = new ArrayList<>();
        Collector.Type valueType;
        for (Metric metric: metrics) {
            switch (metric.getValueType()) {
                case ValueTypeGauge:
                    valueType = Collector.Type.GAUGE;
                    break;
                case ValueTypeCounter:
                    valueType = Collector.Type.COUNTER;
                    break;
                default:
                    valueType = Collector.Type.UNKNOWN;
            }
            ArrayList<String> variableLabels = new ArrayList<>();
            ArrayList<String> variableLabelsValues = new ArrayList<>();
            ArrayList<Label> labels = metric.getLabels();
            JSONMetric j;
            switch (metric.getType()){
                case VALUE:
                    for (Label label: labels) {
                        variableLabels.add(label.getName());
                        variableLabelsValues.add(label.getPath());
                    }
                    j = new JSONMetric();
                    j.setType(VALUE);
                    j.setName(metric.getName());
                    j.setHelp(metric.getHelp());
                    j.setKeyJSONPath(metric.getPath());
                    j.setVariableLabels(variableLabels);
                    j.setLabelsJSONPath(variableLabelsValues);
                    j.setValueType(valueType);
                    jmetrics.add(j);
                    break;
                case OBJECT:
                    ArrayList<Value> values = metric.getValues(); // 只有object类型有values 对应多个指标
                    for(Value value: values) {
                        String name = metric.getName() + "_" + value.getName(); // value指标名为源指标名+value名
                        variableLabels = new ArrayList<>();
                        variableLabelsValues = new ArrayList<>();
                        for (Label label: labels) { // 但都共用同样的label
                            variableLabels.add(label.getName());
                            variableLabelsValues.add(label.getPath());
                        }
                        j = new JSONMetric();
                        j.setType(OBJECT);
                        j.setName(name);
                        j.setHelp(metric.getHelp());
                        j.setKeyJSONPath(metric.getPath()); // 通过这里的keyJSONPath找到value所在的json对象
                        j.setValueJSONPath(value.getPath()); // 设置value指标的JSONPath 相对于上面的json对象
                        j.setVariableLabels(variableLabels);
                        j.setLabelsJSONPath(variableLabelsValues);
                        j.setValueType(valueType);
                        jmetrics.add(j);
                    }
                    break;
                default:
                    log.error("Unknown metric type");
            }
        }
        return jmetrics;
    }
}
