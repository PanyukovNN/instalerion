package org.union.common.model.request;

import lombok.*;
import org.union.common.service.publishingstrategy.PublishingStrategyType;

/**
 * Request to run publisher module
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PublishingRequest extends KafkaRequest {

    private String producingChannelId;
    private PublishingStrategyType strategyType;
}
