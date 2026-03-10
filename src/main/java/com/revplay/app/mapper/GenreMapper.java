package com.revplay.app.mapper;

import com.revplay.app.dto.GenreDTO;
import com.revplay.app.entity.Genre;

public class GenreMapper {

    public static GenreDTO toDTO(Genre genre) {
        if (genre == null)
            return null;
        return new GenreDTO(genre.getGenreId(), genre.getGenreName());
    }

    public static Genre toEntity(GenreDTO dto) {
        if (dto == null)
            return null;
        return new Genre(dto.getGenreId(), dto.getGenreName());
    }
}
