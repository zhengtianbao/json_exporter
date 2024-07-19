package com.zhengtianbao.jsonexporter.service;

import java.util.List;

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
		server.getHeaderSet().forEach(header -> header.setServer(server));
		server.getPreprocessSet().forEach(preprocess -> preprocess.setServer(server));
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
					updatedServer.getHeaderSet().forEach(header -> header.setServer(server));
					server.getHeaderSet().clear();
					server.getHeaderSet().addAll(updatedServer.getHeaderSet());
					updatedServer.getPreprocessSet().forEach(preprocess -> preprocess.setServer(server));
					server.getPreprocessSet().clear();
					server.getPreprocessSet().addAll(updatedServer.getPreprocessSet());
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
