package org.union.common.service;

import com.github.instagram4j.instagram4j.models.media.timeline.TimelineMedia;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.union.common.Constants;
import org.union.common.model.ProducingChannel;
import org.union.common.model.post.Post;
import org.union.common.model.post.PostRating;
import org.union.common.repository.PostRepository;
import org.union.common.model.post.PublicationType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for posts
 */
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final DateTimeHelper dateTimeHelper;

    /**
     * Does post with unique code exists in producing chanel
     *
     * @param code unique code
     * @param producingChannelId producing channel id
     * @return does the post exists in db
     */
    public boolean exists(String code, String producingChannelId) {
        return postRepository.existsByCodeAndProducingChannelId(code, producingChannelId);
    }

    /**
     * Save the post
     *
     * @param post post
     * @return saved post
     */
    public Post save(Post post) {
        return postRepository.save(post);
    }

    /**
     * Find all posts
     *
     * @return list of posts
     */
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    /**
     * Find post by id
     *
     * @param postId post id
     * @return optional of post
     */
    public Optional<Post> findById(String postId) {
        return postRepository.findById(postId);
    }

    /**
     * Returns unrated posts, which were published not earlier than last 24 hours,
     * Max amount of posts defined in property LAST_UNRATED_POSTS_SCAN_LIMIT
     *
     * @return list of unrated posts
     */
    public List<Post> findLastUnratedPost(String producingChannelId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "takenAt");
        Pageable pageable = PageRequest.of(0, Constants.LAST_UNRATED_POSTS_SCAN_LIMIT, sort);

        LocalDateTime earlier = dateTimeHelper.getCurrentDateTime().minusHours(Constants.HOURS_PASSED_FROM_TAKEN_AT_FOR_RATING_CALCULATION);

        return postRepository.findLastUnratedPosts(producingChannelId, earlier, Constants.PUBLISHING_ERROR_COUNT_LIMIT, pageable)
                .getContent();
    }

    /**
     * Returns unpublished post/story most recently downloaded
     *
     * @param producingChannelId id of producing channel
     * @param publicationType type of publication
     * @param sort sorting strategy
     * @return optional of post
     */
    public Optional<Post> findByPublicationType(String producingChannelId, PublicationType publicationType, Sort sort) {
        Pageable pageable = PageRequest.of(0, 1, sort);

        List<Post> content = postRepository.findMostRecentStory(
                producingChannelId,
                publicationType,
                Constants.PUBLISHING_ERROR_COUNT_LIMIT,
                pageable).getContent();

        return !content.isEmpty() ? Optional.of(content.get(0)) : Optional.empty();
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

    /**
     * Returns list of published posts by producing channel an publication type
     *
     * @param producingChannel producing channel
     * @param publicationType type of publication
     * @return list of published posts
     */
    public List<Post> findPublished(ProducingChannel producingChannel, PublicationType publicationType) {
        return postRepository.findPublishedByProducingChannelAndPublicationType(
                producingChannel.getId(), publicationType);
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
     *
     * @return list of posts with errors
     */
    public List<Post> findAllWithErrors() {
        return postRepository.findByPublishingErrorCountGreaterThanEqual(1);
    }

    private PostRating calculateRating(int commentCount, int likeCount, int viewCount, LocalDateTime takenAt) {
        PostRating rating = new PostRating();

        if (takenAt == null
                || takenAt.isAfter(dateTimeHelper.getCurrentDateTime().minusHours(Constants.HOURS_PASSED_FROM_TAKEN_AT_FOR_RATING_CALCULATION))) {
            return rating;
        }

        if (viewCount == 0) {
            rating.setImpossibleToCalculate(true);

            return rating;
        }

        rating.setValue((double) (commentCount + likeCount) / viewCount);

        return rating;
    }
}
