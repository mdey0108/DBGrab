package com.dbgrab.runner;

import com.dbgrab.service.QueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationRunner implements CommandLineRunner {

    private final QueryService queryService;

    @Override
    public void run(String... args) {
        log.info("Starting DBGrab application...");
        // The QueryService will automatically start processing in its @PostConstruct method
    }
}