package com.revplay.app.repository;

import com.revplay.app.entity.Song;
import java.util.List;

public interface IFavoriteRepo {
    boolean addFavorite(int userId, int songId);

    boolean removeFavorite(int userId, int songId);

    List<Song> getFavoriteSongs(int userId);

    boolean isFavorite(int userId, int songId);
}
