package com.example.json_exporter.metrics;

import io.prometheus.client.Collector;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

@Slf4j
public class ServerMetricCollector extends Collector{

    @Override
    public List<Collector.MetricFamilySamples> collect() {
        log.info("############# Server metric collector");
        Collector.MetricFamilySamples single = new Collector.MetricFamilySamples("server_metric", Collector.Type.GAUGE, "server_metric",
                Collections.singletonList(
                        new Collector.MetricFamilySamples.Sample("server_metric_count",
                                Collections.emptyList(), Collections.emptyList(), Math.random())));
        return Collections.singletonList(single);
    }
}
