package com.zhengtianbao.jsonexporter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhengtianbao.jsonexporter.dto.ErrorResponse;
import com.zhengtianbao.jsonexporter.dto.MetricResponse;
import com.zhengtianbao.jsonexporter.dto.MetricResult;
import com.zhengtianbao.jsonexporter.service.MetricService;

@CrossOrigin
@RestController
@RequestMapping("/backend/exporter")
public class MetricController {

	@Autowired
	MetricService metricService;

	@GetMapping("/server/{serverId}/metrics")
	public ResponseEntity<?> getServerMetrics(@PathVariable Long serverId) {
		MetricResult result = metricService.getMetricByServerId(serverId);

		if (result.hasError()) {
			return ResponseEntity.badRequest().body(new ErrorResponse(result.getError()));
		} else {
			MetricResponse response = new MetricResponse(result.getMetric());
			return ResponseEntity.ok().header("Content-Type", "text/plain; version=0.0.4;charset=utf-8")
					.body(response.getPlainText());
		}
	}

}
