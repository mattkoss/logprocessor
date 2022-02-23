package com.saraphie.logprocessor.domain;

public interface EventExtractor {
    Event extractEvent(String line);
}
