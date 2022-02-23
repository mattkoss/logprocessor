package com.saraphie.logprocessor.domain;

import com.saraphie.logprocessor.repository.EventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersistingEventProcessorTest {

    @Mock
    EventRepository eventRepository;

    @InjectMocks
    PersistingEventProcessor processor;

    @Test
    @DisplayName("when this is a first time entry for a given id, persist the event as-is")
    void processEvent_first() {
        // given
        Event event = Event.builder()
                .host("host1")
                .type("type1")
                .build();

        // when
        processor.processEvent(event);

        // then
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    @DisplayName("when this is a second entry for a given id, merge and persist the event")
    void processEvent_secondNoAlert() {
        // given
        Event event = Event.builder()
                .id("id1")
                .host("host1")
                .type("type1")
                .timestamp(1000L)
                .build();
        Event event2 = Event.builder()
                .id("id1")
                .host("host1")
                .type("type1")
                .timestamp(5000L)
                .build();
        when(eventRepository.findById(any())).thenReturn(Optional.of(event));

        // when
        processor.processEvent(event2);

        // then
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    @DisplayName("when this is a second entry for a given id, merge and persist the event, and flag an alert")
    void processEvent_secondWithAlert() {
        // given
        Event event = Event.builder()
                .id("id1")
                .host("host1")
                .type("type1")
                .state(State.STARTED)
                .timestamp(1000L)
                .build();
        Event event2 = Event.builder()
                .id("id1")
                .host("host1")
                .type("type1")
                .state(State.FINISHED)
                .timestamp(6000L)
                .build();
        when(eventRepository.findById("id1")).thenReturn(Optional.of(event));

        // when
        processor.processEvent(event2);

        // then
        verify(eventRepository, times(1)).save(Event.builder()
                .id("id1")
                .host("host1")
                .type("type1")
                .duration(5000L)
                .alert(true)
                .build());
    }

    @Test
    @DisplayName("when this is a second entry for a given id, merge and persist the event, and flag an alert")
    void processEvent_secondWithAlertUpside() {
        // given
        Event event = Event.builder()
                .id("id1")
                .host("host1")
                .type("type1")
                .state(State.FINISHED)
                .timestamp(6000L)
                .build();
        Event event2 = Event.builder()
                .id("id1")
                .host("host1")
                .state(State.STARTED)
                .type("type1")
                .timestamp(1000L)
                .build();
        when(eventRepository.findById(any())).thenReturn(Optional.of(event));

        // when
        processor.processEvent(event2);

        // then
        verify(eventRepository, times(1)).save(Event.builder()
                .id("id1")
                .host("host1")
                .type("type1")
                .duration(5000L)
                .alert(true)
                .build());
    }

    @Test
    @DisplayName("when this is a second entry for a given id, merge and persist the event, reload due to concurrency")
    void processEvent_secondNoAlertWithReload() {
        // given
        Event event = Event.builder()
                .id("id1")
                .host("host1")
                .type("type1")
                .state(State.STARTED)
                .timestamp(1000L)
                .build();
        Event event2 = Event.builder()
                .id("id1")
                .host("host1")
                .type("type1")
                .state(State.FINISHED)
                .timestamp(6000L)
                .build();
        when(eventRepository.findById("id1"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(event));
        when(eventRepository.save(event2))
                .thenThrow(new DataIntegrityViolationException("exc"))
                .thenReturn(event);

        // when
        processor.processEvent(event2);

        // then
        verify(eventRepository, times(1)).save(event2);
        verify(eventRepository, times(1)).save(Event.builder()
                .id("id1")
                .host("host1")
                .type("type1")
                .duration(5000L)
                .alert(true)
                .build());
    }
}