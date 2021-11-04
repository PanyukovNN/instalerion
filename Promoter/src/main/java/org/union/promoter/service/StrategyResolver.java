package org.union.promoter.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.union.common.service.loadingstrategy.LoadingStrategyType;
import org.union.common.service.publishingstrategy.PostDefiningStrategyType;
import org.union.common.service.publishingstrategy.PublishingStrategyType;
import org.union.promoter.service.loadingstrategy.InstagramBaseLoadingStrategy;
import org.union.promoter.service.loadingstrategy.LoadingStrategy;
import org.union.promoter.service.publishingstrategy.InstagramPostPublishingStrategy;
import org.union.promoter.service.publishingstrategy.InstagramStoryPublishingStrategy;
import org.union.promoter.service.publishingstrategy.PublishingStrategy;
import org.union.promoter.service.publishingstrategy.postdefiningstrategy.PostDefiningStrategy;
import org.union.promoter.service.publishingstrategy.postdefiningstrategy.RatedPostDefiningStrategy;
import org.union.promoter.service.publishingstrategy.postdefiningstrategy.RecentPostDefiningStrategy;
import org.union.promoter.service.publishingstrategy.postdefiningstrategy.RecentStoryDefiningStrategy;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static org.union.common.Constants.*;

/**
 * Resolver to get strategy by type
 */
@Service
@RequiredArgsConstructor
public class StrategyResolver {

    private final Logger logger = LoggerFactory.getLogger(StrategyResolver.class);
    private final Map<LoadingStrategyType, Class<? extends LoadingStrategy>> loadingStrategyMap = new HashMap<>();
    private final Map<PublishingStrategyType, Class<? extends PublishingStrategy>> publishingStrategyMap = new HashMap<>();
    private final Map<PostDefiningStrategyType, Class<? extends PostDefiningStrategy>> postDefiningStrategyMap = new HashMap<>();

    private final ApplicationContext context;

    @PostConstruct
    public void postConstruct() {
        loadingStrategyMap.put(LoadingStrategyType.INSTAGRAM_POSTS, InstagramBaseLoadingStrategy.class);

        publishingStrategyMap.put(PublishingStrategyType.INSTAGRAM_STORY, InstagramStoryPublishingStrategy.class);
        publishingStrategyMap.put(PublishingStrategyType.INSTAGRAM_POST, InstagramPostPublishingStrategy.class);

        postDefiningStrategyMap.put(PostDefiningStrategyType.MOST_RATED_POST, RatedPostDefiningStrategy.class);
        postDefiningStrategyMap.put(PostDefiningStrategyType.MOST_RECENT_POST, RecentPostDefiningStrategy.class);
        postDefiningStrategyMap.put(PostDefiningStrategyType.MOST_RECENT_STORY, RecentStoryDefiningStrategy.class);
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

    /**
     * Returns post defining strategy by type
     *
     * @param strategyType type of strategy
     * @return publishing strategy
     */
    public PostDefiningStrategy getPostDefiningStrategy(PostDefiningStrategyType strategyType) {
        try {
            Class<? extends PostDefiningStrategy> strategyClass = postDefiningStrategyMap.get(strategyType);

            return context.getBean(strategyClass);
        } catch (Exception e) {
            logger.error(POST_DEFINING_STRATEGY_RESOLVING_ERROR_MSG);

            throw e;
        }
    }
}
