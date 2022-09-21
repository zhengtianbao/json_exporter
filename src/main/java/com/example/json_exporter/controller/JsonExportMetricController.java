package com.example.json_exporter.controller;

import com.example.json_exporter.exporter.MetricExporter;
import com.example.json_exporter.metrics.ServerMetricCollector;
import com.example.json_exporter.pojo.JSONMetric;
import com.example.json_exporter.pojo.Server;
import com.example.json_exporter.service.ExporterService;
import com.example.json_exporter.service.FetcherService;
import io.prometheus.client.exporter.common.TextFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@RestController
@Slf4j
public class JsonExportMetricController {

    @Autowired
    private ExporterService exporterService;

    @Autowired
    private FetcherService fetcherService;

    @ResponseBody
    @RequestMapping(value = "/prometheus", method = RequestMethod.GET)
    public ResponseEntity metrics(@RequestParam String serverName) {
        MetricExporter exporter = new MetricExporter();
        Server server = exporterService.getServerByID(1);
        String jsonData = fetcherService.fetch(server.getUrl());
        ArrayList<JSONMetric> jmetrics = exporterService.getJSONMetricsByServerID(serverName);
        exporter.register(new ServerMetricCollector(jsonData, jmetrics));
        String response = exporter.writeRegistry();
        return ResponseEntity.ok().header(CONTENT_TYPE, TextFormat.CONTENT_TYPE_004).body(response);
    }
}
