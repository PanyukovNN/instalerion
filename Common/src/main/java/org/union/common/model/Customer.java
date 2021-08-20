package org.union.common.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

/**
 * User of instalerion
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Document(collection = "customer")
public class Customer {

    @Id
    private String id;

    /**
     * User name
     */
    private String username;

    /**
     * User password
     */
    private String password;
}
