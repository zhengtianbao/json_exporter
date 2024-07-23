package com.zhengtianbao.jsonexporter.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhengtianbao.jsonexporter.model.Server;
import com.zhengtianbao.jsonexporter.service.ServerService;

@CrossOrigin
@RestController
@RequestMapping("/backend/exporter")
public class ServerController {

	@Autowired
	ServerService serverService;

	@GetMapping("/servers")
	public ResponseEntity<List<Server>> getServers() {
		List<Server> servers = serverService.getDetailServers();
		if (servers.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(servers, HttpStatus.OK);
	}

	@GetMapping("/servers/{id}")
	public ResponseEntity<Server> getServerById(@PathVariable Long id) {
		Server server = serverService.getDetailServerById(id);
		if (server == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(server, HttpStatus.OK);
	}

	@PostMapping("/servers")
	public ResponseEntity<Server> createServer(@RequestBody Server server) {
		return new ResponseEntity<>(serverService.createServer(server), HttpStatus.CREATED);
	}

	@PutMapping("/servers/{id}")
	public ResponseEntity<Server> updateServer(@PathVariable Long id, @RequestBody Server updatedServer) {
		return new ResponseEntity<>(serverService.updateServer(id, updatedServer), HttpStatus.OK);
	}

	@DeleteMapping("/servers/{id}")
	public ResponseEntity<HttpStatus> deleteServer(@PathVariable Long id) {
		serverService.deleteServer(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
