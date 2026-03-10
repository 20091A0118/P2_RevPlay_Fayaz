package com.revplay.app.rest;

import com.revplay.app.service.IPodcastService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class PodcastRestControllerTest {

    @Mock
    private IPodcastService service;

    @InjectMocks
    private PodcastRestController controller;

    @Test
    public void testControllerIsNotNull() {
        assertThat(controller).isNotNull();
    }
}
