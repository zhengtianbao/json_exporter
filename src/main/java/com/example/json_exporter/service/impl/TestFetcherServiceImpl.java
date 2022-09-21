package com.example.json_exporter.service.impl;

import com.example.json_exporter.service.FetcherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TestFetcherServiceImpl implements FetcherService {

    private String testJSON = "{\n" +
            "    \"counter\": 1234,\n" +
            "    \"timestamp\": 1657568506,\n" +
            "    \"values\": [\n" +
            "        {\n" +
            "            \"id\": \"id-A\",\n" +
            "            \"count\": 1,\n" +
            "            \"some_boolean\": true,\n" +
            "            \"state\": \"ACTIVE\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": \"id-B\",\n" +
            "            \"count\": 2,\n" +
            "            \"some_boolean\": true,\n" +
            "            \"state\": \"INACTIVE\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": \"id-C\",\n" +
            "            \"count\": 3,\n" +
            "            \"some_boolean\": false,\n" +
            "            \"state\": \"ACTIVE\"\n" +
            "        }\n" +
            "    ],\n" +
            "    \"location\": \"mars\"\n" +
            "}";
    @Override
    public String fetch(String url) {
        log.debug(url);
        return this.testJSON;
    }
}
