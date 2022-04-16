package com.example.lifeline;


import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.util.List;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserTable userTable);

    @Query("DELETE FROM user_table")
    void deleteAll();

    @Query("SELECT * from user_table ORDER BY name ASC")
    LiveData<List<UserTable>> getAll();
}

