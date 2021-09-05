package org.union.common.service;

import com.github.instagram4j.instagram4j.models.media.timeline.TimelineMedia;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.union.common.Constants;
import org.union.common.model.ProducingChannel;
import org.union.common.model.post.Post;
import org.union.common.model.post.PostRating;
import org.union.common.repository.PostRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.union.common.Constants.HOURS_PASSED_FROM_TAKEN_AT_FOR_RATING_CALCULATION;
import static org.union.common.Constants.LAST_UNRATED_POSTS_SCAN_LIMIT;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final DateTimeHelper dateTimeHelper;
    private final MongoTemplate mongoTemplate;

    public boolean exists(String code, String producingChannelId) {
        return postRepository.existsByCodeAndProducingChannelId(code, producingChannelId);
    }

    public Post save(Post post) {
        return postRepository.save(post);
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Optional<Post> findById(String postId) {
        return postRepository.findById(postId);
    }

    /**
     * Returns unpublished post with highest rating
     *
     * @param producingChannelId id of producing channel
     * @return optional of post
     */
    public Optional<Post> findMostRatedPost(String producingChannelId) {
        return postRepository.findFirstByProducingChannelIdAndPublishDateTimeIsNullAndPublishingErrorCountLessThanOrderByRatingDesc(producingChannelId, Constants.PUBLISHING_ERROR_COUNT_LIMIT);
    }

    /**
     * Returns unpublished post most recently downloaded
     *
     * @param producingChannelId id of producing channel
     * @return optional of post
     */
    public Optional<Post> findMostRecentPost(String producingChannelId) {
        return postRepository.findFirstByProducingChannelIdAndPublishDateTimeIsNullAndPublishingErrorCountLessThanOrderByTakenAtDesc(producingChannelId, Constants.PUBLISHING_ERROR_COUNT_LIMIT);
    }

    /**
     * Returns unrated posts, which were published not earlier than last 24 hours,
     * Max amount of posts defined in property LAST_UNRATED_POSTS_SCAN_LIMIT
     *
     * @return list of unrated posts
     */
    public List<Post> findLastUnratedPost(String producingChannelId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "takenAt");
        Pageable pageable = PageRequest.of(0, LAST_UNRATED_POSTS_SCAN_LIMIT, sort);

        LocalDateTime earlier = dateTimeHelper.getCurrentDateTime().minusHours(HOURS_PASSED_FROM_TAKEN_AT_FOR_RATING_CALCULATION);

        return postRepository.findLastUnratedPosts(producingChannelId, earlier, Constants.PUBLISHING_ERROR_COUNT_LIMIT, pageable)
                .getContent();
    }

    /**
     * Returns unpublished post most recently downloaded
     *
     * @param producingChannelId id of producing channel
     * @return optional of post
     */
    public Optional<Post> findMostRecentStory(String producingChannelId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "takenAt");
        Pageable pageable = PageRequest.of(0, 1, sort);

        List<Post> content = postRepository.findMostRecentStory(producingChannelId, Constants.PUBLISHING_ERROR_COUNT_LIMIT, pageable).getContent();

        return !content.isEmpty()
                ? Optional.of(content.get(0))
                : Optional.empty();
    }

    public void removeAll() {
        postRepository.deleteAll();
    }

    /**
     * Returns post rating, calculated by formula (likes + comments)/views
     * To calculate rate post must be published not earlier that 24 hours from now
     *
     * @param media timeline media
     * @param viewCount count of views
     * @param takenAt when post was taken
     * @return post rating
     */
    public PostRating calculateRating(TimelineMedia media, int viewCount, LocalDateTime takenAt) {
        return calculateRating(media.getComment_count(), media.getLike_count(), viewCount, takenAt);
    }

    public List<Post> findPublishedInProducingChannel(ProducingChannel producingChannel) {
        return postRepository.findByProducingChannelIdAndPublishDateTimeIsNotNull(producingChannel.getId());
    }

    private PostRating calculateRating(int commentCount, int likeCount, int viewCount, LocalDateTime takenAt) {
        PostRating rating = new PostRating();

        if (takenAt == null
                || takenAt.isAfter(dateTimeHelper.getCurrentDateTime().minusHours(HOURS_PASSED_FROM_TAKEN_AT_FOR_RATING_CALCULATION))) {
            return rating;
        }

        if (viewCount == 0) {
            rating.setImpossibleToCalculate(true);

            return rating;
        }

        rating.setValue((double) (commentCount + likeCount) / viewCount);

        return rating;
    }

    /**
     * Save all posts
     *
     * @param posts list of posts
     */
    public void saveAll(List<Post> posts) {
        postRepository.saveAll(posts);
    }

    /**
     * Returns all posts with errors
     */
    public List<Post> findAllWithErrors() {
        return postRepository.findByPublishingErrorCountGreaterThanEqual(1);
    }
}
