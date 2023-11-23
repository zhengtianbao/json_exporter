package com.example.json_exporter.service;

import com.example.json_exporter.pojo.Header;

import java.util.ArrayList;

public interface FetcherService {
    String fetch(String url, ArrayList<Header> headers);
}
