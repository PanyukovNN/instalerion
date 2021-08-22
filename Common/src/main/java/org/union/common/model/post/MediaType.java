package org.union.common.model.post;

import lombok.Getter;

@Getter
public enum MediaType {

    IMAGE("1"),
    VIDEO("2");

    private final String value;

    MediaType(String value) {
        this.value = value;
    }
}
