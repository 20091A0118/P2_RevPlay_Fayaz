package com.revplay.app.rest;

import com.revplay.app.service.IUserAccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UserRestControllerTest {

    @Mock
    private IUserAccountService service;

    @InjectMocks
    private UserRestController controller;

    @Test
    public void testControllerIsNotNull() {
        assertThat(controller).isNotNull();
    }
}
