package org.union.instalerion.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.union.common.model.request.KafkaRequest;
import org.union.common.service.kafka.KafkaHelper;

import java.util.Map;

@RequiredArgsConstructor
public abstract class KafkaSender {

    private final KafkaHelper kafkaHelper;
    private final KafkaTemplate<String, Map<String, Object>> kafkaTemplate;

    /**
     * Send request to kafka topic
     *
     * @param request kafka request
     */
    public void send(KafkaRequest request) {
        Map<String, Object> mapRequest = kafkaHelper.serialize(request);

        ListenableFuture<SendResult<String, Map<String, Object>>> future =
                kafkaTemplate.send(getTopicName(), mapRequest);

        future.addCallback(getCallback(mapRequest));
    }

    protected abstract String getTopicName();

    protected abstract ListenableFutureCallback<SendResult<String, Map<String, Object>>> getCallback(Map<String, Object> mapRequest);
}
