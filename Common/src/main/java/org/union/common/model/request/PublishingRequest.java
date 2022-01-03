package org.union.common.model.request;

import lombok.*;
import org.union.common.model.post.PublicationType;
import org.union.common.service.publishingstrategy.PostSortingStrategyType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static org.union.common.Constants.*;

/**
 * Request to run publisher module
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PublishingRequest extends KafkaRequest implements ProducingChannelRequest {

    @NotBlank(message = PRODUCING_CHANNEL_NULL_ID_ERROR_MSG)
    private String producingChannelId;

    @NotNull(message = LOADING_STRATEGY_TYPE_NULL_ID_ERROR_MSG)
    private PublicationType publicationType;

    @NotNull(message = POST_SORTING_STRATEGY_NULL_ERROR_MSG)
    private PostSortingStrategyType postSortingStrategyType;
}
