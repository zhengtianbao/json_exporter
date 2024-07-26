package com.zhengtianbao.jsonexporter.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.zhengtianbao.jsonexporter.dto.MetricResult;
import com.zhengtianbao.jsonexporter.exception.custom.JavaScriptExecutionException;
import com.zhengtianbao.jsonexporter.exception.custom.JsonPathExtractionException;
import com.zhengtianbao.jsonexporter.exception.custom.MetricsFetchException;
import com.zhengtianbao.jsonexporter.metrics.JsonexporterMetrics;
import com.zhengtianbao.jsonexporter.model.Label;
import com.zhengtianbao.jsonexporter.model.Metric;
import com.zhengtianbao.jsonexporter.model.MultiValueMetric;
import com.zhengtianbao.jsonexporter.model.Preprocess;
import com.zhengtianbao.jsonexporter.model.Server;
import com.zhengtianbao.jsonexporter.model.SingleValueMetric;
import com.zhengtianbao.jsonexporter.repository.ServerRepository;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Tag;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import jakarta.persistence.EntityNotFoundException;

@Service
public class MetricService {

	private static final Logger logger = LoggerFactory.getLogger(MetricService.class);
	private final ServerRepository serverRepository;
	private final JsonexporterMetrics jsonexporterMetrics;

	public MetricService(ServerRepository serverRepository, JsonexporterMetrics jsonexporterMetrics) {
		this.serverRepository = serverRepository;
		this.jsonexporterMetrics = jsonexporterMetrics;
	}

	public MetricResult getMetricByServerId(Long id) {
		return serverRepository.findById(id).map(server -> {
			try {
				String originResponse = server.fetchMetrics();
				if (server.getPreprocessSet() != null) {
					for (Preprocess preprocess : server.getPreprocessSet()) {
						originResponse = preprocess.apply(originResponse);
					}
				}
				return new MetricResult(collectMetrics(server, originResponse), null);
			} catch (JavaScriptExecutionException e) {
				logger.error("Server {} failed to execute JavaScript: {}", id, e.getMessage());
				jsonexporterMetrics.incrementJavaScriptExecutionFailedCounter(server.getName());
				return new MetricResult(null, "Failed to preprocess metrics");
			} catch (MetricsFetchException e) {
				logger.error("Server {} failed to fetch metrics: {}", id, e.getMessage());
				jsonexporterMetrics.incrementFetchFailedCounter(server.getName());
				return new MetricResult(null, "Failed to fetch metrics");
			} catch (JsonPathExtractionException e) {
				logger.error("Server {} failed to extract metrics: {}", id, e.getMessage());
				jsonexporterMetrics.incrementExtractFailedCounter(server.getName());
				return new MetricResult(null, "Failed to extract metrics");
			}
		}).orElseThrow(() -> new EntityNotFoundException("Server not found with id " + id));
	}

	private String collectMetrics(Server server, String originResponse) throws JsonPathExtractionException {
		PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
		registry.config().commonTags("target", server.getUrl());

		if (server.getMetricSet() != null) {
			for (Metric metric : server.getMetricSet()) {
				if (metric instanceof SingleValueMetric) {
					String metricValue = extractStringFromJsonString(originResponse,
							((SingleValueMetric) metric).getSingleValuePath());
					List<Tag> tags = new ArrayList<>();
					if (metric.getLabelSet() != null) {
						for (Label label : metric.getLabelSet()) {
							String labelValue = extractStringFromJsonString(originResponse, label.getPath());
							tags.add(Tag.of(label.getName(), labelValue));
						}
					}
					Gauge.builder(metric.getName(), () -> sanitize(metricValue)).description(metric.getHelp()).tags(tags)
							.register(registry);
				} else if (metric instanceof MultiValueMetric) {
					List<String> multiValueObject = extractListFromJsonString(originResponse,
							((MultiValueMetric) metric).getMultiValueObjectPath());
					for (String objectString : multiValueObject) {
						List<Tag> tags = new ArrayList<>();
						if (metric.getLabelSet() != null) {
							for (Label label : metric.getLabelSet()) {
								String labelValue = extractStringFromJsonString(objectString, label.getPath());
								tags.add(Tag.of(label.getName(), labelValue));
							}
						}
						String metricValue = extractStringFromJsonString(objectString,
								((MultiValueMetric) metric).getMultiValueObjectValuePath());
						Gauge.builder(metric.getName(), () -> sanitize(metricValue)).description(metric.getHelp()).tags(tags)
								.register(registry);
					}
				}
				return registry.scrape();
			}
		}
		return "";
	}

	private String extractStringFromJsonString(String jsonString, String jsonPath) throws JsonPathExtractionException {
		ObjectMapper objectMapper = new ObjectMapper();
		Object result;
		try {
			Configuration conf = Configuration.builder().jsonProvider(new JacksonJsonProvider(objectMapper)).build();
			result = JsonPath.using(conf).parse(jsonString).read(jsonPath);
		} catch (JsonPathException e) {
			throw new JsonPathExtractionException("JsonPath not found: " + jsonPath, e);
		}
		if (result == null) {
			throw new JsonPathExtractionException("Result of JsonPath not found: " + jsonPath);
		}
		return result.toString();
	}

	private List<String> extractListFromJsonString(String jsonString, String jsonPath)
			throws JsonPathExtractionException {
		ObjectMapper objectMapper = new ObjectMapper();
		List<Object> result;
		try {
			Configuration conf = Configuration.builder()
					.jsonProvider(new JacksonJsonProvider(objectMapper))
					.build().addOptions(Option.ALWAYS_RETURN_LIST);
			result = JsonPath.using(conf).parse(jsonString).read(jsonPath);
		} catch (JsonPathException e) {
			throw new JsonPathExtractionException("JsonPath not found: " + jsonPath, e);
		}
		if (result == null) {
			throw new JsonPathExtractionException("Result of JsonPath not found: " + jsonPath);
		}
		return result.stream().map(o -> {
			try {
				return objectMapper.writer().writeValueAsString(o);
			} catch (JsonProcessingException e) {
				throw new JsonPathExtractionException("JsonPath failed to serialize: " + jsonPath, e);
			}
		}).toList();
	}

	private Double sanitize(String valueString) {
		if (valueString.startsWith("[")) {
			valueString = valueString.replace("[", "").replace("]", "").replace("\"", "");
		}
		try {
			return Double.parseDouble(valueString);
		} catch (NumberFormatException e) {
			return 0.0;
		}
	}
}
