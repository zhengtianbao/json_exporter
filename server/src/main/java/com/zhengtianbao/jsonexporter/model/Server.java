package com.zhengtianbao.jsonexporter.model;

import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

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

	// TODO: when request error occurs, add failed _request_count metric
	public String fetchMetrics() throws RuntimeException {
		HttpHeaders headers = new HttpHeaders();
		addHeadersToRequest(headers);
		HttpMethod method = HttpMethod.valueOf(getMethod());
		HttpEntity<String> request = new HttpEntity<>(getBody(), headers);
		RestTemplate restTemplate = new RestTemplate();
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(
					url,
					method,
					request,
					String.class);
			return responseEntity.getBody();
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			throw new RuntimeException("HTTP request failed", e);
		} catch (ResourceAccessException e) {
			throw new RuntimeException("Network error occurred", e);
		} catch (RestClientException e) {
			throw new RuntimeException("REST client error", e);
		}
	}

	private void addHeadersToRequest(HttpHeaders headers) {
		Optional.ofNullable(getHeaderSet())
				.ifPresent(hs -> hs.forEach(header -> headers.add(header.getHeaderKey(), header.getHeaderValue())));
	}
}
