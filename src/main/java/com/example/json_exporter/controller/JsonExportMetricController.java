package com.example.json_exporter.controller;

import com.example.json_exporter.exporter.MetricExporter;
import com.example.json_exporter.metrics.ServerMetricCollector;
import com.example.json_exporter.pojo.*;
import com.example.json_exporter.service.*;
import io.prometheus.client.exporter.common.TextFormat;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@CrossOrigin
@ApiOperation(value = "json_exporter接口")
@RequestMapping("/exporter")
@RestController
@Slf4j
public class JsonExportMetricController {

    @Autowired
    @Qualifier("exporterServerService")
    private ExporterServerService exporterServerService;

    @Autowired
    private ExporterMetricService exporterMetricService;

    @Autowired
    private ExporterLabelService exporterLabelService;

    @Autowired
    private ExporterValueService exporterValueService;

    @Autowired
    private FetcherService fetcherService;

    @ApiOperation(value = "查询Server列表信息")
    @GetMapping("server")
    public ResponseEntity<Object> getServerList() {
        List<Server> servers = exporterServerService.listDetail();
        return new ResponseEntity<>(servers, HttpStatus.OK);
    }

    @ApiOperation(value = "查询Server信息")
    @GetMapping("server/{id}")
    public ResponseEntity<Object> getServer(@PathVariable("id") Integer id) {
        Server server = exporterServerService.getById(id);
        return new ResponseEntity<>(server, HttpStatus.OK);
    }

    @ApiOperation(value = "增加Server信息")
    @PostMapping("server")
    public ResponseEntity<Object> addServer(@RequestBody Server server) {
        log.info(server.toString());
        exporterServerService.saveWithDetail(server);
        return new ResponseEntity<>("Server is created successfully", HttpStatus.CREATED);
    }

    @ApiOperation(value = "删除Server信息")
    @DeleteMapping("server/{id}")
    public ResponseEntity<Object> deleteServer(@PathVariable("id") Integer id){
        exporterServerService.removeDetailById(id);
        return new ResponseEntity<>("Server is deleted successfully", HttpStatus.OK);
    }

    @ApiOperation(value = "更新Server信息")
    @PutMapping("server/{id}")
    public ResponseEntity<Object> updateServer(@PathVariable("id") Integer id, @RequestBody Server server) {
        exporterServerService.updateDetailById(id, server);
        return new ResponseEntity<>("Server is updated successfully", HttpStatus.OK);
    }

    @ApiOperation(value = "查询Metric列表信息")
    @GetMapping("metric")
    public ResponseEntity<Object> getMetricList() {
        List<Metric> metrics = exporterMetricService.list();
        return new ResponseEntity<>(metrics, HttpStatus.OK);
    }

    @ApiOperation(value = "查询Metric信息")
    @GetMapping("metric/{id}")
    public ResponseEntity<Object> getMetric(@PathVariable("id") Integer id) {
        Metric metric = exporterMetricService.getById(id);
        return new ResponseEntity<>(metric, HttpStatus.OK);
    }

    @ApiOperation(value = "增加Metric信息")
    @PostMapping("metric")
    public ResponseEntity<Object> addMetric(@RequestBody Metric metric) {
        exporterMetricService.save(metric);
        return new ResponseEntity<>("Metric is created successfully", HttpStatus.CREATED);
    }

    @ApiOperation(value = "删除Metric信息")
    @DeleteMapping("metric/{id}")
    public ResponseEntity<Object> deleteMetric(@PathVariable("id") Integer id){
        exporterMetricService.removeById(id);
        return new ResponseEntity<>("Metric is deleted successfully", HttpStatus.OK);
    }

    @ApiOperation(value = "更新Metric信息")
    @PutMapping("metric/{id}")
    public ResponseEntity<Object> updateMetric(@PathVariable("id") Integer id, @RequestBody Metric metric) {
        exporterMetricService.updateById(metric);
        return new ResponseEntity<>("Metric is updated successfully", HttpStatus.OK);
    }

