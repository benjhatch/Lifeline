package com.example.lifeline;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

@Dao
public interface WeatherDao {

    @RawQuery
    int checkpoint(SupportSQLiteQuery supportSQLiteQuery);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(WeatherTable weatherTable);

    @Query("DELETE FROM weather_table")
    void deleteAll();

    @Query("SELECT * from weather_table ORDER BY location ASC")
    LiveData<List<WeatherTable>> getAll();

    @Query("SELECT * from weather_table WHERE location = :location")
    WeatherTable readWeather(String location);
}
