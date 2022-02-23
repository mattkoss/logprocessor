package com.saraphie.logprocessor.domain;

import com.saraphie.logprocessor.exception.ProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Component
@Slf4j
public class NioLogProcessor implements LogProcessor {

    private final EventExtractor eventExtractor;
    private final EventProcessor eventProcessor;

    public NioLogProcessor(EventExtractor eventExtractor, EventProcessor eventProcessor) {
        this.eventExtractor = eventExtractor;
        this.eventProcessor = eventProcessor;
    }

    @Override
    public void process(String fileName) {
        log.info("Processing file: {}", fileName);
        Path path = Paths.get(fileName);

        try (Stream<String> lines = Files.lines(path).parallel()) {
            lines.forEach(line -> eventProcessor.processEvent(eventExtractor.extractEvent(line)));
        } catch (Exception ex) {
            throw new ProcessingException("Error during processing", ex);
        }
    }
}
