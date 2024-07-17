package com.example.json_exporter.service.impl;

import java.util.ArrayList;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.json_exporter.pojo.Header;
import com.example.json_exporter.pojo.Server;
import com.example.json_exporter.service.FetcherService;
import com.example.json_exporter.util.CacheHelper;

@Service
public class FetcherServiceImpl implements FetcherService {

    private final RestTemplate restTemplate;

    public FetcherServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public String fetch(Server server, ArrayList<Header> headers) throws Exception {
        HttpHeaders hs = new HttpHeaders();
        if (headers.isEmpty()) {
            // 没有自定义header的情况下设置水星服务token
            hs.set("x-client-token", CacheHelper.codeMap.get("token"));
        } else {
            for (int i = 0; i < headers.size(); i++) {
                String[] parts = headers.get(i).getValue().split(":");
                hs.set(parts[0].trim(), parts[1].trim());
            }
        }

        String requestBody = server.getBody();
        ResponseEntity<String> response;
        HttpEntity<String> request = new HttpEntity<>(requestBody, hs);
        try {
            response = this.restTemplate.exchange(
                    server.getUrl(),
                    HttpMethod.valueOf(server.getMethod()),
                    request,
                    String.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return response.getBody();
    }
}
