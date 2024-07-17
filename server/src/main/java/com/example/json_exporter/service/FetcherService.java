package com.example.json_exporter.service;

import java.util.ArrayList;

import com.example.json_exporter.pojo.Header;
import com.example.json_exporter.pojo.Server;

public interface FetcherService {
    String fetch(Server server, ArrayList<Header> headers) throws Exception;
}
