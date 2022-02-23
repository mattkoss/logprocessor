package com.saraphie.logprocessor.domain;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GsonEventExtractorTest {

    GsonEventExtractor eventExtractor;

    @BeforeEach
    void setUp() {
        eventExtractor = new GsonEventExtractor(new Gson());
    }

    @Test
    @DisplayName("when correct json is passed, event is populated correctly")
    void extractEvent() {
        // when
        Event result = eventExtractor.extractEvent("{\"id\": \"event1\", \"state\": \"STARTED\", \"type\": \"APPLICATION_LOG\", \"host\": \"12345\", \"timestamp\": 1491377495216}");

        // then
        assertThat(result.getId()).isEqualTo("event1");
        assertThat(result.getState()).isEqualTo(State.STARTED);
        assertThat(result.getType()).isEqualTo("APPLICATION_LOG");
        assertThat(result.getHost()).isEqualTo("12345");
        assertThat(result.getTimestamp()).isEqualTo(1491377495216L);
    }
}