package com.example.lifeline;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "weather_table")
public class WeatherTable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "location")
    private String location;

    @ColumnInfo(name = "temperature")
    private String temperature;

    @ColumnInfo(name = "feels_like")
    private String feelsLike;

    @ColumnInfo(name = "skies")
    private String skies;

    @ColumnInfo(name = "wind")
    private String wind;

    public WeatherTable(@NonNull String location, String temperature, String feelsLike, String skies, String wind) {
        this.location = location;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.skies = skies;
        this.wind = wind;
    }


    public String getLocation() { return location; }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setFeelsLike(String feelsLike) {
        this.feelsLike = feelsLike;
    }

    public String getFeelsLike() {
        return feelsLike;
    }

    public void setSkies(String skies) {
        this.skies = skies;
    }

    public String getSkies() {
        return skies;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getWind() {
        return wind;
    }
}
