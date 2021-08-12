package com.panyukovnn.common.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Util kafka helper class
 */
@Service
@RequiredArgsConstructor
public class KafkaHelper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public <T> T deserialize(String request, Class<T> clazz) throws JsonProcessingException {
        Map<String, Object> mapRequest = objectMapper.readValue(request, Map.class);

        return objectMapper.convertValue(mapRequest, clazz);
    }

    public <T> Map<String, Object> serialize(T object) {
        return objectMapper.convertValue(object, Map.class);
    }
}
