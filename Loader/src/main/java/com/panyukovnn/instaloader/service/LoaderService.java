package com.panyukovnn.instaloader.service;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.IGDevice;
import com.github.instagram4j.instagram4j.actions.users.UserAction;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import com.github.instagram4j.instagram4j.models.media.UploadParameters;
import com.github.instagram4j.instagram4j.models.media.timeline.Comment;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineVideoMedia;
import com.github.instagram4j.instagram4j.requests.IGGetRequest;
import com.github.instagram4j.instagram4j.requests.IGRequest;
import com.github.instagram4j.instagram4j.requests.feed.FeedUserRequest;
import com.github.instagram4j.instagram4j.requests.upload.RuploadPhotoRequest;
import com.github.instagram4j.instagram4j.requests.upload.RuploadVideoRequest;
import com.github.instagram4j.instagram4j.responses.feed.FeedUserResponse;
import com.panyukovnn.common.model.VideoPost;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class LoaderService {

    @Value("${instagram.account}")
    private String account;

    @Value("${instagram.password}")
    private String password;

    public List<VideoPost> load() throws IGLoginException, ExecutionException, InterruptedException {
        IGClient client = IGClient.builder()
                .username(account)
                .password(password)
                .login();

        UserAction userAction = client.actions().users().findByUsername("garikkharlamov").get();

        List<VideoPost> videoPosts = new ArrayList<>();

        CompletableFuture<FeedUserResponse> future = client.sendRequest(new FeedUserRequest(userAction.getUser().getPk()));

        FeedUserResponse feedUserResponse = future.get();

        feedUserResponse.getItems().stream()
                .limit(5)
                .filter(TimelineVideoMedia.class::isInstance)
                .map(TimelineVideoMedia.class::cast)
                .filter(video -> Instant.ofEpochMilli(video.getTaken_at() * 1000)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime().isAfter(LocalDateTime.now().minusDays(1)))
                .forEach(video -> {
                    System.out.println(video);
                    VideoPost videoPost = new VideoPost();
                    videoPost.setDescription(video.getCaption().getText());
                    videoPost.setUrl(video.getVideo_versions().get(0).getUrl());

                    String url = video.getImage_versions2().getCandidates().get(0).getUrl();
                    System.out.println(url);

                    videoPost.setCoverUrl(url);

                    videoPosts.add(videoPost);
                });

        System.out.println(videoPosts.get(0));

        try (BufferedInputStream in = new BufferedInputStream(new URL(videoPosts.get(0).getUrl()).openStream());
             BufferedInputStream in2 = new BufferedInputStream(new URL(videoPosts.get(0).getCoverUrl()).openStream());
             ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
             ByteArrayOutputStream baos2 = new ByteArrayOutputStream()) {
            byte[] dataBuffer = new byte[1000000];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1000000)) != -1) {
                baos1.write(dataBuffer, 0, bytesRead);
            }

            byte[] postCover = new byte[4096];

            int bytesRead2;
            while ((bytesRead2 = in2.read(postCover, 0, 4096)) != -1) {
                baos2.write(postCover, 0, bytesRead2);
            }

            File file = new File("img.jpg");

            FileUtils.copyURLToFile(new URL(videoPosts.get(0).getCoverUrl()), file);

//            client.getActions().upload().photo(postCover, "12873198749817498");

//            UploadParameters uploadParameters = UploadParameters.builder()
//                    .media_type("2")
//                    .upload_id("yoyoyoyoyoy")
//                    .build();

//            client.getActions().upload().videoWithCover(dataBuffer, postCover, uploadParameters);

//            client.actions().upload().photo(postCover, "yoyoyoyoyoyoy.jpg");
//            client.actions().timeline().uploadVideo(dataBuffer, postCover, "video from instalerion");
            client.actions().timeline().uploadPhoto(file, "yoyoyo.jpg");



//            client.sendRequest(new RuploadPhotoRequest(file, uploadParameters)).get();


        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


        return videoPosts;
    }
}
