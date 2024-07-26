package com.zhengtianbao.jsonexporter.metrics;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class JsonexporterMetrics {

	private final MeterRegistry meterRegistry;

	public JsonexporterMetrics(MeterRegistry meterRegistry) {
		this.meterRegistry = meterRegistry;
	}

	public void incrementFetchFailedCounter(String server) {
		Counter.builder("fetch_failed_total")
				.description("Total number of fetch failed")
				.tag("server", server)
				.register(meterRegistry)
				.increment();
	}

	public void incrementJavaScriptExecutionFailedCounter(String server) {
		Counter.builder("javascript_execution_failed_total")
				.description("Total number of javascript execution failed")
				.tag("server", server)
				.register(meterRegistry)
				.increment();
	}

	public void incrementExtractFailedCounter(String server) {
		Counter.builder("extract_failed_total")
				.description("Total number of extract failed")
				.tag("server", server)
				.register(meterRegistry)
				.increment();
	}

}
