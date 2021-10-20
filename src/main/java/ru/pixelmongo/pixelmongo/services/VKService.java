package ru.pixelmongo.pixelmongo.services;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

import ru.pixelmongo.pixelmongo.model.dto.vk.VKResponse;
import ru.pixelmongo.pixelmongo.model.dto.vk.VKWall;

public interface VKService {

    public static final Logger LOGGER = LogManager.getLogger(VKService.class);

    public long getLastUpdate();

    public <T> ResponseEntity<T> apiRequest(String method, Class<T> responseType, Map<String, ?> params);

    public <T> ResponseEntity<T> apiRequest(String method, ParameterizedTypeReference<T> responseType, Map<String, ?> params);

    public VKResponse<VKWall> apiRequestWall(String groupId, int count);

    public void updateWalls();

    public void updateWall(String groupName);

    public VKWall getWall(String groupName);

}
