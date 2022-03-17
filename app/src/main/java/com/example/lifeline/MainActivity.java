package com.example.lifeline;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.lifeline.databinding.ActivityMainBinding;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements LocationListener {

    static final int EDIT_PROFILE = 1;

    private ActivityMainBinding binding;

    Date current = new Date();

    // profile info
    private String name = "";
    private String sex = "";
    private int year = current.getYear() + 1900;
    private int month = current.getMonth() + 1;
    private int day = current.getDate();
    private String city;
    private String country;
    private int height = 69;
    private int weight = 150;
    private Bitmap profilePic;
    // Location variables
    private LocationManager locationManager;
    private Criteria criteria;
    private String bestProvider;
    private double latitude;
    private double longitude;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent receivedIntent = getIntent();
        Bundle b = receivedIntent.getExtras();
        if (b != null) {
            restoreData(b);
        }

        AndroidNetworking.initialize(getApplicationContext());

        getLocation();

        if (name == "")
            editProfile();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 3);
        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        criteria = new Criteria();
        bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.d("location restored", latitude + ", " + longitude);
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void editProfile() {
        Intent intent = new Intent(this, EditProfile.class);
        intent.putExtras(getProfile());
        startActivity(intent);
    }

    private void restoreData(Bundle b) {
        name = b.getString("NAME");
        sex = b.getString("SEX");
        year = b.getInt("YEAR");
        month = b.getInt("MONTH");
        day = b.getInt("DAY");
        city = b.getString("CITY");
        country = b.getString("COUNTRY");
        height = b.getInt("HEIGHT");
        weight = b.getInt("WEIGHT");
        profilePic = b.getParcelable("PIC");
        if (profilePic != null) {
            Drawable d = new BitmapDrawable(getResources(), profilePic);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setLogo(d);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Bundle getProfile() {
        LocalDate birthday = LocalDate.of(year, month, day);
        LocalDate curr = LocalDate.now();

        int age = (int) ChronoUnit.YEARS.between(birthday, curr);
        Bundle bundle = new Bundle();
        bundle.putString("NAME", name);
        bundle.putInt("AGE", age);
        bundle.putString("SEX", sex);
        bundle.putString("CITY", city);
        bundle.putString("COUNTRY", country);
        bundle.putInt("HEIGHT", height);
        bundle.putInt("WEIGHT", weight);
        bundle.putInt("YEAR", year);
        bundle.putInt("MONTH", month);
        bundle.putInt("DAY", day);
        bundle.putDouble("LATITUDE", latitude);
        bundle.putDouble("LONGITUDE", longitude);
        bundle.putParcelable("PIC", profilePic);
        return bundle;
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        locationManager.removeUpdates(this);
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.d("location", latitude + ", " + longitude);
    }
}