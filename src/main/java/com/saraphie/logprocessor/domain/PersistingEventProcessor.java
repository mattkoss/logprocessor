package com.saraphie.logprocessor.domain;

import com.saraphie.logprocessor.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class PersistingEventProcessor implements EventProcessor {

    private final EventRepository eventRepository;

    public PersistingEventProcessor(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void processEvent(Event event) {
        log.debug("got event: {}", event);

        try {
            eventRepository.save(eventRepository.findById(event.getId())
                    .map(value -> mergeEvent(value, event)).orElse(event));
        } catch (DataIntegrityViolationException ex) {
            log.info("Got duplicate key during parallel processing, reloading: " + event.getId());
            eventRepository.findById(event.getId()).map(ev -> eventRepository.save(mergeEvent(ev, event)));
        }
    }

    private Event mergeEvent(Event existingEvent, Event event) {
        if (!Objects.equals(existingEvent.getState(), event.getState())) {
            existingEvent.setDuration(Math.abs(existingEvent.getTimestamp() - event.getTimestamp()));
            // clear timestamp as it is needed after merge
            existingEvent.setState(null);
            existingEvent.setTimestamp(null);
            existingEvent.setAlert(existingEvent.getDuration() > 4000);
        }

        return existingEvent;
    }
}
