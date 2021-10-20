package ru.pixelmongo.pixelmongo.services.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import ru.pixelmongo.pixelmongo.model.dto.vk.VKResponse;
import ru.pixelmongo.pixelmongo.model.dto.vk.VKWall;
import ru.pixelmongo.pixelmongo.services.VKService;

public class VKServiceImpl implements VKService {

    @Autowired
    private RestTemplate rest;

    private final String url, key, version;
    private long newsUpdatePeriod = 0;
    private int maxNews = 15;

    private Map<String, String> groups = Collections.emptyMap();

    private Map<String, VKWall> walls = new ConcurrentHashMap<>();

    private VKWall emptyWall = new VKWall();

    private long lastUpdate;

    private Timer wallUpdateTimer;

    private boolean shutdown = false;

    public VKServiceImpl(String url, String key, String version) {
        if(url.endsWith("/")) url = url.substring(0, url.length()-1);
        this.url = url;
        this.key = key;
        this.version = version;
    }

    @PostConstruct
    public void init() {
        if(newsUpdatePeriod == 0)
            throw new IllegalStateException("News update period is not specified");
        wallUpdateTimer = new Timer("vk-service-news-updator");
        wallUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateWalls();
            }
        }, 5000, newsUpdatePeriod);
        LOGGER.info("VK walls updating started");
    }

    @PreDestroy
    public void stop() {
        shutdown = true;
        wallUpdateTimer.cancel();
        LOGGER.info("VK walls updating stopped");
    }

    public void setGroups(Map<String, String> groups) {
        this.groups = groups;
    }

    public void setNewsUpdatePeriod(long updatePeriod) {
        if(updatePeriod < 60000)
            throw new IllegalArgumentException("VK news update period can't be less than 1 minute");
        this.newsUpdatePeriod = updatePeriod;
    }

    public void setMaxNews(int maxNews) {
        this.maxNews = maxNews;
    }

    @Override
    public long getLastUpdate() {
        return lastUpdate;
    }

    @Override
    public <T> ResponseEntity<T> apiRequest(String method, Class<T> responseType, Map<String, ?> params) {
        RequestEntity<T> re = prepareRequest(method, params);
        return rest.exchange(re, responseType);
    }

    @Override
    public <T> ResponseEntity<T> apiRequest(String method, ParameterizedTypeReference<T> responseType, Map<String, ?> params) {
        RequestEntity<T> re = prepareRequest(method, params);
        return rest.exchange(re, responseType);
    }

    private <T> RequestEntity<T> prepareRequest(String method, Map<String, ?> params){
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url+"/"+method);
        params.forEach(builder::queryParam);
        builder.queryParam("access_token", key);
        builder.queryParam("v", version);
        return new RequestEntity<T>(HttpMethod.GET, builder.build().toUri());
    }

    @Override
    public VKResponse<VKWall> apiRequestWall(String groupId, int count){
        Map<String, Object> params = new HashMap<>();
        params.put("owner_id", "-"+groupId);
        params.put("count", count);
        ResponseEntity<VKResponse<VKWall>> response = apiRequest("wall.get",
                new ParameterizedTypeReference<VKResponse<VKWall>>() {}, params);
        VKResponse<VKWall> result =  response.getBody();
        if(result == null) {
            LOGGER.warn("wall.get for group id "+groupId+" failed. HTTP status: "
                    + response.getStatusCodeValue());
        }
        return result;
    }

    @Override
    public void updateWalls() {
        for(String groupName : this.groups.keySet())
            if(!shutdown)
                updateWall(groupName);
    }

    @Override
    public void updateWall(String groupName) {
        String groupId = groups.get(groupName);
        if(groupId == null) return;
        VKResponse<VKWall> response;
        try {
            response = apiRequestWall(groupId, maxNews);
        }catch (Exception e) {
            LOGGER.error("Exception while updating wall "+groupName);
            LOGGER.catching(e);
            return;
        }
        if(response != null) {
            if(response.getResponse() != null) {
                walls.put(groupName, response.getResponse());
                lastUpdate = System.currentTimeMillis();
            }else if(response.getError() != null) {
                LOGGER.warn("Errow while updating wall "+groupName+". Code: "
                        +response.getError().getCode()+". Message :"+response.getError().getMessage());
            }
        }
    }

    @Override
    public VKWall getWall(String groupName) {
        return walls.getOrDefault(groupName, emptyWall);
    }



}
