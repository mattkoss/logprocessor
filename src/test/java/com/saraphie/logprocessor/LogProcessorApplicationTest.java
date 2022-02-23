package com.saraphie.logprocessor;

import com.saraphie.logprocessor.domain.LogProcessor;
import com.saraphie.logprocessor.exception.ProcessingException;
import com.saraphie.logprocessor.repository.EventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogProcessorApplicationTest {

    @InjectMocks
    LogProcessorApplication application;

    @Mock
    LogProcessor logProcessor;

    @Mock
    EventRepository eventRepository;

    @Test
    @DisplayName("when correct parameters are passed, process the file")
    void run() {
        // given
        String fileName = "testfile.txt";

        // when
        application.run(fileName);

        // then
        verify(logProcessor, times(1)).process(fileName);
    }

    @Test
    @DisplayName("when no parameters are passed, stop")
    void run_empty() {
        // given

        // when
        application.run();

        // then
        verify(logProcessor, never()).process(any());
    }

    @Test
    @DisplayName("when processing error occurs, stop")
    void run_error() {
        // given
        String fileName = "testfile.txt";
        doThrow(new ProcessingException("exc", new Exception("exd2"))).when(logProcessor).process(fileName);

        // when
        application.run(fileName);

        // then
    }
}