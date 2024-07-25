package com.zhengtianbao.jsonexporter.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhengtianbao.jsonexporter.dto.MetricResult;
import com.zhengtianbao.jsonexporter.exception.custom.JavaScriptExecutionException;
import com.zhengtianbao.jsonexporter.exception.custom.MetricsFetchException;
import com.zhengtianbao.jsonexporter.model.Preprocess;
import com.zhengtianbao.jsonexporter.repository.ServerRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class MetricService {

	private static final Logger logger = LoggerFactory.getLogger(MetricService.class);

	@Autowired
	ServerRepository serverRepository;

	public MetricResult getMetricByServerId(Long id) {
		return serverRepository.findById(id).map(server -> {
			try {
				String originResponse = server.fetchMetrics();
				if (server.getPreprocessSet() != null) {
					for (Preprocess preprocess : server.getPreprocessSet()) {
						originResponse = preprocess.apply(originResponse);
					}
				}
				return new MetricResult(originResponse, null);
			} catch (JavaScriptExecutionException e) {
				logger.error("Server {} failed to execute JavaScript: {}", id, e.getMessage());
				return new MetricResult(null, "Failed to preprocess metrics");
			} catch (MetricsFetchException e) {
				logger.error("Server {} failed to fetch metrics: {}", id, e.getMessage());
				return new MetricResult(null, "Failed to fetch metrics");
			}
		}).orElseThrow(() -> new EntityNotFoundException("Server not found with id " + id));
	}

}
