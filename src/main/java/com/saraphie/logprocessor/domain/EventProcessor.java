package com.saraphie.logprocessor.domain;

public interface EventProcessor {
    void processEvent(Event extractEvent);
}
