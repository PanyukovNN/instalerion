package org.union.common.service;

import com.github.kilianB.datastructures.tree.Result;
import com.github.kilianB.hashAlgorithms.PerceptiveHash;
import com.github.kilianB.matcher.persistent.ConsecutiveMatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.union.common.model.post.Post;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

import static org.union.common.Constants.IMAGE_MATCHING_THRESHOLD;
import static org.union.common.Constants.IMPOSSIBLE_TO_LOAD_IMAGE_BY_URL_ERROR_MSG;

/**
 * Service for working image matching
 */
@Service
@RequiredArgsConstructor
public class ImageMatcher {

    private final CloudService cloudService;

    /**
     * Returns matcher with added posts images to compare
     *
     * @param posts list of posts
     * @return matcher
     * @throws IOException exception
     */
    public ConsecutiveMatcher createMatcher(List<Post> posts) throws IOException {
        ConsecutiveMatcher matcher = new ConsecutiveMatcher(true);
        PerceptiveHash perceptiveHash = new PerceptiveHash(32);

        matcher.addHashingAlgorithm(perceptiveHash, 0.2);

        for (Post post : posts) {
            String code = post.getCode();
            File image = cloudService.getImageFileByCode(code);

            matcher.addImage(code, image);
        }

        return matcher;
    }

    /**
     * Looks for image duplicates in matcher
     * Has to be the last check after all posts filtering, because it is adding the image to matcher, which cant be removed
     *
     * @param matcher image matcher
     * @param imageUrl url of image to compare
     * @param code unique code of image
     * @return flag is duplicate
     */
    public boolean isUniqueImage(ConsecutiveMatcher matcher, String imageUrl, String code) {
        BufferedImage image = getBufferedImageByUrl(imageUrl)
                .orElseThrow(() -> new IllegalArgumentException(IMPOSSIBLE_TO_LOAD_IMAGE_BY_URL_ERROR_MSG));

        PriorityQueue<Result<String>> matchingImages = getMatchingImages(matcher, code, image);

        boolean noMatch = matchingImages
                .stream()
                .noneMatch(matchingImage -> matchingImage.distance < IMAGE_MATCHING_THRESHOLD);

        if (noMatch) {
            matcher.addImage(code, image);

            return true;
        } else {
            return false;
        }
    }

    private PriorityQueue<Result<String>> getMatchingImages(ConsecutiveMatcher matcher, String code, BufferedImage image) {
        try {
            return matcher.getMatchingImages(image);
        } catch (IllegalStateException e) {
            // exception while adding a very first image
            if (e.getMessage().equals("Tried to add an incompatible hash to the binary tree")) {
                matcher.addImage(code, image);

                return new PriorityQueue<>();
            } else {
                throw e;
            }
        }
    }

    private Optional<BufferedImage> getBufferedImageByUrl(String imageUrl) {
        try {
            BufferedImage image = ImageIO.read(new URL(imageUrl));

            return Optional.of(image);
        } catch (IOException e) {
            e.printStackTrace();

            return Optional.empty();
        }
    }
}
