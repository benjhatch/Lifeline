package com.example.lifeline;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class AppViewModel extends AndroidViewModel {
    private MutableLiveData<User> userData;
    private MutableLiveData<Weather> weatherData;
    private MutableLiveData<StepCounterState> stepCounterState;
    private MutableLiveData<StepCount> stepCount;


    private Repository repository;

    public AppViewModel(Application application) {
        super(application);
        repository = Repository.getInstance(application);
        userData = repository.getUserData();
        weatherData = repository.getWeatherData();
        stepCounterState = repository.getStepCounterState();
        stepCount = repository.getStepCount();
    }

    public void loginUser(String name) {
        repository.loginUser(name);
    }

    public void logoutUser() {
        repository.logoutUser();
    }

    public void setUser(String name, String sex, int year, int month, int day,
                        String city, String country, int height, int weight, Bitmap profilePic) {
        repository.setUser(name, sex, year, month, day, city, country, height, weight, profilePic);
    }

    public void setLocation(double latitude, double longitude) {
        repository.setLocation(latitude, longitude);
    }

    public void setStepCounterOn(boolean on) {
        repository.setStepCounterState(on);
    }

    public void setStepCount(int count) {
        repository.setStepCount(count);
    }

    public LiveData<User> getUserData() {
        return userData;
    }

    public LiveData<Weather> getWeatherData() {
        return weatherData;
    }

    public LiveData<StepCounterState> getStepCounterState() { return stepCounterState; }

    public LiveData<StepCount> getStepCount() { return stepCount; }

    public double getLatitude() {
        return repository.getLatitude();
    }

    public double getLongitude() {
        return repository.getLongitude();
    }
}
