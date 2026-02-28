package com.revplay.app.service;

import com.revplay.app.repository.ISongRepo;
import com.revplay.app.entity.Song;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Mock implementation of ISongRepo for unit testing.
 */
public class MockSongDao implements ISongRepo {
    private Map<Integer, Song> songs = new HashMap<>();
    private int nextId = 1;

    // Custom methods from ISongRepo
    @Override
    public List<Song> findByArtistId(int artistId) {
        return songs.values().stream()
                .filter(s -> s.getArtistId() == artistId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Song> findByAlbumId(int albumId) {
        return songs.values().stream()
                .filter(s -> s.getAlbumId() != null && s.getAlbumId().equals(albumId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Song> findByGenreId(int genreId) {
        return songs.values().stream()
                .filter(s -> s.getGenreId() == genreId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Song> searchSongs(String keyword) {
        return songs.values().stream()
                .filter(s -> s.getTitle() != null && s.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Song> findTop10ByOrderByPlayCountDesc() {
        return songs.values().stream()
                .sorted((a, b) -> b.getPlayCount() - a.getPlayCount())
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public Long getTotalPlaysByArtistId(int artistId) {
        return songs.values().stream()
                .filter(s -> s.getArtistId() == artistId)
                .mapToLong(Song::getPlayCount)
                .sum();
    }

    // Legacy helpers for tests
    public boolean addSong(Song song) {
        save(song);
        return true;
    }

    public Song getSongById(int songId) {
        return findById(songId).orElse(null);
    }

    public List<Song> getSongsByArtistId(int artistId) {
        return findByArtistId(artistId);
    }

    public List<Song> getSongsByAlbumId(int albumId) {
        return findByAlbumId(albumId);
    }

    public List<Song> getSongsByGenreId(int genreId) {
        return findByGenreId(genreId);
    }

    public boolean updateSong(Song song) {
        save(song);
        return true;
    }

    public boolean deleteSong(int songId) {
        deleteById(songId);
        return true;
    }

    public boolean incrementPlayCount(int songId) {
        return findById(songId).map(s -> {
            s.setPlayCount(s.getPlayCount() + 1);
            save(s);
            return true;
        }).orElse(false);
    }

    public List<Song> getAllSongs() {
        return findAll();
    }

    public List<Song> getTopPlayedSongs(int limit) {
        return songs.values().stream()
                .sorted((a, b) -> b.getPlayCount() - a.getPlayCount())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public int countSongsByArtist(int artistId) {
        return (int) songs.values().stream().filter(s -> s.getArtistId() == artistId).count();
    }

    // JpaRepository methods
    @Override
    public <S extends Song> S save(S entity) {
        if (entity.getSongId() == 0) {
            entity.setSongId(nextId++);
        }
        songs.put(entity.getSongId(), entity);
        return entity;
    }

    @Override
    public Optional<Song> findById(Integer id) {
        return Optional.ofNullable(songs.get(id));
    }

    @Override
    public List<Song> findAll() {
        return new ArrayList<>(songs.values());
    }

    @Override
    public void deleteById(Integer id) {
        songs.remove(id);
    }

    @Override
    public List<Song> findAll(Sort sort) {
        return findAll();
    }

    @Override
    public Page<Song> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Song> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public boolean existsById(Integer id) {
        return songs.containsKey(id);
    }

    @Override
    public long count() {
        return songs.size();
    }

    @Override
    public void delete(Song entity) {
        deleteById(entity.getSongId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Integer> ids) {
    }

    @Override
    public void deleteAll(Iterable<? extends Song> entities) {
    }

    @Override
    public void deleteAll() {
        songs.clear();
    }

    @Override
    public void flush() {
    }

    @Override
    public <S extends Song> S saveAndFlush(S entity) {
        return save(entity);
    }

    @Override
    public <S extends Song> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Song> entities) {
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Integer> ids) {
    }

    @Override
    public void deleteAllInBatch() {
    }

    @Override
    public Song getOne(Integer id) {
        return getById(id);
    }

    @Override
    public Song getById(Integer id) {
        return findById(id).orElse(null);
    }

    @Override
    public Song getReferenceById(Integer id) {
        return findById(id).orElse(null);
    }

    @Override
    public <S extends Song> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Song> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Song> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Song> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Song> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Song> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Song, R> R findBy(Example<S> example,
            Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<Song> findAllById(Iterable<Integer> ids) {
        return null;
    }

    public void clear() {
        songs.clear();
        nextId = 1;
    }
}
