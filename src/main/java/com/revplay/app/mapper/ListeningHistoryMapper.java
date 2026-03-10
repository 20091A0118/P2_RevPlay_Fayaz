package com.revplay.app.mapper;

import com.revplay.app.dto.ListeningHistoryDTO;
import com.revplay.app.entity.ListeningHistory;

public class ListeningHistoryMapper {

    public static ListeningHistoryDTO toDTO(ListeningHistory history) {
        if (history == null)
            return null;
        ListeningHistoryDTO dto = new ListeningHistoryDTO();
        dto.setHistoryId(history.getHistoryId());
        dto.setUserId(history.getUserId());
        dto.setSongId(history.getSongId());
        dto.setSongTitle(history.getSongTitle());
        dto.setArtistName(history.getArtistName());
        dto.setPlayedAt(history.getPlayedAt());
        dto.setActionType(history.getActionType());
        return dto;
    }

    public static ListeningHistory toEntity(ListeningHistoryDTO dto) {
        if (dto == null)
            return null;
        ListeningHistory history = new ListeningHistory();
        history.setHistoryId(dto.getHistoryId());
        history.setUserId(dto.getUserId());
        history.setSongId(dto.getSongId());
        history.setPlayedAt(dto.getPlayedAt());
        history.setActionType(dto.getActionType());
        return history;
    }
}
