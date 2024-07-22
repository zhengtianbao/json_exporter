package com.zhengtianbao.jsonexporter.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SINGLE")
public class SingleValueMetric extends Metric {

	@Column(name = "single_value_path")
	private String singleValuePath;

	protected SingleValueMetric() {
	}

	public SingleValueMetric(String name, String help, String singleValuePath) {
		super(name, help);
		this.singleValuePath = singleValuePath;
	}

	@Override
	public String toString() {
		return String.format("SingleValueMetric [singleValuePath=%s, %s]", singleValuePath, super.toString());
	}

	public String getSingleValuePath() {
		return singleValuePath;
	}

	public void setSingleValuePath(String singleValuePath) {
		this.singleValuePath = singleValuePath;
	}

}
