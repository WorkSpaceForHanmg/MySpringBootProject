package com.basic.myspringboot.config;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyEnvironment {
    private String mode;
}
