package com.revplay.app.service;

import com.revplay.app.repository.IPlaylistRepo;
import com.revplay.app.entity.Playlist;
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
 * Mock implementation of IPlaylistRepo for unit testing.
 */
public class MockPlaylistDao implements IPlaylistRepo {
    private Map<Integer, Playlist> playlists = new HashMap<>();
    private Map<Integer, List<Integer>> playlistSongs = new HashMap<>(); // Standardizing if needed
    private int nextId = 1;

    // Custom methods from IPlaylistRepo
    @Override
    public List<Playlist> findByUserIdOrderByUpdatedAtDesc(int userId) {
        return playlists.values().stream()
                .filter(p -> p.getUserId() == userId)
                .sorted((p1, p2) -> {
                    if (p1.getUpdatedAt() == null || p2.getUpdatedAt() == null)
                        return 0;
                    return p2.getUpdatedAt().compareTo(p1.getUpdatedAt());
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Playlist> findByPrivacyStatusOrderByUpdatedAtDesc(String privacyStatus) {
        return playlists.values().stream()
                .filter(p -> privacyStatus.equalsIgnoreCase(p.getPrivacyStatus()))
                .sorted((p1, p2) -> {
                    if (p1.getUpdatedAt() == null || p2.getUpdatedAt() == null)
                        return 0;
                    return p2.getUpdatedAt().compareTo(p1.getUpdatedAt());
                })
                .collect(Collectors.toList());
    }

    @Override
    public long countByUserId(int userId) {
        return playlists.values().stream()
                .filter(p -> p.getUserId() == userId)
                .count();
    }

    // Legacy helpers for tests
    public boolean createPlaylist(Playlist playlist) {
        save(playlist);
        return true;
    }

    public boolean deletePlaylist(int playlistId) {
        deleteById(playlistId);
        return true;
    }

    public boolean updatePlaylist(Playlist playlist) {
        save(playlist);
        return true;
    }

    public boolean addSongToPlaylist(int playlistId, int songId) {
        Playlist p = playlists.get(playlistId);
        if (p != null) {
            Song song = new Song();
            song.setSongId(songId);
            if (!p.getSongs().contains(song)) {
                p.getSongs().add(song);
            }
            return true;
        }
        return false;
    }

    public boolean removeSongFromPlaylist(int playlistId, int songId) {
        Playlist p = playlists.get(playlistId);
        if (p != null) {
            return p.getSongs().removeIf(s -> s.getSongId() == songId);
        }
        return false;
    }

    public List<Playlist> getPlaylistsByUserId(int userId) {
        return findByUserIdOrderByUpdatedAtDesc(userId);
    }

    public Playlist getPlaylistById(int playlistId) {
        return findById(playlistId).orElse(null);
    }

    public List<Song> getSongsInPlaylist(int playlistId) {
        return new ArrayList<>();
    }

    public List<Playlist> getPublicPlaylists() {
        return findByPrivacyStatusOrderByUpdatedAtDesc("PUBLIC");
    }

    // JpaRepository methods
    @Override
    public <S extends Playlist> S save(S entity) {
        if (entity.getPlaylistId() == 0) {
            entity.setPlaylistId(nextId++);
        }
        playlists.put(entity.getPlaylistId(), entity);
        return entity;
    }

    @Override
    public Optional<Playlist> findById(Integer id) {
        return Optional.ofNullable(playlists.get(id));
    }

    @Override
    public List<Playlist> findAll() {
        return new ArrayList<>(playlists.values());
    }

    @Override
    public void deleteById(Integer id) {
        playlists.remove(id);
    }

    @Override
    public List<Playlist> findAll(Sort sort) {
        return findAll();
    }

    @Override
    public Page<Playlist> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Playlist> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public boolean existsById(Integer id) {
        return playlists.containsKey(id);
    }

    @Override
    public long count() {
        return playlists.size();
    }

    @Override
    public void delete(Playlist entity) {
        deleteById(entity.getPlaylistId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Integer> ids) {
    }

    @Override
    public void deleteAll(Iterable<? extends Playlist> entities) {
    }

    @Override
    public void deleteAll() {
        playlists.clear();
    }

    @Override
    public void flush() {
    }

    @Override
    public <S extends Playlist> S saveAndFlush(S entity) {
        return save(entity);
    }

    @Override
    public <S extends Playlist> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Playlist> entities) {
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Integer> ids) {
    }

    @Override
    public void deleteAllInBatch() {
    }

    @Override
    public Playlist getOne(Integer id) {
        return getById(id);
    }

    @Override
    public Playlist getById(Integer id) {
        return findById(id).orElse(null);
    }

    @Override
    public Playlist getReferenceById(Integer id) {
        return findById(id).orElse(null);
    }

    @Override
    public <S extends Playlist> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Playlist> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Playlist> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Playlist> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Playlist> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Playlist> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Playlist, R> R findBy(Example<S> example,
            Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<Playlist> findAllById(Iterable<Integer> ids) {
        return null;
    }

    public void clear() {
        playlists.clear();
        nextId = 1;
    }
}
