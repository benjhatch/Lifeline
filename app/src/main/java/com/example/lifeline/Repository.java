package com.example.lifeline;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.os.HandlerCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class Repository {
    private static Repository instance;
    private final MutableLiveData<User> userData = new MutableLiveData<>();
    private final MutableLiveData<Weather> weatherData = new MutableLiveData<>();
    private final MutableLiveData<StepCounterState> stepCounterState = new MutableLiveData<>();
    private final MutableLiveData<StepCount> stepCount = new MutableLiveData<>();

    private double latitude;
    private double longitude;
    private boolean locationSet = false;
    private UserDao mUserDao;
    private WeatherDao mWeatherDao;

    private Application application;

    private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=imperial";
    private static final String key = "113d3ab578badceb2636da939d5d280f";

    private Repository(Application application) {
        this.application = application;

        if (locationSet)
            loadWeather();

        setupAmplify(application);

        UserRoomDatabase db = UserRoomDatabase.getDatabase(application.getApplicationContext());

        uploadUserDatabase(application);

        mUserDao = db.UserDao();

        WeatherRoomDatabase weatherDb = WeatherRoomDatabase.getDatabase(application);
        mWeatherDao = weatherDb.WeatherDao();

    }

    private void setupAmplify(Application application) {
        try {
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.configure(application);
            Log.i("MyAmplifyApp", "Initialized Amplify");
        } catch (AmplifyException error) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error);
        }
    }

    private void uploadUserDatabase(Application application) {

        UserRoomDatabase.databaseExecutor.execute(() -> {
            if (mUserDao != null) {
                mUserDao.checkpoint(new SimpleSQLiteQuery("pragma wal_checkpoint(full)"));
                File userDB = application.getDatabasePath("users.db");
                Log.i("MyAmplifyApp", userDB.getAbsolutePath());

                Amplify.Storage.uploadFile(
                        "Users",
                        userDB,
                        result -> Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getKey()),
                        storageFailure -> Log.e("MyAmplifyApp", "Upload failed", storageFailure)
                );
            }
        });
    }

    private void uploadWeatherDatabase(Application application) {

        WeatherRoomDatabase.databaseExecutor.execute(() -> {
            if (mWeatherDao != null) {
                mWeatherDao.checkpoint(new SimpleSQLiteQuery("pragma wal_checkpoint(full)"));
                File weatherDB = application.getDatabasePath("weather.db");
                Log.i("MyAmplifyApp", weatherDB.getAbsolutePath());

                Amplify.Storage.uploadFile(
                        "Weather",
                        weatherDB,
                        result -> Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getKey()),
                        storageFailure -> Log.e("MyAmplifyApp", "Upload failed", storageFailure)
                );
            }
        });
    }


    public void loginUser(String name) {
        UserRoomDatabase.databaseExecutor.execute(() -> {
            UserTable user = mUserDao.readUser(name);
            if (user != null) {
                Log.d("user", "fetched user");
                User loggedIn = new User();
                loggedIn.absorbUserTable(user);
                userData.postValue(loggedIn);
            }
            else {
                Log.d("user", "no user found for " + name);
            }
        });
    }

    public void logoutUser() {
        userData.setValue(null);
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

        // backup user in database
        if (name!=null && sex!=null && city!=null && country!=null){
            UserTable table = new UserTable(name,sex,year,month,day,city,country,height,weight,BitmapEncoder.encodeBitmap(profilePic));
            insertUser(table);
        }

    }

    private void insertUser(UserTable table){
        UserRoomDatabase.databaseExecutor.execute(() -> {
            mUserDao.insert(table);
            Log.i("MyAmplifyApp", "Uploading Again");
            uploadUserDatabase(application);
        });
    }

    private void insertWeather(WeatherTable table) {
        WeatherRoomDatabase.databaseExecutor.execute(() -> {
            mWeatherDao.insert(table);
            Log.i("MyAmplifyApp", "Uploading Weather Again");
            uploadWeatherDatabase(application);
        });
    }


    public MutableLiveData<User> getUserData() {
        return userData;
    }

    public MutableLiveData<Weather> getWeatherData() {
        return weatherData;
    }

    public MutableLiveData<StepCounterState> getStepCounterState() { return stepCounterState; }

    public MutableLiveData<StepCount> getStepCount() { return stepCount; }

    public void setStepCounterState(boolean on) {
        Log.i("StepCounter", "repo setting to: " + on);
        StepCounterState newStepCounterState = new StepCounterState();
        newStepCounterState.setOn(on);
        stepCounterState.setValue(newStepCounterState);
    }

    public void setStepCount(int count) {
        StepCount newStepCount = new StepCount();
        newStepCount.setCount(count);
        stepCount.setValue(newStepCount);
    }


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void loadWeather() {
        // Uses android networking, which asynchronously makes request on different thread
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

                                    WeatherTable table = new WeatherTable(latitude + "," + longitude, temperature, feelsLike, skies, wind);
                                    insertWeather(table);


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
