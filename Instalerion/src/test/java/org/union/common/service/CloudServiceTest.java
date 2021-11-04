package org.union.common.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.union.common.model.post.Post;
import org.union.instalerion.InstalerionApplication;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = InstalerionApplication.class)
public class CloudServiceTest {

    private static final String CLOUD_PATH = "cloud/";
    private static final String VIDEO_PREFIX = "video_";
    private static final String IMAGE_PREFIX = "image_";
    private static final String CODE = "code";
    private static final String URL = "url";
    private static final String FILENAME = "filename";
    private static final int POSTS_NUMBER = 3;

    @Autowired
    private CloudService cloudService;

    private List<Post> posts;

    @Before
    public void setUp() {
        Post post = mock(Post.class);
        posts = Collections.singletonList(post);
    }

    @Test
    public void saveByUrl() throws IOException {
    }

    @Test
    public void savePostsMedia() throws IOException {
    }

    //TODO tests with input null/empty string
    @Test
    public void getVideoFileByCode() {
        File file = cloudService.getVideoFileByCode(CODE);

        assertEquals(CLOUD_PATH.replace("/", "\\") + VIDEO_PREFIX + CODE, file.getPath());
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
