package com.revplay.app.rest;

import com.revplay.app.service.ISongService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class FileUploadRestControllerTest {

    @Mock
    private ISongService service;

    @InjectMocks
    private FileUploadRestController controller;

    @Test
    public void testControllerIsNotNull() {
        assertThat(controller).isNotNull();
    }
}
