package com.example.json_exporter.controller;

import com.example.json_exporter.exporter.MetricExporter;
import com.example.json_exporter.metrics.ServerMetricCollector;
import com.example.json_exporter.metrics.TestMetricCollector;
import io.prometheus.client.exporter.common.TextFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@RestController
@Slf4j
public class JsonExportMetricController {
//    private final MetricExporter exporter;
//
//    @Autowired
//    public JsonExportMetricController(MetricExporter exporter) {
//        this.exporter = exporter;
//    }

    @ResponseBody
    @RequestMapping(value = "/prometheus", method = RequestMethod.GET)
    public ResponseEntity metrics(@RequestParam String serverName) {
        MetricExporter exporter = new MetricExporter();
        if (serverName.equals("test")) {
            exporter.register(new TestMetricCollector());
        } else if (serverName.equals("server")) {
            exporter.register(new ServerMetricCollector());
        }
        String response = exporter.writeRegistry();
        return ResponseEntity.ok().header(CONTENT_TYPE, TextFormat.CONTENT_TYPE_004).body(response);
    }
}
