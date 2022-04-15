package com.example.lifeline;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.os.HandlerCompat;
import androidx.lifecycle.MutableLiveData;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

public class Repository {
    private static Repository instance;
    private final MutableLiveData<User> userData = new MutableLiveData<>();
    private final MutableLiveData<Weather> weatherData = new MutableLiveData<>();

    private double latitude;
    private double longitude;
    private boolean locationSet = false;

    private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=imperial";
    private static final String key = "113d3ab578badceb2636da939d5d280f";

    private Repository(Application application) {
        if (locationSet)
            loadWeather();
    }

    public static synchronized Repository getInstance(Application application) {
        if (instance == null)
            instance = new Repository(application);
        return instance;
    }

    public void setLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        locationSet = true;
        loadWeather();
    }

    public void setUser(String name, String sex, int year, int month, int day,
                        String city, String country, int height, int weight, Bitmap profilePic) {
        User user = new User();
        user.setName(name);
        user.setSex(sex);
        user.setYear(year);
        user.setMonth(month);
        user.setDay(day);
        user.setCity(city);
        user.setCountry(country);
        user.setHeight(height);
        user.setWeight(weight);
        user.setProfilePic(profilePic);

        userData.setValue(user);
    }

    public MutableLiveData<User> getUserData() {
        return userData;
    }

    public MutableLiveData<Weather> getWeatherData() {
        return weatherData;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void loadWeather() {
        AndroidNetworking.get(String.format(OPEN_WEATHER_MAP_API, latitude, longitude))
                .addHeaders("x-api-key", key)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());
                        mainThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("json", response.toString());
                                try {
                                    String temperature = response.getJSONObject("main").getDouble("temp") + " degrees";
                                    String feelsLike = response.getJSONObject("main").getDouble("feels_like") + " degrees";
                                    String wind = response.getJSONObject("wind").getDouble("speed") + " mph";
                                    Log.d("wind", wind);
                                    JSONObject weatherObject = response.getJSONArray("weather").getJSONObject(0);
                                    String skies = weatherObject.getString("main");

                                    Weather weather = new Weather();
                                    weather.setTemperature(temperature);
                                    weather.setFeelsLike(feelsLike);
                                    weather.setWind(wind);
                                    weather.setSkies(skies);

                                    weatherData.setValue(weather);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("json", "error: ");
                    }
                });
    }



}
