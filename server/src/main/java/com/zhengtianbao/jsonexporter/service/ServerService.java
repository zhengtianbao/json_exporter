package com.zhengtianbao.jsonexporter.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zhengtianbao.jsonexporter.model.Header;
import com.zhengtianbao.jsonexporter.model.Server;
import com.zhengtianbao.jsonexporter.repository.HeaderRepository;
import com.zhengtianbao.jsonexporter.repository.ServerRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ServerService {

	@Autowired
	ServerRepository serverRepository;

	@Autowired
	HeaderRepository headerRepository;

	public List<Server> getDetailServers() {
		return serverRepository.findAll();
	}

	public Server getDetailServerById(Long id) {
		return serverRepository.findById(id).orElse(null);
	}

	@Transactional
	public Server createServer(Server server) {
		server.getHeaderList().forEach(header -> header.setServer(server));
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

					// Handle header updates
					List<Header> newHeaders = updatedServer.getHeaderList();
					List<Header> existingHeaders = server.getHeaderList();
					List<Header> headersToRemove = existingHeaders.stream()
							.filter(
									header -> newHeaders.stream().noneMatch(newHeader -> newHeader.getValue().equals(header.getValue())))
							.collect(Collectors.toList());
					headersToRemove.forEach(header -> {
						header.setServer(null);
						existingHeaders.remove(header);
						headerRepository.delete(header);
					});
					newHeaders.forEach(header -> {
						if (existingHeaders.stream()
								.noneMatch(existingHeader -> existingHeader.getValue().equals(header.getValue()))) {
							header.setServer(server);
							existingHeaders.add(header);
						}
					});
					server.setHeaderList(existingHeaders);
					return serverRepository.save(server);
				})
				.orElseThrow(() -> new EntityNotFoundException("Server not found with id " + id));
	}

	public void deleteServer(Long id) {
		if (serverRepository.existsById(id)) {
			serverRepository.deleteById(id);
		} else {
			throw new EntityNotFoundException("Server not found with id " + id);
		}
	}
}
