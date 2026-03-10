package com.revplay.app.rest;

import com.revplay.app.service.IHistoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class HistoryRestControllerTest {

    @Mock
    private IHistoryService service;

    @InjectMocks
    private HistoryRestController controller;

    @Test
    public void testControllerIsNotNull() {
        assertThat(controller).isNotNull();
    }
}
