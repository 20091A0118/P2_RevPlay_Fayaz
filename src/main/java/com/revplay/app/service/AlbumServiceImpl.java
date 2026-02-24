package com.revplay.app.service;

import com.revplay.app.entity.Album;
import com.revplay.app.repository.IAlbumRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlbumServiceImpl implements IAlbumService {

    @Autowired
    private IAlbumRepo albumRepo;

    @Override
    public boolean createAlbum(Album album) {
        return albumRepo.createAlbum(album);
    }

    @Override
    public Album getAlbumById(int albumId) {
        return albumRepo.getAlbumById(albumId);
    }

    @Override
    public List<Album> getAlbumsByArtistId(int artistId) {
        return albumRepo.getAlbumsByArtistId(artistId);
    }

    @Override
    public List<Album> getAllAlbums() {
        return albumRepo.getAllAlbums();
    }

    @Override
    public boolean updateAlbum(Album album) {
        return albumRepo.updateAlbum(album);
    }

    @Override
    public boolean deleteAlbum(int albumId) {
        return albumRepo.deleteAlbum(albumId);
    }
}
