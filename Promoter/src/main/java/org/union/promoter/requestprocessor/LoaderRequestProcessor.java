package org.union.promoter.requestprocessor;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.union.common.model.request.LoadingRequest;
import org.union.common.service.UseContext;
import org.union.promoter.service.loadingstrategy.LoadingStrategy;
import org.union.promoter.service.StrategyResolver;

import static org.union.common.Constants.PRODUCING_CHANNEL_IS_BUSY_MSG;

/**
 * Request processor for posts loading
 */
@Service
@RequiredArgsConstructor
public class LoaderRequestProcessor {

    private final Logger logger = LoggerFactory.getLogger(LoaderRequestProcessor.class);
    private final StrategyResolver strategyResolver;

    /**
     * Load posts from consuming channels to database and cloud
     *
     * @param request loading request
     * @throws Exception exception
     */
    @Transactional
    public void processLoadingRequest(LoadingRequest request) throws Exception {
        try {
            if (UseContext.checkInUseAndSet(request.getProducingChannelId())) {
                logger.info(String.format(PRODUCING_CHANNEL_IS_BUSY_MSG, request.getProducingChannelId()));

                return;
            }

            LoadingStrategy strategy = strategyResolver.getLoadingStrategy(request.getStrategyType());

            strategy.load(request);
        } finally {
            UseContext.release(request.getProducingChannelId());
        }
    }
}
