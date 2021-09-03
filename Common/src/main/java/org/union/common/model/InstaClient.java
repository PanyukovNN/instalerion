package org.union.common.model;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.requests.media.MediaInfoRequest;
import com.github.instagram4j.instagram4j.responses.media.MediaInfoResponse;
import com.github.instagram4j.instagram4j.responses.media.MediaResponse;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

/**
 * Wrapper to work with IGClient
 */
@Getter
@Setter
public class InstaClient {

    private final IGClient iGClient;
    private final LocalDateTime loginTime;

    /**
     * ctor
     *
     * @param iGClient IGClient
     * @param loginTime date time of login
     */
    public InstaClient(IGClient iGClient, LocalDateTime loginTime) {
        this.iGClient = iGClient;
        this.loginTime = loginTime;
    }

    /**
     * Uploads image post
     *
     * @param imageFile file of image
     * @param caption description
     * @return response
     */
    public MediaResponse uploadPhotoPost(File imageFile, String caption) throws ExecutionException, InterruptedException {
        return iGClient
                .actions()
                .timeline()
                .uploadPhoto(imageFile, caption)
                .get();
    }

    /**
     * Uploads video post
     *
     * @param videoFile file of video
     * @param coverFile file of cover
     * @param caption description
     * @return response
     */
    public MediaResponse uploadVideoPost(File videoFile, File coverFile, String caption) throws ExecutionException, InterruptedException {
        return iGClient
                .actions()
                .timeline()
                .uploadVideo(videoFile, coverFile, caption)
                .get();
    }

    /**
     * Uploads image story
     *
     * @param imageFile file of image
     * @return response
     */
    public MediaResponse uploadPhotoStory(File imageFile) throws ExecutionException, InterruptedException {
        return iGClient
                .actions()
                .story()
                .uploadPhoto(imageFile)
                .get();
    }

    /**
     * Uploads video story
     *
     * @param videoFile file of video
     * @param coverFile file of cover
     * @return response
     */
    public MediaResponse uploadVideoStory(File videoFile, File coverFile) throws ExecutionException, InterruptedException {
        return iGClient
                .actions()
                .story()
                .uploadVideo(videoFile, coverFile)
                .get();
    }

    /**
     * Returns info about instagram media
     *
     * @param mediaId id of media
     * @return media info response
     */
    public MediaInfoResponse requestMediaInfo(long mediaId) throws ExecutionException, InterruptedException {
        return iGClient
                .sendRequest(new MediaInfoRequest(String.valueOf(mediaId)))
                .get();
    }
}
