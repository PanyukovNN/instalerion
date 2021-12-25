package org.union.common.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import java.time.LocalDateTime;

/**
 * User of instalerion
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Document(collection = "customer")
public class Customer implements Persistable<String> {

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

    @CreatedDate
    private LocalDateTime creationDateTime;

    @Override
    public boolean isNew() {
        return !StringUtils.hasLength(id);
    }
}
