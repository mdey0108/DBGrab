package com.dbgrab.runner;

import com.dbgrab.service.QueryService;
import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2 // Changed from @Slf4j to @Log4j2
public class ApplicationRunner implements CommandLineRunner {

    private final QueryService queryService;

    @Override
    public void run(String... args) {
        log.info("Starting DBGrab application...");
        // The QueryService will automatically start processing in its @PostConstruct
        // method
    }
}