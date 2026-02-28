package com.revplay.app.service;

import com.revplay.app.repository.IPodcastEpisodeRepo;
import com.revplay.app.entity.PodcastEpisode;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Mock implementation of IPodcastEpisodeRepo for unit testing.
 */
public class MockPodcastEpisodeDao implements IPodcastEpisodeRepo {
    private Map<Integer, PodcastEpisode> episodes = new HashMap<>();
    private int nextId = 1;

    @Override
    public List<PodcastEpisode> findByPodcastIdOrderByReleaseDateDesc(int podcastId) {
        return episodes.values().stream()
                .filter(e -> e.getPodcastId() == podcastId)
                .sorted((e1, e2) -> {
                    if (e1.getReleaseDate() == null || e2.getReleaseDate() == null)
                        return 0;
                    return e2.getReleaseDate().compareTo(e1.getReleaseDate());
                })
                .collect(Collectors.toList());
    }

    @Override
    public long getTotalPlaysByArtistId(int artistId) {
        return 0L;
    }

    // JpaRepository methods
    @Override
    public <S extends PodcastEpisode> S save(S entity) {
        if (entity.getEpisodeId() == 0) {
            entity.setEpisodeId(nextId++);
        }
        episodes.put(entity.getEpisodeId(), entity);
        return entity;
    }

    @Override
    public Optional<PodcastEpisode> findById(Integer id) {
        return Optional.ofNullable(episodes.get(id));
    }

    @Override
    public List<PodcastEpisode> findAll() {
        return new ArrayList<>(episodes.values());
    }

    @Override
    public void deleteById(Integer id) {
        episodes.remove(id);
    }

    @Override
    public List<PodcastEpisode> findAll(Sort sort) {
        return findAll();
    }

    @Override
    public Page<PodcastEpisode> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public <S extends PodcastEpisode> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public boolean existsById(Integer id) {
        return episodes.containsKey(id);
    }

    @Override
    public long count() {
        return episodes.size();
    }

    @Override
    public void delete(PodcastEpisode entity) {
        deleteById(entity.getEpisodeId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Integer> ids) {
    }

    @Override
    public void deleteAll(Iterable<? extends PodcastEpisode> entities) {
    }

    @Override
    public void deleteAll() {
        episodes.clear();
    }

    @Override
    public void flush() {
    }

    @Override
    public <S extends PodcastEpisode> S saveAndFlush(S entity) {
        return save(entity);
    }

    @Override
    public <S extends PodcastEpisode> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<PodcastEpisode> entities) {
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Integer> ids) {
    }

    @Override
    public void deleteAllInBatch() {
    }

    @Override
    public PodcastEpisode getOne(Integer id) {
        return getById(id);
    }

    @Override
    public PodcastEpisode getById(Integer id) {
        return findById(id).orElse(null);
    }

    @Override
    public PodcastEpisode getReferenceById(Integer id) {
        return findById(id).orElse(null);
    }

    @Override
    public <S extends PodcastEpisode> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends PodcastEpisode> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends PodcastEpisode> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends PodcastEpisode> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends PodcastEpisode> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends PodcastEpisode> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends PodcastEpisode, R> R findBy(Example<S> example,
            Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<PodcastEpisode> findAllById(Iterable<Integer> ids) {
        return null;
    }

    public void clear() {
        episodes.clear();
        nextId = 1;
    }
}
