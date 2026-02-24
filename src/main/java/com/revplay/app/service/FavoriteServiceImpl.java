package com.revplay.app.service;

import com.revplay.app.entity.Song;
import com.revplay.app.repository.IFavoriteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteServiceImpl implements IFavoriteService {

    @Autowired
    private IFavoriteRepo favoriteRepo;

    @Override
    public boolean addFavorite(int userId, int songId) {
        return favoriteRepo.addFavorite(userId, songId);
    }

    @Override
    public boolean removeFavorite(int userId, int songId) {
        return favoriteRepo.removeFavorite(userId, songId);
    }

    @Override
    public List<Song> getFavoriteSongs(int userId) {
        return favoriteRepo.getFavoriteSongs(userId);
    }

    @Override
    public boolean isFavorite(int userId, int songId) {
        return favoriteRepo.isFavorite(userId, songId);
    }
}
