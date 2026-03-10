package com.revplay.app.mapper;

import com.revplay.app.dto.ArtistDTO;
import com.revplay.app.entity.ArtistAccount;

public class ArtistMapper {

    public static ArtistDTO toDTO(ArtistAccount artist) {
        if (artist == null)
            return null;
        ArtistDTO dto = new ArtistDTO();
        dto.setArtistId(artist.getArtistId());
        dto.setStageName(artist.getStageName());
        dto.setEmail(artist.getEmail());
        dto.setBio(artist.getBio());
        dto.setGenre(artist.getGenre());
        dto.setInstagramLink(artist.getInstagramLink());
        dto.setYoutubeLink(artist.getYoutubeLink());
        dto.setSpotifyLink(artist.getSpotifyLink());
        dto.setStatus(artist.getStatus());
        dto.setProfilePicture(artist.getProfilePicture());
        dto.setBannerImage(artist.getBannerImage());
        dto.setTwitterLink(artist.getTwitterLink());
        dto.setWebsiteLink(artist.getWebsiteLink());
        return dto;
    }

    public static ArtistAccount toEntity(ArtistDTO dto) {
        if (dto == null)
            return null;
        ArtistAccount artist = new ArtistAccount();
        artist.setArtistId(dto.getArtistId());
        artist.setStageName(dto.getStageName());
        artist.setEmail(dto.getEmail());
        artist.setBio(dto.getBio());
        artist.setGenre(dto.getGenre());
        artist.setInstagramLink(dto.getInstagramLink());
        artist.setYoutubeLink(dto.getYoutubeLink());
        artist.setSpotifyLink(dto.getSpotifyLink());
        artist.setStatus(dto.getStatus());
        artist.setProfilePicture(dto.getProfilePicture());
        artist.setBannerImage(dto.getBannerImage());
        artist.setTwitterLink(dto.getTwitterLink());
        artist.setWebsiteLink(dto.getWebsiteLink());
        return artist;
    }
}
