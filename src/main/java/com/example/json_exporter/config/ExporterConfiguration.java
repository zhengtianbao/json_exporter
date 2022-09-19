package com.example.json_exporter.config;

import com.example.json_exporter.exporter.MetricExporter;
import com.example.json_exporter.metrics.ServerMetricCollector;
import com.example.json_exporter.metrics.TestMetricCollector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ExporterConfiguration {

    @Bean
    public MetricExporter metricExporter() {
        MetricExporter exporter = new MetricExporter();
        exporter.register(new TestMetricCollector());
        exporter.register(new ServerMetricCollector());
        return exporter;
    }

}
