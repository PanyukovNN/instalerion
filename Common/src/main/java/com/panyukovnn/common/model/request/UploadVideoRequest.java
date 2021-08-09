package com.panyukovnn.common.model.request;

import com.panyukovnn.common.model.ConsumeChannel;
import com.panyukovnn.common.model.VideoPost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class UploadVideoRequest implements Serializable {

    private String consumerId;
    private VideoPost videoPost;
}
