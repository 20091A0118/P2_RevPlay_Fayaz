package com.revplay.app.service;

import com.revplay.app.repository.IGenreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class GenreServiceImplTest {

    @Mock
    private IGenreRepository repository;

    @InjectMocks
    private GenreServiceImpl service;

    @Test
    public void testServiceIsNotNull() {
        assertThat(service).isNotNull();
    }
}
