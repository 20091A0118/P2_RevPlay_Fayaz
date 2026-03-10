package com.revplay.app.mapper;

import com.revplay.app.dto.FavoriteSongDTO;
import com.revplay.app.entity.FavoriteSong;

public class FavoriteSongMapper {

    public static FavoriteSongDTO toDTO(FavoriteSong favorite) {
        if (favorite == null)
            return null;
        FavoriteSongDTO dto = new FavoriteSongDTO();
        dto.setUserId(favorite.getUserId());
        dto.setSongId(favorite.getSongId());
        dto.setFavoritedAt(favorite.getFavoritedAt());
        return dto;
    }

    public static FavoriteSong toEntity(FavoriteSongDTO dto) {
        if (dto == null)
            return null;
        FavoriteSong favorite = new FavoriteSong();
        favorite.setUserId(dto.getUserId());
        favorite.setSongId(dto.getSongId());
        favorite.setFavoritedAt(dto.getFavoritedAt());
        return favorite;
    }
}
