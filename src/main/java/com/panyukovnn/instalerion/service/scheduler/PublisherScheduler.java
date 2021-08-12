package com.panyukovnn.instalerion.service.scheduler;

import com.panyukovnn.common.model.post.Post;
import com.panyukovnn.common.model.post.VideoPost;
import com.panyukovnn.common.model.request.UploadVideoRequest;
import com.panyukovnn.common.repository.PostRepository;
import com.panyukovnn.instalerion.service.kafka.KafkaSender;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static com.panyukovnn.common.Constants.VIDEO_POST_FOR_PUBLICATION_NOT_FOUND_ERROR_MSG;

@Service
@RequiredArgsConstructor
public class PublisherScheduler {

    private final KafkaSender kafkaSender;
    private final PostRepository postRepository;

    @Scheduled(fixedRateString = "${publisher.scheduler.fixed.rate.mills}")
    public void schedulePublisherKafkaSend() {
//        String customerId = "6110322f8f21ee113e916f85";
        String customerId = "61142789176ee50133a93609";

        Post post = postRepository.findFirstByCustomerIdAndPublishDateTimeIsNull(customerId);



//        if (videoPost == null) {
//            System.out.println(VIDEO_POST_FOR_PUBLICATION_NOT_FOUND_ERROR_MSG);
//
//            return;
//        }
//
//        UploadVideoRequest request = new UploadVideoRequest(videoPost.getId());
//
//        kafkaSender.publisherUploadVideoSend(request);
    }
}
