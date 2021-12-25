package org.union.common.model.request;

import lombok.*;
import org.union.common.model.post.PublicationType;
import org.union.common.service.publishingstrategy.PostSortingStrategyType;

/**
 * Request to run publisher module
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PublishingRequest extends KafkaRequest implements ProducingChannelRequest {

    private String producingChannelId;
    private PublicationType publicationType;
    private PostSortingStrategyType postSortingStrategyType;
}
