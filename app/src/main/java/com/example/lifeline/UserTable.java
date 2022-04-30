package com.example.lifeline;

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

    @ColumnInfo(name = "profilePic")
    private String profilePic;




    public UserTable(@NonNull String name,@NonNull String sex, @NonNull int year,
                     @NonNull int month, @NonNull int day, @NonNull String city,
                     @NonNull String country, @NonNull int height, @NonNull int weight, String profilePic){
        this.name = name;
        this.sex = sex;
        this.year = year;
        this.month = month;
        this.day = day;
        this.city = city;
        this.country = country;
        this.height = height;
        this.weight = weight;
        this.profilePic = profilePic;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public String getSex(){
        return sex;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
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

    public String getProfilePic() {
        return profilePic;
    }

    @Override
    public String toString() {
        return name + ";" + sex + ";" + year +  month + ";" + day + ";" + city + ";" + country + ";" + height + ";" + weight + ";" + profilePic + "\n";
    }

}

