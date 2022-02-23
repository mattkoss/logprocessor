package com.saraphie.logprocessor;

import com.saraphie.logprocessor.domain.LogProcessor;
import com.saraphie.logprocessor.exception.ProcessingException;
import com.saraphie.logprocessor.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class LogProcessorApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(LogProcessorApplication.class, args);
    }

    @Autowired
    private LogProcessor logProcessor;

    @Autowired
    private EventRepository eventRepository;

    @Override
    public void run(String... args) {
        for (int i = 0; i < args.length; ++i) {
            log.info("args[{}]: {}", i, args[i]);
        }

        if (args.length == 0) {
            log.error("Path missing.");
        } else {
            // clear the table
            eventRepository.deleteAll();

            try {
                logProcessor.process(args[0]);
            } catch (ProcessingException ex) {
                log.error("Error during processing", ex);
            }
        }
    }
}
