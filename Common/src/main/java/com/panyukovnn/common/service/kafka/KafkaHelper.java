package com.panyukovnn.common.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class KafkaHelper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public <T> T deserialize(String request, Class<T> clazz) throws JsonProcessingException {
        Map<String, Object> mapRequest = objectMapper.readValue(request, Map.class);

        return objectMapper.convertValue(mapRequest, clazz);
    }
}
