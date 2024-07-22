package com.zhengtianbao.jsonexporter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zhengtianbao.jsonexporter.model.Server;
import com.zhengtianbao.jsonexporter.repository.ServerRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ServerService {

	@Autowired
	ServerRepository serverRepository;

	public List<Server> getDetailServers() {
		return serverRepository.findAll();
	}

	public Server getDetailServerById(Long id) {
		return serverRepository.findById(id).orElse(null);
	}

	@Transactional
	public Server createServer(Server server) {
		Optional.ofNullable(server.getHeaderSet())
				.ifPresent(headers -> headers.forEach(header -> header.setServer(server)));
		Optional.ofNullable(server.getPreprocessSet())
				.ifPresent(preprocesses -> preprocesses.forEach(preprocess -> preprocess.setServer(server)));
		Optional.ofNullable(server.getMetricSet())
				.ifPresent(metrics -> metrics.forEach(metric -> {
					metric.setServer(server);
					Optional.ofNullable(metric.getLabelSet())
							.ifPresent(labels -> labels.forEach(label -> label.setMetric(metric)));
				}));
		return serverRepository.save(server);
	}

	@Transactional
	public Server updateServer(Long id, Server updatedServer) {
		return serverRepository.findById(id)
				.map(server -> {
					server.setName(updatedServer.getName());
					server.setUrl(updatedServer.getUrl());
					server.setMethod(updatedServer.getMethod());
					server.setBody(updatedServer.getBody());
					Optional.ofNullable(updatedServer.getHeaderSet()).ifPresent(headers -> {
						server.getHeaderSet().clear();
						headers.forEach(header -> header.setServer(server));
						server.getHeaderSet().addAll(headers);
					});
					Optional.ofNullable(updatedServer.getPreprocessSet()).ifPresent(preprocesses -> {
						server.getPreprocessSet().clear();
						preprocesses.forEach(preprocess -> preprocess.setServer(server));
						server.getPreprocessSet().addAll(preprocesses);
					});
					Optional.ofNullable(updatedServer.getMetricSet()).ifPresent(metrics -> {
						server.getMetricSet().clear();
						metrics.forEach(metric -> {
							metric.setServer(server);
							Optional.ofNullable(metric.getLabelSet()).ifPresent(labels -> {
								labels.forEach(label -> label.setMetric(metric));
								metric.getLabelSet().addAll(labels);
							});
						});
						server.getMetricSet().addAll(metrics);
					});
					return serverRepository.save(server);
				})
				.orElseThrow(() -> new EntityNotFoundException("Server not found with id " + id));
	}

	@Transactional
	public void deleteServer(Long id) {
		if (serverRepository.existsById(id)) {
			serverRepository.deleteById(id);
		} else {
			throw new EntityNotFoundException("Server not found with id " + id);
		}
	}
}
