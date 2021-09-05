package org.union.promoter.service.loadingstrategy;

import org.union.common.model.request.LoadingRequest;

public interface LoadingStrategy {

    void load(LoadingRequest request) throws Exception;
}
