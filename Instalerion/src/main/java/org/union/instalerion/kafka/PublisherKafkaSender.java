package org.union.instalerion.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.union.common.service.kafka.KafkaHelper;
import org.union.common.service.kafka.LoaderCallback;
import org.union.common.service.kafka.PublisherCallback;

import java.util.Map;

@Service
public class PublisherKafkaSender extends KafkaSender {

    @Value("${kafka.publisher.topic}")
    private String topicName;

    public PublisherKafkaSender(KafkaHelper kafkaHelper, KafkaTemplate<String, Map<String, Object>> kafkaTemplate) {
        super(kafkaHelper, kafkaTemplate);
    }

    @Override
    protected String getTopicName() {
        return topicName;
    }

    @Override
    protected ListenableFutureCallback<SendResult<String, Map<String, Object>>> getCallback(Map<String, Object> mapRequest) {
        return new PublisherCallback(mapRequest);
    }
}
