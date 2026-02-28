package com.revplay.app.mapper;

import com.revplay.app.dto.UserDTO;
import com.revplay.app.entity.UserAccount;

public class UserMapper {

    public static UserDTO toDTO(UserAccount user) {
        if (user == null)
            return null;
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setStatus(user.getStatus());
        return dto;
    }

    public static UserAccount toEntity(UserDTO dto) {
        if (dto == null)
            return null;
        UserAccount user = new UserAccount();
        user.setUserId(dto.getUserId());
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setStatus(dto.getStatus());
        return user;
    }
}
