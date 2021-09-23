package org.union.promoter.service.publishingstrategy;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.union.common.model.InstaClient;
import org.union.common.service.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.mockito.Mockito.times;

@SpringBootTest
class InstagramPostPublishingStrategyTest {

    private InstagramPostPublishingStrategy postPublishingStrategy;

    @Mock
    private File imageFile;
    @Mock
    private InstaClient client;
    @Mock
    private PostService postService;
    @Mock
    private CloudService cloudService;
    @Mock
    private InstaService instaService;
    @Mock
    private DateTimeHelper dateTimeHelper;
    @Mock
    private ProducingChannelService producingChannelService;

    @Test
    void uploadPhoto() throws ExecutionException, InterruptedException {
        postPublishingStrategy = new InstagramPostPublishingStrategy(
                postService,
                cloudService,
                instaService,
                dateTimeHelper,
                producingChannelService);

        List<String> hashtags = null;

        postPublishingStrategy.uploadPhoto(hashtags, imageFile, client);

        Mockito.verify(instaService, times(1))
                .uploadPhotoPost(client, imageFile, EMPTY);
    }

    @Test
    void uploadVideo() {
    }

    @Test
    void logStartPublishing() {
    }

    @Test
    void setPostPublicationDateTime() {
    }

    @Test
    void setLastPublicationDateTime() {
    }

    @Test
    void logSuccessPublishing() {
    }
}
