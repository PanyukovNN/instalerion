package com.panyukovnn.common.service;

import com.panyukovnn.common.model.post.ImagePost;
import com.panyukovnn.common.model.post.VideoPost;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

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

    /**
     * Save a file by url to the cloud
     *
     * @param url url of file
     * @param filename name of file
     * @throws IOException exception
     */
    public void saveByUrl(String url, String filename) throws IOException {
        FileUtils.copyURLToFile(new URL(url), new File(CLOUD_PATH + filename));
    }

    /**
     * Save video posts files
     *
     * @param videoPosts list of video posts
     * @throws IOException exception
     */
    public void saveVideoPosts(List<VideoPost> videoPosts) throws IOException {
        for (VideoPost videoPost : videoPosts) {
            // Сохраняем видео
            saveByUrl(videoPost.getUrl(), videoPost.getCode() + ".mp4");

            // Сохраняем фото обложки
            saveByUrl(videoPost.getCoverUrl(), videoPost.getCode() + ".jpg");
        }
    }

    /**
     * Save image posts files
     *
     * @param imagePosts list of image posts
     * @throws IOException exception
     */
    public void saveImagePosts(List<ImagePost> imagePosts) throws IOException {
        for (ImagePost imagePost : imagePosts) {
            saveByUrl(imagePost.getUrl(), imagePost.getUrl() + ".jpg");
        }
    }

    /**
     * Returns a video file
     *
     * @param code unique instagram code
     * @return video file
     */
    public File getVideoFileByCode(String code) {
        return new File(CLOUD_PATH + code + ".mp4");
    }

    /**
     * Returns a photo file
     *
     * @param code unique instagram code
     * @return photo file
     */
    public File getImageFileByCode(String code) {
        return new File(CLOUD_PATH + code + ".jpg");
    }
}
