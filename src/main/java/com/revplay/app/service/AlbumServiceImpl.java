package com.revplay.app.service;

import com.revplay.app.repository.IAlbumRepo;
import com.revplay.app.repository.IArtistRepo;
import com.revplay.app.entity.Album;
import com.revplay.app.entity.ArtistAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AlbumServiceImpl implements IAlbumService {

    @Autowired
    private IAlbumRepo albumDao;

    @Autowired
    private IArtistRepo artistRepo;

    public void setAlbumDao(IAlbumRepo albumDao) {
        this.albumDao = albumDao;
    }

    private void enrichAlbums(List<Album> albums) {
        if (albums == null || albums.isEmpty())
            return;
        Map<Integer, String> artistNames = artistRepo.findAll().stream()
                .collect(Collectors.toMap(ArtistAccount::getArtistId, ArtistAccount::getStageName, (a, b) -> a));
        for (Album a : albums) {
            a.setArtistName(artistNames.getOrDefault(a.getArtistId(), "Unknown"));
        }
    }

    public boolean createAlbum(Album album) {
        try {
            if (album.getCreatedAt() == null)
                album.setCreatedAt(LocalDateTime.now());
            albumDao.save(album);
            return true;
        } catch (Exception e) {
            System.err.println("Album creation failed: " + e.getMessage());
            return false;
        }
    }

    public Album getAlbumById(int albumId) {
        return albumDao.findById(albumId).orElse(null);
    }

    public List<Album> getAlbumsByArtist(int artistId) {
        List<Album> albums = albumDao.findByArtistId(artistId);
        enrichAlbums(albums);
        return albums;
    }

    public List<Album> getAllAlbums() {
        List<Album> albums = albumDao.findAll();
        enrichAlbums(albums);
        return albums;
    }

    public boolean updateAlbum(Album album) {
        try {
            albumDao.save(album);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteAlbum(int albumId) {
        try {
            albumDao.deleteById(albumId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
