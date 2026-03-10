package com.revplay.app.mapper;

import com.revplay.app.dto.AlbumDTO;
import com.revplay.app.entity.Album;

public class AlbumMapper {

    public static AlbumDTO toDTO(Album album) {
        if (album == null)
            return null;
        AlbumDTO dto = new AlbumDTO();
        dto.setAlbumId(album.getAlbumId());
        dto.setTitle(album.getTitle());
        dto.setArtistName(album.getArtistName());
        dto.setReleaseDate(album.getReleaseDate());
        dto.setDescription(album.getDescription());
        dto.setCoverImageUrl(album.getCoverImageUrl());
        return dto;
    }

    public static Album toEntity(AlbumDTO dto) {
        if (dto == null)
            return null;
        Album album = new Album();
        album.setAlbumId(dto.getAlbumId());
        album.setTitle(dto.getTitle());
        album.setReleaseDate(dto.getReleaseDate());
        album.setDescription(dto.getDescription());
        album.setCoverImageUrl(dto.getCoverImageUrl());
        return album;
    }
}
