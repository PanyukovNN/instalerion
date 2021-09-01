package org.union.common.service.publishingstrategy;

import org.union.common.model.request.PublishingRequest;

public interface PublishingStrategy {

    void publish(PublishingRequest request) throws Exception;
}
