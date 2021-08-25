package org.union.common.service;

import com.github.kilianB.datastructures.tree.Result;
import com.github.kilianB.hashAlgorithms.DifferenceHash;
import com.github.kilianB.hashAlgorithms.PerceptiveHash;
import com.github.kilianB.matcher.persistent.ConsecutiveMatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.union.common.model.ProducingChannel;
import org.union.common.model.post.Post;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.PriorityQueue;

@Service
@RequiredArgsConstructor
public class ImageMatcher {

    private final CloudService cloudService;

    private final DifferenceHash differenceHash = new DifferenceHash(32, DifferenceHash.Precision.Double);
    private final PerceptiveHash perceptiveHash = new PerceptiveHash(32);
    private final double matchingThreshold = 3d;

    public boolean isDuplicate(ConsecutiveMatcher matcher, String imageUrl, String code) {
        BufferedImage image;
        try {
            image = ImageIO.read(new URL(imageUrl));
        } catch (IOException e) {
            e.printStackTrace();

            return true;
        }

        PriorityQueue<Result<String>> matchingImages = matcher.getMatchingImages(image);

        boolean hasMatch = matchingImages
                .stream()
                .anyMatch(matchingImage -> matchingImage.distance < matchingThreshold);

        if (hasMatch) {
            return true;
        } else {
            matcher.addImage(code, image);

            return false;
        }
    }

    public ConsecutiveMatcher createMatcher(List<Post> publishedPosts) throws IOException {
        ConsecutiveMatcher matcher = new ConsecutiveMatcher(true);
        matcher.addHashingAlgorithm(perceptiveHash, 3);

        for (Post post : publishedPosts) {
            String code = post.getCode();
            File image = cloudService.getImageFileByCode(code);

            matcher.addImage(code, image);
        }

        return matcher;
    }
}
