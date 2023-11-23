package com.example.json_exporter.service.impl;

import com.example.json_exporter.pojo.Header;
import com.example.json_exporter.service.FetcherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

@Slf4j
@Service
public class FetcherServiceImpl implements FetcherService {

    private final RestTemplate restTemplate;

    public FetcherServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public String fetch(String url, ArrayList<Header> headers) {
        HttpHeaders hs = new HttpHeaders();
        for (int i=0; i<headers.size(); i++) {
            String[] parts = headers.get(i).getValue().split(":");
            hs.set(parts[0].trim(), parts[1].trim());
        }
        HttpEntity request = new HttpEntity(hs);
        ResponseEntity<String> response = this.restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                String.class
        );
        return response.getBody();
    }
}
