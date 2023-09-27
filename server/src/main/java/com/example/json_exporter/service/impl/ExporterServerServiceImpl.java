package com.example.json_exporter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.json_exporter.mapper.*;
import com.example.json_exporter.pojo.*;
import com.example.json_exporter.service.ExporterServerService;
import io.prometheus.client.Collector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.json_exporter.common.constant.ParseType.OBJECT;
import static com.example.json_exporter.common.constant.ParseType.VALUE;
import static com.example.json_exporter.common.constant.ValueType.ValueTypeUntyped;


@Slf4j
@Service
public class ExporterServerServiceImpl extends ServiceImpl<ServerMapper, Server> implements ExporterServerService {

    @Autowired
    private ServerMapper serverMapper;

    @Autowired
    private MetricMapper metricMapper;

    @Autowired
    private LabelMapper labelMapper;

    @Autowired
    private ValueMapper valueMapper;

    @Autowired
    private PreprocessMapper preprocessMapper;

    @Override
    public List<Server> listDetail() {
        QueryWrapper<Server> queryWrapper = new QueryWrapper<>();
        List<Server> servers = serverMapper.selectList(queryWrapper);
        for (int i = 0; i < servers.size(); i++) {
            QueryWrapper<Preprocess> qw = new QueryWrapper<>();
            qw.eq("server_id", servers.get(i).getId());
            List<Preprocess> ps = preprocessMapper.selectList(qw);
            servers.get(i).setPreprocesses(new ArrayList<>(ps));

            QueryWrapper<Metric> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("server_id", servers.get(i).getId());
            List<Metric> metrics = metricMapper.selectList(queryWrapper1);
            for (int j = 0; j < metrics.size(); j++) {
                QueryWrapper<Label> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.eq("metric_id", metrics.get(j).getId());
                List<Label> labels = labelMapper.selectList(queryWrapper2);
                metrics.get(j).setLabels(new ArrayList<>(labels));
                QueryWrapper<Value> queryWrapper3 = new QueryWrapper<>();
                queryWrapper3.eq("metric_id", metrics.get(j).getId());
                List<Value> values = valueMapper.selectList(queryWrapper3);
                metrics.get(j).setValues(new ArrayList<>(values));
            }
            servers.get(i).setMetrics(new ArrayList<>(metrics));
        }
        return servers;
    }

    @Override
    public void saveWithDetail(Server server) {
        serverMapper.insert(server);
        log.info(server.toString());
        for (int index = 0; index < server.preprocesses.size(); index++) {
            server.preprocesses.get(index).setServerId(server.getId());
            preprocessMapper.insert(server.preprocesses.get(index));
        }
        for (int i = 0; i < server.metrics.size(); i++) {
            server.metrics.get(i).setServerId(server.getId());
            server.metrics.get(i).setValueType(ValueTypeUntyped);
            metricMapper.insert(server.metrics.get(i));
            for (int j = 0; j < server.metrics.get(i).getLabels().size(); j++) {
                server.metrics.get(i).getLabels().get(j).setMetricId(server.metrics.get(i).getId());
                labelMapper.insert(server.metrics.get(i).getLabels().get(j));
            }
            for (int k = 0; k < server.metrics.get(i).getValues().size(); k++) {
                server.metrics.get(i).getValues().get(k).setMetricId(server.metrics.get(i).getId());
                valueMapper.insert(server.metrics.get(i).getValues().get(k));
            }
        }
    }

    @Override
    public void removeDetailById(Integer id) {
        QueryWrapper<Preprocess> qw = new QueryWrapper<>();
        qw.eq("server_id", id);
        List<Preprocess> ps = preprocessMapper.selectList(qw);
        for (int index = 0; index < ps.size();index++) {
            Integer pid = ps.get(index).getId();
            preprocessMapper.deleteById(pid);
        }
        QueryWrapper<Metric> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("server_id", id);
        List<Metric> metrics = metricMapper.selectList(queryWrapper);
        for (int i = 0; i < metrics.size(); i++) {
            Integer metricId = metrics.get(i).getId();
            QueryWrapper<Label> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("metric_id", metricId);
            List<Label> labels = labelMapper.selectList(queryWrapper1);
            for (int j = 0; j < labels.size(); j++) {
                labelMapper.deleteById(labels.get(j).getId());
            }
            QueryWrapper<Value> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("metric_id", metricId);
            List<Value> values = valueMapper.selectList(queryWrapper2);
            for (int k = 0; k < values.size(); k++) {
                valueMapper.deleteById(values.get(k).getId());
            }
            metricMapper.deleteById(metricId);
        }
        serverMapper.deleteById(id);
    }

    @Override
    public void updateDetailById(Integer id, Server server) {
        log.info(server.toString());
        server.setId(id);
        serverMapper.updateById(server);
        // remove old preprocesses
        QueryWrapper<Preprocess> qw = new QueryWrapper<>();
        qw.eq("server_id", id);
        List<Preprocess> ps = preprocessMapper.selectList(qw);
        for (int index = 0; index < ps.size();index++) {
            Integer pid = ps.get(index).getId();
            preprocessMapper.deleteById(pid);
        }
        // add new preprocesses
        for (int index = 0; index < server.preprocesses.size(); index++) {
            server.preprocesses.get(index).setServerId(server.getId());
            preprocessMapper.insert(server.preprocesses.get(index));
        }
        // remove old metrics
        QueryWrapper<Metric> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("server_id", id);
        List<Metric> metrics = metricMapper.selectList(queryWrapper);
        for (int i = 0; i < metrics.size(); i++) {
            Integer metricId = metrics.get(i).getId();
            QueryWrapper<Label> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("metric_id", metricId);
            List<Label> labels = labelMapper.selectList(queryWrapper1);
            for (int j = 0; j < labels.size(); j++) {
                labelMapper.deleteById(labels.get(j).getId());
            }
            QueryWrapper<Value> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("metric_id", metricId);
            List<Value> values = valueMapper.selectList(queryWrapper2);
            for (int k = 0; k < values.size(); k++) {
                valueMapper.deleteById(values.get(k).getId());
            }
            metricMapper.deleteById(metricId);
        }
        // add new metrics
        for (int i = 0; i < server.metrics.size(); i++) {
            server.metrics.get(i).setServerId(server.getId());
            server.metrics.get(i).setValueType(ValueTypeUntyped);
            metricMapper.insert(server.metrics.get(i));
            for (int j = 0; j < server.metrics.get(i).getLabels().size(); j++) {
                server.metrics.get(i).getLabels().get(j).setMetricId(server.metrics.get(i).getId());
                labelMapper.insert(server.metrics.get(i).getLabels().get(j));
            }
            for (int k = 0; k < server.metrics.get(i).getValues().size(); k++) {
                server.metrics.get(i).getValues().get(k).setMetricId(server.metrics.get(i).getId());
                valueMapper.insert(server.metrics.get(i).getValues().get(k));
            }
        }
    }

    @Override
    public ArrayList<Metric> getMetricsByServerID(Integer id) {
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

    @Override
    public ArrayList<Preprocess> getPreprocessesByServerID(Integer id) {
        QueryWrapper<Preprocess> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("server_id", id);
        List<Preprocess> ps = preprocessMapper.selectList(queryWrapper);
        return new ArrayList<>(ps);
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
