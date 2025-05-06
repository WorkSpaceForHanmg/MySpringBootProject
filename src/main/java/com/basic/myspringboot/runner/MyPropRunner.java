package com.basic.myspringboot.runner;

import com.basic.myspringboot.config.MyEnvironment;
import com.basic.myspringboot.property.MyPropProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MyPropRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(MyPropRunner.class);
    private final MyPropProperties myProp;
    private final MyEnvironment env;

    public MyPropRunner(MyPropProperties myProp, MyEnvironment env) {
        this.myProp = myProp;
        this.env = env;
    }

    @Override
    public void run(String... args) {
        logger.info("환경 모드: {}", env.getMode());
        logger.debug("username = {}", myProp.getUsername());
        logger.debug("port = {}", myProp.getPort());
    }
}


