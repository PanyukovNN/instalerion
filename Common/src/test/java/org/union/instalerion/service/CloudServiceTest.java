package org.union.instalerion.service;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.union.common.exception.CloudException;
import org.union.common.model.post.MediaInfo;
import org.union.common.model.post.Post;
import org.union.common.service.CloudService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.union.common.Constants.CODE_COULD_NOT_BE_NULL_ERROR_MSG;
import static org.union.common.Constants.POST_MEDIA_INFO_IS_NULL_ERROR_MSG;

/**
 * Unit tests of {@link CloudService}
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(FileUtils.class)
@PowerMockIgnore("javax.management.*")
public class CloudServiceTest {

    private static final String CLOUD_PATH = "cloud\\";
    private static final String VIDEO_PREFIX = "video_";
    private static final String IMAGE_PREFIX = "image_";
    private static final String CODE = "code";
    private static final String IMAGE_URL = "http://instagram.com/test_image";
    private static final String VIDEO_URL = "http://instagram.com/test_video";
    private static final int POSTS_NUMBER = 3;

    private final CloudService cloudService = new CloudService();

    private List<Post> posts;
    private ArgumentCaptor<URL> urlCaptor;
    private ArgumentCaptor<File> fileCaptor;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(FileUtils.class);

        urlCaptor = ArgumentCaptor.forClass(URL.class);
        fileCaptor = ArgumentCaptor.forClass(File.class);

        Post post = mock(Post.class);
        Mockito.when(post.getCode()).thenReturn(CODE);

        MediaInfo mediaInfo = mock(MediaInfo.class);
        Mockito.when(post.getMediaInfo()).thenReturn(mediaInfo);
        Mockito.when(mediaInfo.getVideoUrl()).thenReturn(VIDEO_URL);
        Mockito.when(mediaInfo.getImageUrl()).thenReturn(IMAGE_URL);
        posts = Collections.singletonList(post);
    }

    @Test
    public void savePostsMedia() throws IOException {
        cloudService.savePostsMedia(posts);

        PowerMockito.verifyStatic(FileUtils.class, Mockito.times(2));
        FileUtils.copyURLToFile(urlCaptor.capture(), fileCaptor.capture());

        assertEquals(VIDEO_URL, urlCaptor.getAllValues().get(0).toString());
        assertEquals(CLOUD_PATH + VIDEO_PREFIX + CODE, fileCaptor.getAllValues().get(0).toString());

        assertEquals(IMAGE_URL, urlCaptor.getAllValues().get(1).toString());
        assertEquals(CLOUD_PATH + IMAGE_PREFIX + CODE, fileCaptor.getAllValues().get(1).toString());
    }

    @Test
    public void savePostsMedia_nullMediaInfo_throwException() throws IOException {
        Mockito.when(posts.get(0).getMediaInfo()).thenReturn(null);

        try {
            cloudService.savePostsMedia(posts);
            fail();
        } catch (CloudException e) {
            assertEquals(POST_MEDIA_INFO_IS_NULL_ERROR_MSG, e.getMessage());
        }
    }

    @Test
    public void getVideoFileByCode() {
        File file = cloudService.getVideoFileByCode(CODE);

        assertEquals(CLOUD_PATH + VIDEO_PREFIX + CODE, file.getPath());
    }

    @Test
    public void getVideoFileByCode_emptyCode_throwException() {
        try {
            cloudService.getVideoFileByCode("");
            fail();
        } catch (CloudException e) {
            assertEquals(CODE_COULD_NOT_BE_NULL_ERROR_MSG, e.getMessage());
        }
    }

    @Test
    public void getImageFileByCode() {
        File file = cloudService.getImageFileByCode(CODE);

        assertEquals(CLOUD_PATH.replace("/", "\\") + IMAGE_PREFIX + CODE, file.getPath());
    }

    @Test
    public void getImageFileByCode_emptyCode_throwException() {
        try {
            cloudService.getImageFileByCode("");
            fail();
        } catch (CloudException e) {
            assertEquals(CODE_COULD_NOT_BE_NULL_ERROR_MSG, e.getMessage());
        }
    }

    public List<Post> generatePosts() {
        return Stream.generate(() -> Mockito.mock(Post.class))
                .peek(post -> Mockito.when(post.getMediaInfo()))
                .limit(POSTS_NUMBER)
                .collect(Collectors.toList());
    }
}
