package org.union.promoter.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.union.common.service.loadingstrategy.LoadingStrategyType;
import org.union.common.service.publishingstrategy.PostDefiningStrategyType;
import org.union.common.service.publishingstrategy.PublishingStrategyType;
import org.union.promoter.service.loadingstrategy.LoadingStrategy;
import org.union.promoter.service.publishingstrategy.PublishingStrategy;
import org.union.promoter.service.publishingstrategy.postdefiningstrategy.PostDefiningStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.union.common.Constants.*;

/**
 * Resolver to get strategy by type
 */
@Service
@RequiredArgsConstructor
public class StrategyFactory {

    private final Logger logger = LoggerFactory.getLogger(StrategyFactory.class);
    private final Map<LoadingStrategyType, LoadingStrategy> loadingStrategyMap = new HashMap<>();
    private final Map<PublishingStrategyType, PublishingStrategy> publishingStrategyMap = new HashMap<>();
    private final Map<PostDefiningStrategyType, PostDefiningStrategy> postDefiningStrategyMap = new HashMap<>();

    @Autowired
    public StrategyFactory(Set<LoadingStrategy> loadingStrategies,
                           Set<PublishingStrategy> publishingStrategies,
                           Set<PostDefiningStrategy> postDefiningStrategies) {
        loadingStrategies.forEach(strategy -> loadingStrategyMap.put(strategy.getType(), strategy));
        publishingStrategies.forEach(strategy -> publishingStrategyMap.put(strategy.getType(), strategy));
        postDefiningStrategies.forEach(strategy -> postDefiningStrategyMap.put(strategy.getType(), strategy));
    }

    /**
     * Returns loading strategy by type
     *
     * @param strategyType type of strategy
     * @return loading strategy
     */
    public LoadingStrategy getLoadingStrategy(LoadingStrategyType strategyType) {
        try {
            return loadingStrategyMap.get(strategyType);
        } catch (Exception e) {
            logger.error(LOADING_STRATEGY_RESOLVING_ERROR_MSG);

            throw e;
        }
    }

    /**
     * Returns publishing strategy by type
     *
     * @param strategyType type of strategy
     * @return publishing strategy
     */
    public PublishingStrategy getPublishingStrategy(PublishingStrategyType strategyType) {
        try {
            return publishingStrategyMap.get(strategyType);
        } catch (Exception e) {
            logger.error(PUBLISHING_STRATEGY_RESOLVING_ERROR_MSG);

            throw e;
        }
    }

    /**
     * Returns post defining strategy by type
     *
     * @param strategyType type of strategy
     * @return publishing strategy
     */
    public PostDefiningStrategy getPostDefiningStrategy(PostDefiningStrategyType strategyType) {
        try {
            return postDefiningStrategyMap.get(strategyType);
        } catch (Exception e) {
            logger.error(POST_DEFINING_STRATEGY_RESOLVING_ERROR_MSG);

            throw e;
        }
    }
}
