package com.zhengtianbao.jsonexporter.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MULTI")
public class MultiValueMetric extends Metric {

	@Column(name = "multi_value_object_path")
	private String multiValueObjectPath;

	@Column(name = "multi_value_object_value_path")
	private String multiValueObjectValuePath;

	protected MultiValueMetric() {
	}

	public MultiValueMetric(String name, String help, String multiValueObjectPath, String multiValueObjectValuePath) {
		super(name, help);
		this.multiValueObjectPath = multiValueObjectPath;
		this.multiValueObjectValuePath = multiValueObjectValuePath;
	}

	@Override
	public String toString() {
		return String.format("MultiValueMetric [multiValueObjectPath=%s, multiValueObjectValuePath=%s]",
				multiValueObjectPath, multiValueObjectValuePath);
	}

	public String getMultiValueObjectPath() {
		return multiValueObjectPath;
	}

	public void setMultiValueObjectPath(String multiValueObjectPath) {
		this.multiValueObjectPath = multiValueObjectPath;
	}

	public String getMultiValueObjectValuePath() {
		return multiValueObjectValuePath;
	}

	public void setMultiValueObjectValuePath(String multiValueObjectValuePath) {
		this.multiValueObjectValuePath = multiValueObjectValuePath;
	}

}
