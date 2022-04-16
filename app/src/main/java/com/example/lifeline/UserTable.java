package com.example.lifeline;


import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "user_table")
public class UserTable {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @NonNull
    @ColumnInfo(name = "sex")
    private String sex;

    @ColumnInfo(name = "year")
    private int year;

    @ColumnInfo(name = "month")
    private int month;

    @ColumnInfo(name = "day")
    private int day;

    @NonNull
    @ColumnInfo(name = "city")
    private String city;

    @NonNull
    @ColumnInfo(name = "country")
    private String country;

    @ColumnInfo(name = "height")
    private int height;

    @ColumnInfo(name = "weight")
    private int weight;

    @ColumnInfo(name = "profilePicPath")
    private String profilePicPath;




    public UserTable(@NonNull String name,@NonNull String sex, @NonNull int year, @NonNull int month, @NonNull int day, @NonNull String city, @NonNull String country, @NonNull int height, @NonNull int weight, String profilePicPath){
        this.name = name;
        this.sex = sex;
        this.year = year;
        this.month = month;
        this.day = day;
        this.city = city;
        this.country = country;
        this.height = height;
        this.weight = weight;
        this.profilePicPath = profilePicPath;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setSex(String sex){
        this.sex = sex;
    }

    public String getSex(){
        return sex;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getCity() {
        return city;
    }

    public void setCity(@NonNull String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(@NonNull String country) {
        this.country = country;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getProfilePicPath() {
        return profilePicPath;
    }

    public void setProfilePicPath(String profilePic) {
        this.profilePicPath = profilePicPath;
    }
}

