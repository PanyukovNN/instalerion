package com.panyukovnn.common.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Customer
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Document(collection = "customer")
public class Customer {

    @Id
    private String id;

    private String login;
    private String password;

    private List<ConsumeChannel> consumeChannels;
}
