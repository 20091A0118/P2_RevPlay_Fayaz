package com.revplay.app.mapper;

import com.revplay.app.dto.PlaylistDTO;
import com.revplay.app.entity.Playlist;

public class PlaylistMapper {

    public static PlaylistDTO toDTO(Playlist playlist) {
        if (playlist == null)
            return null;
        PlaylistDTO dto = new PlaylistDTO();
        dto.setPlaylistId(playlist.getPlaylistId());
        dto.setUserId(playlist.getUserId());
        dto.setName(playlist.getName());
        dto.setDescription(playlist.getDescription());
        dto.setPrivacyStatus(playlist.getPrivacyStatus());
        dto.setCreatedAt(playlist.getCreatedAt());
        dto.setUpdatedAt(playlist.getUpdatedAt());
        dto.setSongCount(playlist.getSongCount());
        return dto;
    }

    public static Playlist toEntity(PlaylistDTO dto) {
        if (dto == null)
            return null;
        Playlist playlist = new Playlist();
        playlist.setPlaylistId(dto.getPlaylistId());
        playlist.setUserId(dto.getUserId());
        playlist.setName(dto.getName());
        playlist.setDescription(dto.getDescription());
        playlist.setPrivacyStatus(dto.getPrivacyStatus());
        playlist.setCreatedAt(dto.getCreatedAt());
        playlist.setUpdatedAt(dto.getUpdatedAt());
        playlist.setSongCount(dto.getSongCount());
        return playlist;
    }
}
