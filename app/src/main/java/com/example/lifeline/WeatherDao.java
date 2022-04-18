package com.example.lifeline;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import java.util.List;

@Dao
public interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(WeatherTable weatherTable);

    @Query("DELETE FROM weather_table")
    void deleteAll();

    @Query("SELECT * from weather_table ORDER BY location ASC")
    LiveData<List<WeatherTable>> getAll();

    @Query("SELECT * from weather_table WHERE location = :location")
    WeatherTable readWeather(String location);
}