    @ApiOperation(value = "查询Label列表信息")
    @GetMapping("label")
    public ResponseEntity<Object> getLabelList() {
        List<Label> labels = exporterLabelService.list();
        return new ResponseEntity<>(labels, HttpStatus.OK);
    }

    @ApiOperation(value = "查询Label信息")
    @GetMapping("label/{id}")
    public ResponseEntity<Object> getLabel(@PathVariable("id") Integer id) {
        Label label = exporterLabelService.getById(id);
        return new ResponseEntity<>(label, HttpStatus.OK);
    }

    @ApiOperation(value = "增加Label信息")
    @PostMapping("label")
    public ResponseEntity<Object> addLabel(@RequestBody Label label) {
        exporterLabelService.save(label);
        return new ResponseEntity<>("Label is created successfully", HttpStatus.CREATED);
    }

    @ApiOperation(value = "删除Label信息")
    @DeleteMapping("label/{id}")
    public ResponseEntity<Object> deleteLabel(@PathVariable("id") Integer id){
        exporterLabelService.removeById(id);
        return new ResponseEntity<>("Label is deleted successfully", HttpStatus.OK);
    }

    @ApiOperation(value = "更新Label信息")
    @PutMapping("label/{id}")
    public ResponseEntity<Object> updateLabel(@PathVariable("id") Integer id, @RequestBody Label label) {
        exporterLabelService.updateById(label);
        return new ResponseEntity<>("Label is updated successfully", HttpStatus.OK);
    }

    @ApiOperation(value = "查询Value列表信息")
    @GetMapping("value")
    public ResponseEntity<Object> getValueList() {
        List<Value> values = exporterValueService.list();
        return new ResponseEntity<>(values, HttpStatus.OK);
    }

    @ApiOperation(value = "查询Value信息")
    @GetMapping("value/{id}")
    public ResponseEntity<Object> getValue(@PathVariable("id") Integer id) {
        Value value = exporterValueService.getById(id);
        return new ResponseEntity<>(value, HttpStatus.OK);
    }

    @ApiOperation(value = "增加Value信息")
    @PostMapping("value")
    public ResponseEntity<Object> addValue(@RequestBody Value value) {
        exporterValueService.save(value);
        return new ResponseEntity<>("Value is created successfully", HttpStatus.CREATED);
    }

    @ApiOperation(value = "删除Value信息")
    @DeleteMapping("value/{id}")
    public ResponseEntity<Object> deleteValue(@PathVariable("id") Integer id){
        exporterValueService.removeById(id);
        return new ResponseEntity<>("Value is deleted successfully", HttpStatus.OK);
    }

    @ApiOperation(value = "更新Value信息")
    @PutMapping("value/{id}")
    public ResponseEntity<Object> updateValue(@PathVariable("id") Integer id, @RequestBody Value value) {
        exporterValueService.updateById(value);
        return new ResponseEntity<>("Value is updated successfully", HttpStatus.OK);
    }

    @ApiOperation(value = "获取指定serverId符合prometheus格式的metrics信息")
    @ResponseBody
    @RequestMapping(value = "/metrics", method = RequestMethod.GET)
    public ResponseEntity<Object> metrics(@RequestParam Integer serverId) {
        MetricExporter exporter = new MetricExporter();
        Server server = exporterServerService.getById(serverId);
        log.info(server.toString());
        String jsonData = fetcherService.fetch(server.getUrl());
        ArrayList<JSONMetric> jmetrics = exporterServerService.getJSONMetricsByServerID(serverId);
        exporter.register(new ServerMetricCollector(jsonData, jmetrics));
        String response = exporter.writeRegistry();
        return ResponseEntity.ok().header(CONTENT_TYPE, TextFormat.CONTENT_TYPE_004).body(response);
    }
}
