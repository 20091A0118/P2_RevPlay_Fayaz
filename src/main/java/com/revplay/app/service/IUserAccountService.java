package com.revplay.app.service;

import com.revplay.app.entity.UserAccount;

import java.util.List;

public interface IUserAccountService {
    boolean registerUser(UserAccount user);

    UserAccount getUserById(int userId);

    UserAccount getUserByEmail(String email);

    List<UserAccount> getAllUsers();

    boolean updateUser(UserAccount user);

    int getPlaylistCount(int userId);

    int getFavoriteCount(int userId);
}
