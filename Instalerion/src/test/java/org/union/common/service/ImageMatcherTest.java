package org.union.common.service;

import com.github.kilianB.hashAlgorithms.PerceptiveHash;
import com.github.kilianB.matcher.persistent.ConsecutiveMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.union.common.model.post.Post;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
    ImageMatcher.class,
    PerceptiveHash.class,
    ConsecutiveMatcher.class
})
public class ImageMatcherTest {

    private static final String CODE = "CODE";

    private List<Post> posts;
    private ImageMatcher imageMatcher;

    @Mock
    private File file;
    @Mock
    private Post post;
    @Mock
    private CloudService cloudService;
    @Mock
    private PerceptiveHash perceptiveHash;
    @Mock
    private ConsecutiveMatcher consecutiveMatcher;

    @Before
    public void setUp() {
        imageMatcher = new ImageMatcher(cloudService);
        posts = Collections.singletonList(post);

        when(post.getCode()).thenReturn(CODE);
        when(cloudService.getImageFileByCode(CODE)).thenReturn(file);
    }

    @Test
    public void createMatcher() throws Exception {
        whenNew(ConsecutiveMatcher.class)
                .withArguments(true)
                .thenReturn(consecutiveMatcher);
        whenNew(PerceptiveHash.class)
                .withArguments(32)
                .thenReturn(perceptiveHash);

        ConsecutiveMatcher matcher = imageMatcher.createMatcher(posts);

        assertEquals(consecutiveMatcher, matcher);

        verify(consecutiveMatcher, times(1))
                .addHashingAlgorithm(perceptiveHash, 0.2);
        verify(post, times(1)).getCode();
        verify(cloudService, times(1)).getImageFileByCode(CODE);
        verify(consecutiveMatcher, times(1)).addImage(CODE, file);
    }

    @Test
    public void isUniqueImage() {
    }
}
