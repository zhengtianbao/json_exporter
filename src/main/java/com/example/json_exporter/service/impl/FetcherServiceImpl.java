package com.example.json_exporter.service.impl;

import com.example.json_exporter.service.FetcherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class FetcherServiceImpl implements FetcherService {

    private final RestTemplate restTemplate;

    public FetcherServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public String fetch(String url) {
        return this.restTemplate.getForObject(url, String.class);
    }
}
