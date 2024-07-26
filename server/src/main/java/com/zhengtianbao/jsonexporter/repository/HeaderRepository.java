package com.zhengtianbao.jsonexporter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zhengtianbao.jsonexporter.model.Header;

import jakarta.transaction.Transactional;

public interface HeaderRepository extends JpaRepository<Header, Long> {

	List<Header> findByServerId(Long serverId);

	@Transactional
	void deleteByServerId(Long serverId);
}
