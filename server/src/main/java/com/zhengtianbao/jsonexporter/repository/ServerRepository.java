package com.zhengtianbao.jsonexporter.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import com.zhengtianbao.jsonexporter.model.Server;

public interface ServerRepository extends JpaRepository<Server, Long> {

	@NonNull
	@EntityGraph(value = "graph.detail.server")
	List<Server> findAll();

	@NonNull
	@EntityGraph(value = "graph.detail.server")
	Optional<Server> findById(@NonNull Long id);
}
