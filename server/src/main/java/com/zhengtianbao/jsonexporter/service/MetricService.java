package com.zhengtianbao.jsonexporter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhengtianbao.jsonexporter.repository.ServerRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class MetricService {

	@Autowired
	ServerRepository serverRepository;

	public String getMetricByServerId(Long serverId) {
		return serverRepository.findById(serverId).map(server -> {
			String originResponse;
			try {
				originResponse = server.fetchRequest();
			} catch (RuntimeException e) {
				// when server is not reachable use empty response
				originResponse = "";
			}
			return originResponse;
		}).orElseThrow(() -> new EntityNotFoundException("Server not found with id " + serverId));
	}

}
