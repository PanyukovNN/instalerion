package org.union.common.service;

import com.github.instagram4j.instagram4j.models.media.timeline.TimelineMedia;
import org.union.common.model.ProducingChannel;
import org.union.common.model.post.Post;
import org.union.common.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.union.common.Constants;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final DateTimeHelper dateTimeHelper;

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
    public Optional<Post> findMostRated(String producingChannelId) {
        return postRepository.findFirstByProducingChannelIdAndPublishDateTimeIsNullAndPublishingErrorCountLessThanOrderByRatingDesc(producingChannelId, Constants.PUBLISHING_ERROR_COUNT_LIMIT);
    }

    public void removeAll() {
        postRepository.deleteAll();
    }

    /**
     * Returns post rating, calculated by formula (likes + comments)/views
     * To calculate rate post must be published not earlier that 24 hours from now
     *
     * @param media timeline media
     * @param takenAt when post was taken
     * @param viewCount count of views
     * @return post rating
     */
    public double calculateRating(TimelineMedia media, LocalDateTime takenAt, int viewCount) {
        if (takenAt == null
                || takenAt.isAfter(dateTimeHelper.getCurrentDateTime().minusDays(1))) {
            return -1d;
        }

        if (viewCount == 0) {
            return 0d;
        }

        return (double) (media.getComment_count() + media.getLike_count()) / viewCount;
    }

    public List<Post> findPublishedInProducingChannel(ProducingChannel producingChannel) {
        return postRepository.findByProducingChannelIdAndPublishDateTimeIsNotNull(producingChannel.getId());
    }
}
