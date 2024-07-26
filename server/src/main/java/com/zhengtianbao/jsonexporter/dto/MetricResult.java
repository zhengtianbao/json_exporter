package com.zhengtianbao.jsonexporter.dto;

public class MetricResult {

	private String metric;
	private String error;

	public MetricResult(String metric, String error) {
		this.metric = metric;
		this.error = error;
	}

	public String getMetric() {
		return metric;
	}

	public String getError() {
		return error;
	}

	public boolean hasError() {
		return error != null;
	}

}
