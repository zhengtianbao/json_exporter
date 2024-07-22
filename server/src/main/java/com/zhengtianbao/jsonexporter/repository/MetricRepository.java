package com.zhengtianbao.jsonexporter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.zhengtianbao.jsonexporter.model.Metric;

@NoRepositoryBean
public interface MetricRepository<T extends Metric> extends JpaRepository<T, Long> {

}
