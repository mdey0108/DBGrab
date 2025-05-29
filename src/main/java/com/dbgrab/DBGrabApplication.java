package com.dbgrab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DBGrabApplication {

    public static void main(String[] args) {
        SpringApplication.run(DBGrabApplication.class, args);
    }
}