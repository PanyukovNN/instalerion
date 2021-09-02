package org.union.promoter.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.union.common.service.loadingstrategy.LoadingStrategy;
import org.union.common.service.loadingstrategy.LoadingStrategyType;
import org.union.common.service.publishingstrategy.PublishingStrategy;
import org.union.common.service.publishingstrategy.PublishingStrategyType;
import org.union.promoter.kafka.LoaderKafkaListener;
import org.union.promoter.service.loadingstrategy.InstagramPostLoadingStrategy;
import org.union.promoter.service.publishingstrategy.post.InstagramRecentPostPublishingStrategy;
import org.union.promoter.service.publishingstrategy.story.InstagramBaseStoryPublishingStrategy;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static org.union.common.Constants.LOADING_STRATEGY_RESOLVING_ERROR_MSG;
import static org.union.common.Constants.PUBLISHING_STRATEGY_RESOLVING_ERROR_MSG;

/**
 * Resolver to get strategy by type
 */
@Service
@RequiredArgsConstructor
public class StrategyResolver {

    private final Logger logger = LoggerFactory.getLogger(LoaderKafkaListener.class);
    private final Map<LoadingStrategyType, Class<? extends LoadingStrategy>> loadingStrategyMap = new HashMap<>();
    private final Map<PublishingStrategyType, Class<? extends PublishingStrategy>> publishingStrategyMap = new HashMap<>();

    private final ApplicationContext context;

    @PostConstruct
    public void postConstruct() {
        loadingStrategyMap.put(LoadingStrategyType.INSTAGRAM_POSTS, InstagramPostLoadingStrategy.class);

        publishingStrategyMap.put(PublishingStrategyType.RECENT_INSTAGRAM_STORY, InstagramRecentPostPublishingStrategy.class);
        publishingStrategyMap.put(PublishingStrategyType.RECENT_INSTAGRAM_POST, InstagramRecentPostPublishingStrategy.class);
    }

    /**
     * Returns loading strategy by type
     *
     * @param strategyType type of strategy
     * @return loading strategy
     */
    public LoadingStrategy getLoadingStrategy(LoadingStrategyType strategyType) {
        try {
            Class<? extends LoadingStrategy> strategyClass = loadingStrategyMap.get(strategyType);

            return context.getBean(strategyClass);
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
            Class<? extends PublishingStrategy> strategyClass = publishingStrategyMap.get(strategyType);

            return context.getBean(strategyClass);
        } catch (Exception e) {
            logger.error(PUBLISHING_STRATEGY_RESOLVING_ERROR_MSG);

            throw e;
        }
    }
}
