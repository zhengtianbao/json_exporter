package com.zhengtianbao.jsonexporter.model;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "server")
@NamedEntityGraph(name = "graph.detail.server", attributeNodes = { @NamedAttributeNode("headerSet"),
		@NamedAttributeNode("preprocessSet") })
public class Server {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "url")
	private String url;

	@Column(name = "method")
	private String method;

	@Column(name = "body")
	private String body;

	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER, mappedBy = "server")
	private Set<Header> headerSet;

	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER, mappedBy = "server")
	private Set<Preprocess> preprocessSet;

	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER, mappedBy = "server")
	private Set<Metric> metricSet;

	protected Server() {
	}

	public Server(String name, String url, String method, String body) {
		this.name = name;
		this.url = url;
		this.method = method;
		this.body = body;
	}

	@Override
	public String toString() {
		return String.format("Server [id=%s, url=%s, method=%s, body=%s]", id, url, method, body);
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Set<Header> getHeaderSet() {
		return headerSet;
	}

	public void setHeaderSet(Set<Header> headerSet) {
		this.headerSet = headerSet;
	}

	public Set<Preprocess> getPreprocessSet() {
		return preprocessSet;
	}

	public void setPreprocessSet(Set<Preprocess> preprocessSet) {
		this.preprocessSet = preprocessSet;
	}

	public Set<Metric> getMetricSet() {
		return metricSet;
	}

	public void setMetricSet(Set<Metric> metricSet) {
		this.metricSet = metricSet;
	}

}
