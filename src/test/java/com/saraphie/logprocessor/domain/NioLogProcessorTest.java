package com.saraphie.logprocessor.domain;

import com.saraphie.logprocessor.exception.ProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NioLogProcessorTest {

    @Mock
    EventExtractor eventExtractor;

    @Mock
    EventProcessor eventProcessor;

    @InjectMocks
    NioLogProcessor nioLogProcessor;


    @Test
    @DisplayName("when invoked with a correct file, events are processed")
    void process() {
        // given
        URL url = getClass().getClassLoader().getResource("testlog.txt");

        String filename = url.getFile();

        // when
        nioLogProcessor.process(filename);

        // then
        verify(eventExtractor, times(4)).extractEvent(any());
        verify(eventProcessor, times(4)).processEvent(any());
    }

    @Test
    @DisplayName("when invoked with a correct file, after encountering error, processing stops")
    void process_error() {
        // given
        URL url = getClass().getClassLoader().getResource("testlog.txt");

        String filename = url.getFile();

        when(eventExtractor.extractEvent(any())).thenThrow(new RuntimeException("exc"));

        // when
        Throwable result = assertThrows(ProcessingException.class, () -> nioLogProcessor.process(filename));

        // then
        assertInstanceOf(ProcessingException.class, result);
        verify(eventProcessor, never()).processEvent(any());
    }
}