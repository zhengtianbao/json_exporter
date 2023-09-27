package com.example.json_exporter.metrics;

import com.example.json_exporter.pojo.JSONMetric;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import io.prometheus.client.Collector;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ServerMetricCollector extends Collector{

    private ArrayList<JSONMetric> jmetrics;
    private String jsonData;

    public ServerMetricCollector(String jsonData, ArrayList<JSONMetric> jmetrics){
        this.jsonData = jsonData;
        this.jmetrics = jmetrics;
    }
    @Override
    public List<Collector.MetricFamilySamples> collect() {
        log.info("############# Server metric collector");

        ArrayList<MetricFamilySamples> mfs = new ArrayList<>();

        for(JSONMetric m : this.jmetrics) {
            switch (m.getType()) {
                case VALUE:
                    try {
                        ArrayList<MetricFamilySamples.Sample> samples = new ArrayList<>();
                        ObjectMapper objectMapper = new ObjectMapper();
                        Object value = extractValue(this.jsonData, m.getKeyJSONPath());
                        double floatValue = SanitizeValue(objectMapper.writer().writeValueAsString(value));
                        ArrayList<String> labelValues = extractLabels(this.jsonData, m.getLabelsJSONPath());
                        samples.add(new MetricFamilySamples.Sample(m.getName(), m.getVariableLabels(), labelValues, floatValue));
                        MetricFamilySamples metricFamilySamples = new MetricFamilySamples(
                                m.getName(),
                                m.getValueType(),
                                m.getHelp(),
                                samples
                        );
                        mfs.add(metricFamilySamples);
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage());
                    }
                    break;
                case OBJECT:
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        List<Object> values = extractValueAsList(this.jsonData, m.getKeyJSONPath());
                        for (Object v : values) {
                            ArrayList<MetricFamilySamples.Sample> samples = new ArrayList<>();
                            Object value = extractValue(objectMapper.writer().writeValueAsString(v), m.getValueJSONPath());
                            double floatValue = SanitizeValue(objectMapper.writer().writeValueAsString(value));
                            ArrayList<String> labelValues = extractLabels(objectMapper.writer().writeValueAsString(v), m.getLabelsJSONPath());
                            samples.add(new MetricFamilySamples.Sample(m.getName(), m.getVariableLabels(), labelValues, floatValue));
                            MetricFamilySamples metricFamilySamples = new MetricFamilySamples(
                                    m.getName(),
                                    m.getValueType(),
                                    m.getHelp(),
                                    samples
                            );
                            mfs.add(metricFamilySamples);
                        }
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage());
                    }
                    break;
                default:
                    log.error("Unknown scrape config type");
            }
        }
        return mfs;
    }

    public List<Object> extractValueAsList(String body, String path) {
        ObjectMapper objectMapper = new ObjectMapper();
        Configuration conf;
        conf = Configuration.builder().jsonProvider(new JacksonJsonProvider(objectMapper)).build().addOptions(Option.ALWAYS_RETURN_LIST);
        return JsonPath.using(conf).parse(body).read(path);
    }

    public Object extractValue(String body, String path) {
        ObjectMapper objectMapper = new ObjectMapper();
        Configuration conf;
        conf = Configuration.builder().jsonProvider(new JacksonJsonProvider(objectMapper)).build();
        return JsonPath.using(conf).parse(body).read(path);
    }

    public ArrayList<String> extractLabels(String body, ArrayList<String> paths) {
        ArrayList<String> labels = new ArrayList<>();
        for (int i = 0; i < paths.size(); i++) {
            Object value = extractValue(body, paths.get(i));
            labels.add(i, value.toString());
        }
        return labels;
    }

    public double SanitizeValue(String s) {
        // jsonpath在使用filter过滤后产生的列表无法通过index获取元素
        // $.resourceSecondsMap.entry[?(@.key == "memory-mb")][0].value // 这样的语法是不支持的
        // $.resourceSecondsMap.entry[?(@.key == "memory-mb")].value // 只能这样
        // 返回结果为 ["200268"]
        if (s.startsWith("[")) {
            s = s.replace("[", "").replace("]", "").replace("\"", "");
        }
        double rst;
        try {
            rst = Double.parseDouble(s);
            return rst;
        } catch (NumberFormatException e) {
            log.debug("error parse");
        }
        Boolean b = Boolean.parseBoolean(s);
        if (b) {
            return 1.0;
        } else {
            return 0.0;
        }
    }
}
