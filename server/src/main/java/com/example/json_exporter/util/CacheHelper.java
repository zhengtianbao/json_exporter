package com.example.json_exporter.util;

import com.example.json_exporter.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class CacheHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheHelper.class);
    public static Map<String, String> codeMap = new HashMap<>();

    @Autowired
    private LoginService loginService;

    @PostConstruct
    public void updateToken() {
        try {
            LOGGER.info("start to update token...");
            String token = loginService.getToken();
            codeMap.put("token", token);
            LOGGER.info("new token: {}", token);
            LOGGER.info("update token finished.");
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
    }

    @Scheduled(cron = "0 0 * * * *")
    public void start() {
        updateToken();
    }
}
