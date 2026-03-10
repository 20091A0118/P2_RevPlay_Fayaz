package com.revplay.app.mapper;

import com.revplay.app.dto.SongDTO;
import com.revplay.app.entity.Song;

public class SongMapper {

    public static SongDTO toDTO(Song song) {
        if (song == null)
            return null;
        SongDTO dto = new SongDTO();
        dto.setSongId(song.getSongId());
        dto.setTitle(song.getTitle());
        dto.setArtistName(song.getArtistName());
        dto.setAlbumTitle(song.getAlbumTitle());
        dto.setGenreName(song.getGenreName());
        dto.setDurationSeconds(song.getDurationSeconds());
        dto.setReleaseDate(song.getReleaseDate());
        dto.setFileUrl(song.getFileUrl());
        dto.setCoverImageUrl(song.getCoverImageUrl());
        dto.setPlayCount(song.getPlayCount());
        dto.setIsActive(song.getIsActive());
        return dto;
    }

    public static Song toEntity(SongDTO dto) {
        if (dto == null)
            return null;
        Song song = new Song();
        song.setSongId(dto.getSongId());
        song.setTitle(dto.getTitle());
        song.setDurationSeconds(dto.getDurationSeconds());
        song.setReleaseDate(dto.getReleaseDate());
        song.setFileUrl(dto.getFileUrl());
        song.setCoverImageUrl(dto.getCoverImageUrl());
        song.setPlayCount(dto.getPlayCount());
        song.setIsActive(dto.getIsActive());
        return song;
    }
}
