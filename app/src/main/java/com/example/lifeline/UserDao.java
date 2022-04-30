package com.example.lifeline;


import androidx.lifecycle.LiveData;
import androidx.room.*;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

@Dao
public interface UserDao {

    @RawQuery
    int checkpoint(SupportSQLiteQuery supportSQLiteQuery);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserTable userTable);

    @Query("DELETE FROM user_table")
    void deleteAll();

    @Query("SELECT * from user_table ORDER BY name ASC")
    List<UserTable> getAll();

    @Query("SELECT * from user_table WHERE name = :name")
    UserTable readUser(String name);
}

