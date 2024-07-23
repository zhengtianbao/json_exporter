package com.zhengtianbao.jsonexporter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zhengtianbao.jsonexporter.service.MetricService;

@CrossOrigin
@RestController
@RequestMapping("/backend/exporter")
public class MetricController {

	@Autowired
	MetricService metricService;

	@GetMapping("/metrics")
	public ResponseEntity<String> metrics(@RequestParam Long serverId) {
		String response = metricService.getMetricByServerId(serverId);
		return ResponseEntity.ok().header("Content-Type", "text/plain; version=0.0.4; charset=utf-8").body(response);
	}
}
