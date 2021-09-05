package org.union.promoter.requestprocessor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.union.common.model.request.LoadingRequest;
import org.union.common.service.UseContext;
import org.union.promoter.service.loadingstrategy.LoadingStrategy;
import org.union.promoter.service.StrategyResolver;

/**
 * Request processor for posts loading
 */
@Service
@RequiredArgsConstructor
public class LoaderRequestProcessor {

    private final StrategyResolver strategyResolver;

    /**
     * Load posts from consuming channels to database and cloud
     *
     * @param request loading request
     * @throws Exception exception
     */
    @Transactional
    public void processLoadingRequest(LoadingRequest request) throws Exception {
        UseContext.checkInUse(request.getProducingChannelId());

        try {
            UseContext.setInUse(request.getProducingChannelId());

            LoadingStrategy strategy = strategyResolver.getLoadingStrategy(request.getStrategyType());

            strategy.load(request);
        } finally {
            UseContext.release(request.getProducingChannelId());
        }
    }
}
