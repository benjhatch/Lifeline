package com.example.lifeline;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.lifeline.databinding.ActivityMainBinding;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent receivedIntent = getIntent();
        Bundle b = receivedIntent.getExtras();
        if (b != null) {
            restoreData(b);
        }

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
        bundle.putParcelable("PIC", profilePic);
        return bundle;
    }


}