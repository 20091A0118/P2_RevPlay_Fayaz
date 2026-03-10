package com.revplay.app.mapper;

import com.revplay.app.dto.PodcastEpisodeDTO;
import com.revplay.app.entity.PodcastEpisode;

public class PodcastEpisodeMapper {

    public static PodcastEpisodeDTO toDTO(PodcastEpisode episode) {
        if (episode == null)
            return null;
        PodcastEpisodeDTO dto = new PodcastEpisodeDTO();
        dto.setEpisodeId(episode.getEpisodeId());
        dto.setPodcastId(episode.getPodcastId());
        dto.setTitle(episode.getTitle());
        dto.setDurationSeconds(episode.getDurationSeconds());
        dto.setReleaseDate(episode.getReleaseDate());
        dto.setFileUrl(episode.getFileUrl());
        dto.setPlayCount(episode.getPlayCount());
        dto.setCoverImageUrl(episode.getCoverImageUrl());
        return dto;
    }

    public static PodcastEpisode toEntity(PodcastEpisodeDTO dto) {
        if (dto == null)
            return null;
        PodcastEpisode episode = new PodcastEpisode();
        episode.setEpisodeId(dto.getEpisodeId());
        episode.setPodcastId(dto.getPodcastId());
        episode.setTitle(dto.getTitle());
        episode.setDurationSeconds(dto.getDurationSeconds());
        episode.setReleaseDate(dto.getReleaseDate());
        episode.setFileUrl(dto.getFileUrl());
        episode.setPlayCount(dto.getPlayCount());
        episode.setCoverImageUrl(dto.getCoverImageUrl());
        return episode;
    }
}
