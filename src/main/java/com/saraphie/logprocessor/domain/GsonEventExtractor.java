package com.saraphie.logprocessor.domain;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GsonEventExtractor implements EventExtractor {

    private final Gson gson;

    public GsonEventExtractor(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Event extractEvent(String line) {
        return gson.fromJson(line, Event.class);
    }
}
