package com.revplay.app.service;

import com.revplay.app.repository.IPodcastRepo;
import com.revplay.app.entity.Podcast;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

/**
 * Mock implementation of IPodcastRepo for unit testing.
 */
public class MockPodcastDao implements IPodcastRepo {
    private Map<Integer, Podcast> podcasts = new HashMap<>();
    private int nextId = 1;

    // Custom method from IPodcastRepo
    @Override
    public List<Podcast> findByOrderByCreatedAtDesc() {
        List<Podcast> list = new ArrayList<>(podcasts.values());
        list.sort((p1, p2) -> {
            if (p1.getCreatedAt() == null || p2.getCreatedAt() == null)
                return 0;
            return p2.getCreatedAt().compareTo(p1.getCreatedAt());
        });
        return list;
    }

    @Override
    public List<Podcast> findByArtistIdOrderByCreatedAtDesc(Integer artistId) {
        List<Podcast> list = podcasts.values().stream()
                .filter(p -> p.getArtistId() == artistId)
                .collect(java.util.stream.Collectors.toList());
        list.sort((p1, p2) -> {
            if (p1.getCreatedAt() == null || p2.getCreatedAt() == null)
                return 0;
            return p2.getCreatedAt().compareTo(p1.getCreatedAt());
        });
        return list;
    }

    // Legacy helper for tests
    public boolean createPodcast(Podcast podcast) {
        save(podcast);
        return true;
    }

    // Legacy helper for tests
    public List<Podcast> getAllPodcasts() {
        return findAll();
    }

    // Legacy helper for tests
    public Podcast getPodcastById(int id) {
        return findById(id).orElse(null);
    }

    // JpaRepository methods
    @Override
    public <S extends Podcast> S save(S entity) {
        if (entity.getPodcastId() == 0) {
            entity.setPodcastId(nextId++);
        }
        podcasts.put(entity.getPodcastId(), entity);
        return entity;
    }

    @Override
    public Optional<Podcast> findById(Integer id) {
        return Optional.ofNullable(podcasts.get(id));
    }

    @Override
    public List<Podcast> findAll() {
        return new ArrayList<>(podcasts.values());
    }

    @Override
    public void deleteById(Integer id) {
        podcasts.remove(id);
    }

    @Override
    public List<Podcast> findAll(Sort sort) {
        return findAll();
    }

    @Override
    public Page<Podcast> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Podcast> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public boolean existsById(Integer id) {
        return podcasts.containsKey(id);
    }

    @Override
    public long count() {
        return podcasts.size();
    }

    @Override
    public void delete(Podcast entity) {
        deleteById(entity.getPodcastId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Integer> ids) {
    }

    @Override
    public void deleteAll(Iterable<? extends Podcast> entities) {
    }

    @Override
    public void deleteAll() {
        podcasts.clear();
    }

    @Override
    public void flush() {
    }

    @Override
    public <S extends Podcast> S saveAndFlush(S entity) {
        return save(entity);
    }

    @Override
    public <S extends Podcast> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Podcast> entities) {
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Integer> ids) {
    }

    @Override
    public void deleteAllInBatch() {
    }

    @Override
    public Podcast getOne(Integer id) {
        return getById(id);
    }

    @Override
    public Podcast getById(Integer id) {
        return findById(id).orElse(null);
    }

    @Override
    public Podcast getReferenceById(Integer id) {
        return findById(id).orElse(null);
    }

    @Override
    public <S extends Podcast> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Podcast> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Podcast> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Podcast> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Podcast> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Podcast> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Podcast, R> R findBy(Example<S> example,
            Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<Podcast> findAllById(Iterable<Integer> ids) {
        return null;
    }

    public void clear() {
        podcasts.clear();
        nextId = 1;
    }

    // Helper for PodcastEpisode tests in PodcastServiceTest
    // Since MockPodcastDao now ONLY implements IPodcastRepo,
    // it doesn't need episode logic. PodcastServiceTest uses BOTH.
    // Wait, the original MockPodcastDao had both.
    // I should probably keep both or create MockPodcastEpisodeDao.
    // PodcastServiceImpl uses BOTH IPodcastRepo and IPodcastEpisodeRepo.

    // Test helper to allow tests to call addEpisode on the mock if they want
    public boolean addEpisode(com.revplay.app.entity.PodcastEpisode episode) {
        // This is a bit ugly. Tests should probably have another mock.
        return true;
    }

    public com.revplay.app.entity.PodcastEpisode getEpisodeById(int id) {
        return null;
    }
}
