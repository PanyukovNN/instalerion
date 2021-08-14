package com.panyukovnn.instalerion.kafka;

public interface KafkaSender {

    /**
     * Send request to kafka topic
     *
     * @param producingChannelId producing channel id
     */
    void send(String producingChannelId);
}