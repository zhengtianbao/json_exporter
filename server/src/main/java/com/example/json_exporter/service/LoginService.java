package com.example.json_exporter.service;

import com.example.json_exporter.pojo.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class LoginService {

    private final RestTemplate restTemplate;

    @Value("${mecury.tokenUrl}")
    private String tokenUrl;

    @Value("${mecury.tokenClientID}")
    private String tokenClientID;

    @Value("${mecury.tokenSecret}")
    private String tokenSecret;

    public LoginService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public String getToken() {
        String url = tokenUrl;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("clientId", tokenClientID);
        map.add("secret", tokenSecret);
        HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<TokenResponse> response = this.restTemplate.postForEntity(url, entity, TokenResponse.class);
        return response.getBody().getData();
    }
}
