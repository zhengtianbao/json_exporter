package com.example.json_exporter.controller;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.json_exporter.exporter.MetricExporter;
import com.example.json_exporter.metrics.ServerMetricCollector;
import com.example.json_exporter.pojo.Header;
import com.example.json_exporter.pojo.JSONMetric;
import com.example.json_exporter.pojo.Preprocess;
import com.example.json_exporter.pojo.Server;
import com.example.json_exporter.service.ExporterServerService;
import com.example.json_exporter.service.FetcherService;
import com.example.json_exporter.util.JavaScriptConverter;
import com.example.json_exporter.util.XmlConverter;

import io.prometheus.client.exporter.common.TextFormat;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@ApiOperation(value = "json_exporter接口")
@RequestMapping("/backend/exporter")
@RestController
@Slf4j
public class JsonExportMetricController {

    @Autowired
    private ExporterServerService exporterServerService;

    @Autowired
    private FetcherService fetcherService;

    private final String JsonProbeSuccess = "# HELP json_probe_success json_probe_success\n"
            + "# TYPE json_probe_success untyped\n";

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
        exporterServerService.saveWithDetail(server);
        return new ResponseEntity<>("Server is created successfully", HttpStatus.CREATED);
    }

    @ApiOperation(value = "删除Server信息")
    @DeleteMapping("server/{id}")
    public ResponseEntity<Object> deleteServer(@PathVariable("id") Integer id) {
        exporterServerService.removeDetailById(id);
        return new ResponseEntity<>("Server is deleted successfully", HttpStatus.OK);
    }

    @ApiOperation(value = "更新Server信息")
    @PutMapping("server/{id}")
    public ResponseEntity<Object> updateServer(@PathVariable("id") Integer id, @RequestBody Server server) {
        exporterServerService.updateDetailById(id, server);
        return new ResponseEntity<>("Server is updated successfully", HttpStatus.OK);
    }

    @ApiOperation(value = "获取指定serverId符合prometheus格式的metrics信息")
    @ResponseBody
    @RequestMapping(value = "/metrics", method = RequestMethod.GET)
    public ResponseEntity<Object> metrics(@RequestParam Integer serverId) {
        MetricExporter exporter = new MetricExporter();
        Server server = exporterServerService.getById(serverId);
        ArrayList<Header> headers = exporterServerService.getHeadersByServerID(serverId);
        String instanceString = server.getUrl();
        try {
            URL url = new URL(server.getUrl());
            int port = url.getPort();
            if (port == -1) {
                port = url.getDefaultPort();
            }
            InetAddress address = InetAddress.getByName(url.getHost());
            String ip = address.getHostAddress();
            instanceString = ip + ":" + port;
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        String data;
        try {
            data = fetcherService.fetch(server, headers);
        } catch (Exception e) {
            String targetDownMetric = JsonProbeSuccess + "json_probe_success{instance=\"" + instanceString
                    + "\", target=\""
                    + server.getUrl()
                    + "\"} 0\n";
            return ResponseEntity.ok().header(CONTENT_TYPE, TextFormat.CONTENT_TYPE_004).body(targetDownMetric);
        }

        ArrayList<Preprocess> ps = exporterServerService.getPreprocessesByServerID(serverId);
        if (!ps.isEmpty()) {
            for (Preprocess p : ps) {
                switch (p.getName()) {
                    case "xmlconvert":
                        data = XmlConverter.toJSON(data);
                    case "jsconvert":
                        data = JavaScriptConverter.toJSON(data, p.getScript());
                    default:
                }
            }
        }
        log.info(data);
        ArrayList<JSONMetric> jmetrics = exporterServerService.getJSONMetricsByServerID(serverId);
        exporter.register(new ServerMetricCollector(data, jmetrics));
        String response = exporter.writeRegistry();
        String targetUpMetric = JsonProbeSuccess + "json_probe_success{instance=\"" + instanceString + "\", target=\""
                + server.getUrl()
                + "\"} 1\n";
        return ResponseEntity.ok().header(CONTENT_TYPE, TextFormat.CONTENT_TYPE_004).body(response + targetUpMetric);
    }
}
