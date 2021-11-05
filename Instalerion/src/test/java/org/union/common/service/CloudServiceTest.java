package org.union.common.service;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.union.common.exception.CloudException;
import org.union.common.model.post.MediaInfo;
import org.union.common.model.post.Post;
import org.union.instalerion.InstalerionApplication;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.union.common.Constants.POST_MEDIA_INFO_IS_NULL_ERROR_MSG;

@RunWith(PowerMockRunner.class)
@SpringBootTest(classes = InstalerionApplication.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest(FileUtils.class)
@PowerMockIgnore("javax.management.*")
public class CloudServiceTest {

    private static final String CLOUD_PATH = "cloud/";
    private static final String VIDEO_PREFIX = "video_";
    private static final String IMAGE_PREFIX = "image_";
    private static final String CODE = "code";
    private static final String IMAGE_URL = "http://instagram.com/some_image";
    private static final String VIDEO_URL = "http://instagram.com/some_video";
    private static final String FILENAME = "filename";
    private static final int POSTS_NUMBER = 3;

    @Autowired
    private CloudService cloudService;

    private List<Post> posts;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(FileUtils.class);

        Post post = mock(Post.class);
        MediaInfo mediaInfo = mock(MediaInfo.class);

        Mockito.when(post.getMediaInfo()).thenReturn(mediaInfo);
        Mockito.when(mediaInfo.getVideoUrl()).thenReturn(VIDEO_URL);
        Mockito.when(mediaInfo.getImageUrl()).thenReturn(IMAGE_URL);
        posts = Collections.singletonList(post);
    }

    @Test
    public void savePostsMedia() throws IOException {
        cloudService.savePostsMedia(posts);

        //TODO add captors
        PowerMockito.verifyStatic(FileUtils.class, Mockito.times(2));
        FileUtils.copyURLToFile(any(URL.class), any(File.class));
    }

    //TODO tests with input null/empty string
    @Test
    public void getVideoFileByCode() {
        File file = cloudService.getVideoFileByCode(CODE);

        assertEquals(CLOUD_PATH.replace("/", "\\") + VIDEO_PREFIX + CODE, file.getPath());
    }

    @Test
    public void savePostsMedia_NullMediaInfo_ThrowException() throws IOException {
        Mockito.when(posts.get(0).getMediaInfo()).thenReturn(null);

        try {
            cloudService.savePostsMedia(posts);
            fail();
        } catch (CloudException e) {
            assertEquals(POST_MEDIA_INFO_IS_NULL_ERROR_MSG, e.getMessage());
        }
    }

    @Test
    public void getImageFileByCode() {
        File file = cloudService.getImageFileByCode(CODE);

        assertEquals(CLOUD_PATH.replace("/", "\\") + IMAGE_PREFIX + CODE, file.getPath());
    }

    public List<Post> generatePosts() {
        return Stream.generate(() -> Mockito.mock(Post.class))
                .peek(post -> Mockito.when(post.getMediaInfo()))
                .limit(POSTS_NUMBER)
                .collect(Collectors.toList());
    }
}
