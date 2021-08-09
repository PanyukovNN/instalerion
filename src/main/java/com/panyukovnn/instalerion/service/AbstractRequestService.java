package com.panyukovnn.instalerion.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.panyukovnn.instalerion.exception.RequestException;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractRequestService {

    public <T> ResponseEntity<T> getBackForEntity(String link, Class<T> clazz) {
        try {
            RestTemplate restTemplate = createRestTemplate();

            return restTemplate.getForEntity(formatUri(link), clazz);
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    public <T> T getBackForObject(String link, Class<T> clazz) {
        try {
            RestTemplate restTemplate = createRestTemplate();

            return restTemplate.getForObject(formatUri(link), clazz);
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    public <T> ResponseEntity<T> postBackForEntity(String link, List<Object> objectList, Class<T> clazz) {
        try {
            HttpEntity<MultiValueMap<String, String>> entity = createHttpEntityFromObject(objectList);

            RestTemplate restTemplate = createRestTemplate();

            return restTemplate.postForEntity(formatUri(link), entity, clazz);
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    public <T> T postBackForObject(String link, MultiValueMap<String, String> multiMap, Class<T> clazz) {
        try {
            HttpEntity<MultiValueMap<String, String>> entity = createHttpEntityFromMultiMap(multiMap);

            RestTemplate restTemplate = createRestTemplate();

            return restTemplate.postForObject(formatUri(link), entity, clazz);
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    public <T> T postBackForObject(String link, List<Object> objectList, Class<T> clazz) {
        try {
            HttpEntity<MultiValueMap<String, String>> entity = createHttpEntityFromObject(objectList);

            RestTemplate restTemplate = createRestTemplate();

            return restTemplate.postForObject(formatUri(link), entity, clazz);
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

//    public <T> BackResponse<T> postBackForBackResponse(String link, List<Object> objectList) {
//        try {
//            HttpEntity<MultiValueMap<String, String>> entity = createHttpEntityFromObject(objectList);
//
//            RestTemplate restTemplate = createRestTemplate();
//
//            ResponseEntity<BackResponse<T>> response = restTemplate.exchange(
//                    formatUri(link),
//                    HttpMethod.POST,
//                    entity,
//                    new ParameterizedTypeReference<BackResponse<T>>() {});
//
//            return response.getBody();
//        } catch (Exception e) {
//            throw new RequestException(e);
//        }
//    }

    public String formatUri(String link) {
        return "http://" + getHostname() + link;
    };

    public abstract String getHostname();

    private RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return restTemplate;
    }

    private HttpEntity<MultiValueMap<String, String>> createHttpEntityFromObject(List<Object> objectList) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> map = new HashMap<>();

        for (Object object : objectList) {
            Map<String, String> mappedObject = objectMapper.convertValue(object, new TypeReference<Map<String, String>>() {});

            map.putAll(mappedObject);
        }

        parameters.setAll(map);

        return createHttpEntityFromMultiMap(parameters);
    }

    private HttpEntity<MultiValueMap<String, String>> createHttpEntityFromMultiMap(MultiValueMap<String, String> multiMap) {
        HttpHeaders headers = createHttpHeaders();

        return new HttpEntity<>(multiMap, headers);
    }

    private HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return headers;
    }
}
