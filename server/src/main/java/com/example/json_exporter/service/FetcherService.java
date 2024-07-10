package com.example.json_exporter.service;

import com.example.json_exporter.pojo.Header;
import com.example.json_exporter.pojo.Server;

import java.util.ArrayList;

public interface FetcherService {
    String fetch(Server server, ArrayList<Header> headers);
}
