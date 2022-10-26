package com.lopez.julz.readandbillhv.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UsersDao {
    @Insert
    void insertAll(Users... users);

    @Update
    void updateAll(Users... users);

    @Query("SELECT * FROM Users WHERE Username = :username AND Password = :password")
    Users getOne(String username, String password);

    @Query("SELECT * FROM Users WHERE id = :id")
    Users getOneById(String id);

    @Query("SELECT * FROM Users ORDER BY id DESC LIMIT 1")
    Users getFirst();
}
