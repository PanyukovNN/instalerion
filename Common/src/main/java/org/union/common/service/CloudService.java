package org.union.common.service;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.union.common.Constants;
import org.union.common.exception.CloudException;
import org.union.common.model.post.Post;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Service for working with files in the cloud
 */
@Service
public class CloudService {

    private static final String CLOUD_PATH = "cloud/";
    private static final String VIDEO_PREFIX = "video_";
    private static final String IMAGE_PREFIX = "image_";

    /**
     * Save posts media files
     *
     * @param posts list of posts
     * @throws IOException exception
     */
    public void savePostsMedia(List<Post> posts) throws IOException {
        for (Post post : posts) {
            if (post.getMediaInfo() == null) {
                throw new CloudException(Constants.POST_MEDIA_INFO_IS_NULL_ERROR_MSG);
            }

            if (post.getMediaInfo().getVideoUrl() != null) {
                saveByUrl(post.getMediaInfo().getVideoUrl(), VIDEO_PREFIX + post.getCode());
            }

            // image must be always
            saveByUrl(post.getMediaInfo().getImageUrl(), IMAGE_PREFIX + post.getCode());
        }
    }

    /**
     * Returns a video file
     *
     * @param code unique instagram code
     * @return video file
     */
    public File getVideoFileByCode(String code) {
        if (!StringUtils.hasText(code)) {
            throw new CloudException(Constants.CODE_COULD_NOT_BE_NULL_ERROR_MSG);
        }

        return new File(CLOUD_PATH + VIDEO_PREFIX + code);
    }

    /**
     * Returns a photo file
     *
     * @param code unique instagram code
     * @return photo file
     */
    public File getImageFileByCode(String code) {
        if (!StringUtils.hasText(code)) {
            throw new CloudException(Constants.CODE_COULD_NOT_BE_NULL_ERROR_MSG);
        }

        return new File(CLOUD_PATH + IMAGE_PREFIX + code);
    }

    /**
     * Save a file by url to the cloud
     *
     * @param url url of file
     * @param filename name of file
     * @throws IOException exception
     */
    private void saveByUrl(String url, String filename) throws IOException {
        FileUtils.copyURLToFile(new URL(url), new File(CLOUD_PATH + filename));
    }
}
