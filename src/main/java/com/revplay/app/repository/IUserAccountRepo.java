package com.revplay.app.repository;

import com.revplay.app.entity.UserAccount;
import java.util.List;

public interface IUserAccountRepo {
    boolean addUserAccount(UserAccount user);

    boolean updateUserAccount(UserAccount user);

    UserAccount getUserAccount(int userId);

    UserAccount getUserAccountByEmail(String email);

    List<UserAccount> getAllUserAccounts();

    int getPlaylistCount(int userId);

    int getFavoriteCount(int userId);
}
