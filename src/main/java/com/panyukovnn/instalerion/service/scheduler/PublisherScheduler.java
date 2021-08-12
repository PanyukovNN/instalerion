package com.panyukovnn.instalerion.service.scheduler;

import com.panyukovnn.common.model.post.Post;
import com.panyukovnn.common.model.request.PublishPostRequest;
import com.panyukovnn.common.repository.PostRepository;
import com.panyukovnn.instalerion.service.kafka.KafkaSender;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static com.panyukovnn.common.Constants.POST_FOR_PUBLICATION_NOT_FOUND_ERROR_MSG;

@Service
@RequiredArgsConstructor
public class PublisherScheduler {

    private final KafkaSender kafkaSender;
    private final PostRepository postRepository;

    @Scheduled(fixedRateString = "${publisher.scheduler.fixed.rate.mills}")
    public void schedulePublisherKafkaSend() {
        String customerId = "6115880fe732f957a46fb24e";

        Post post = postRepository.findFirstByProducingChannelIdAndPublishDateTimeIsNull(customerId);

        if (post == null) {
            System.out.println(POST_FOR_PUBLICATION_NOT_FOUND_ERROR_MSG);

            return;
        }

        PublishPostRequest request = new PublishPostRequest(post.getId());

        kafkaSender.sendPublishPost(request);
    }
}
