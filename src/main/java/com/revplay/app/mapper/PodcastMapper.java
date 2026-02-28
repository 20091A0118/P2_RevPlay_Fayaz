package com.revplay.app.mapper;

import com.revplay.app.dto.PodcastDTO;
import com.revplay.app.entity.Podcast;

public class PodcastMapper {

    public static PodcastDTO toDTO(Podcast podcast) {
        if (podcast == null)
            return null;
        PodcastDTO dto = new PodcastDTO();
        dto.setPodcastId(podcast.getPodcastId());
        dto.setTitle(podcast.getTitle());
        dto.setHostName(podcast.getHostName());
        dto.setCategory(podcast.getCategory());
        dto.setDescription(podcast.getDescription());
        dto.setCoverImageUrl(podcast.getCoverImageUrl());
        dto.setCreatedAt(podcast.getCreatedAt());
        return dto;
    }

    public static Podcast toEntity(PodcastDTO dto) {
        if (dto == null)
            return null;
        Podcast podcast = new Podcast();
        podcast.setPodcastId(dto.getPodcastId());
        podcast.setTitle(dto.getTitle());
        podcast.setHostName(dto.getHostName());
        podcast.setCategory(dto.getCategory());
        podcast.setDescription(dto.getDescription());
        podcast.setCoverImageUrl(dto.getCoverImageUrl());
        podcast.setCreatedAt(dto.getCreatedAt());
        return podcast;
    }
}
