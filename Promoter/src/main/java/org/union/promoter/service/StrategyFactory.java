package org.union.promoter.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.union.common.service.*;
import org.union.instalerion.service.*;
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
import java.util.function.Supplier;

import static org.union.common.Constants.*;

/**
 * Resolver to get strategy by type
 */
@Service
@RequiredArgsConstructor
public class StrategyFactory {

    private final Logger logger = LoggerFactory.getLogger(StrategyFactory.class);
    private final Map<LoadingStrategyType, Supplier<LoadingStrategy>> loadingStrategyMap = new HashMap<>();
    private final Map<PublishingStrategyType, Supplier<PublishingStrategy>> publishingStrategyMap = new HashMap<>();
    private final Map<PostDefiningStrategyType, Supplier<PostDefiningStrategy>> postDefiningStrategyMap = new HashMap<>();

    private final PostService postService;
    private final ImageMatcher imageMatcher;
    private final CloudService cloudService;
    private final InstaService instaService;
    private final LoaderService loaderService;
    private final DateTimeHelper dateTimeHelper;
    private final ProducingChannelService producingChannelService;
    private final ConsumingChannelService consumingChannelService;

    @PostConstruct
    public void postConstruct() {
        loadingStrategyMap.put(LoadingStrategyType.INSTAGRAM_POSTS, () -> new InstagramBaseLoadingStrategy(
                postService,
                imageMatcher,
                cloudService,
                instaService,
                loaderService,
                dateTimeHelper,
                producingChannelService,
                consumingChannelService
        ));

        publishingStrategyMap.put(PublishingStrategyType.INSTAGRAM_STORY, () -> new InstagramStoryPublishingStrategy(
                postService,
                cloudService,
                instaService,
                dateTimeHelper,
                producingChannelService
        ));
        publishingStrategyMap.put(PublishingStrategyType.INSTAGRAM_POST, () -> new InstagramPostPublishingStrategy(
                postService,
                cloudService,
                instaService,
                dateTimeHelper,
                producingChannelService
        ));

        postDefiningStrategyMap.put(PostDefiningStrategyType.MOST_RATED_POST, () -> new RatedPostDefiningStrategy(postService));
        postDefiningStrategyMap.put(PostDefiningStrategyType.MOST_RECENT_POST, () -> new RecentPostDefiningStrategy(postService));
        postDefiningStrategyMap.put(PostDefiningStrategyType.MOST_RECENT_STORY, () -> new RecentStoryDefiningStrategy(postService));
    }

    /**
     * Returns loading strategy by type
     *
     * @param strategyType type of strategy
     * @return loading strategy
     */
    public LoadingStrategy getLoadingStrategy(LoadingStrategyType strategyType) {
        try {
            return loadingStrategyMap.get(strategyType).get();
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
            return publishingStrategyMap.get(strategyType).get();
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
            return postDefiningStrategyMap.get(strategyType).get();
        } catch (Exception e) {
            logger.error(POST_DEFINING_STRATEGY_RESOLVING_ERROR_MSG);

            throw e;
        }
    }
}
