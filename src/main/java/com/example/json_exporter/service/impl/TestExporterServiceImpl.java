package com.example.json_exporter.service.impl;

import com.example.json_exporter.pojo.*;
import com.example.json_exporter.service.ExporterService;
import io.prometheus.client.Collector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.example.json_exporter.pojo.JSONMetric.ParseType.OBJECT;
import static com.example.json_exporter.pojo.JSONMetric.ParseType.VALUE;

@Slf4j
@Service
public class TestExporterServiceImpl implements ExporterService {
    @Override
    public Server getServerByID(Integer id) {
        Server server = new Server();
        server.setName("远程公共服务平台");
        server.setUrl("http://localhost:8000/examples/data.json");
        return server;
    }

    @Override
    public ArrayList<Metric> getMetricsByServerID(String serverName) {
        ArrayList<Metric> serverMetrics = new ArrayList<>();
        Metric m1 = new Metric();
        m1.setName("example_global_value");
        m1.setPath("$.counter");
        m1.setHelp("Example of a top-level global value scrape in the json");
        m1.setType(VALUE);
        ArrayList<Label> m1labels = new ArrayList<>();
        Label l = new Label();
        l.setName("location");
        l.setPath("$.location");
        m1labels.add(l);
        m1.setLabels(m1labels);
        m1.setValueType(Metric.ValueType.ValueTypeUntyped);
        serverMetrics.add(m1);
        Metric m2 = new Metric();
        m2.setName("example_value");
        m2.setPath("$.values[?(@.state == \"ACTIVE\")]");
        m2.setHelp("Example of sub-level value scrapes from a json");
        m2.setType(OBJECT);
        ArrayList<Label> m2labels = new ArrayList<>();
        Label l2 = new Label();
        l2.setName("id");
        l2.setPath("$.id");
        m2labels.add(l2);
        m2.setLabels(m2labels);
        ArrayList<Value> m2values = new ArrayList<>();
        Value m2v1 = new Value();
        m2v1.setName("count");
        m2v1.setPath("$.count");
        m2values.add(m2v1);
        Value m2v2 = new Value();
        m2v2.setName("boolean");
        m2v2.setPath("$.some_boolean");
        m2values.add(m2v2);
        m2.setValues(m2values);
        m2.setValueType(Metric.ValueType.ValueTypeUntyped);
        serverMetrics.add(m2);
        return serverMetrics;
    }
    @Override
    public ArrayList<JSONMetric> getJSONMetricsByServerID(String id) {
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
