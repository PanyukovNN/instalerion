package com.panyukovnn.instalerion.module;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Customer {

    @Id
    public String id;

    public String name;
    public String password;
}
