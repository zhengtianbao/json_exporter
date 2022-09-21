package com.example.json_exporter.exporter;

import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

@Slf4j
public class MetricExporter {
    private final CollectorRegistry collectorRegistry = new CollectorRegistry();

    public String writeRegistry() {
        try {
            Writer writer = new StringWriter();
            TextFormat.write004(writer, collectorRegistry.metricFamilySamples());
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException("Writing metrics failed", e);
        }
    }

    public void register(Collector collector) {
        collectorRegistry.register(collector);
    }
}
