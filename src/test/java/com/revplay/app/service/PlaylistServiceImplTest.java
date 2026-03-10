package com.revplay.app.service;

import com.revplay.app.repository.IPlaylistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class PlaylistServiceImplTest {

    @Mock
    private IPlaylistRepository repository;

    @InjectMocks
    private PlaylistServiceImpl service;

    @Test
    public void testServiceIsNotNull() {
        assertThat(service).isNotNull();
    }
}
