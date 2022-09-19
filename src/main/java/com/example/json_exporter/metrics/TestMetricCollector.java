package com.example.json_exporter.metrics;

import io.prometheus.client.Collector;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

@Slf4j
public class TestMetricCollector extends Collector {
    @Override
    public List<MetricFamilySamples> collect() {
        MetricFamilySamples single = new MetricFamilySamples("test_metric", Type.GAUGE, "test_metric",
                Collections.singletonList(
                        new MetricFamilySamples.Sample("test_metric_count",
                                Collections.emptyList(), Collections.emptyList(), Math.random())));
        return Collections.singletonList(single);
    }
}
