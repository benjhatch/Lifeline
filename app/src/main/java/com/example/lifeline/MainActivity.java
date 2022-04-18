package com.example.lifeline;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;

import com.androidnetworking.AndroidNetworking;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.lifeline.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity implements LocationSubscriber {

    private AppViewModel viewModel;

    private ActivityMainBinding binding;

    private LocationFetcher locationFetcher;

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    if (locationFetcher != null)
                        locationFetcher.getLocation();
                }
            });


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AppViewModel.class);
        viewModel.getUserData().observe(this, userObserver);

        locationFetcher = new LocationFetcher(this, requestPermissionLauncher);
        locationFetcher.getLocation();

        AndroidNetworking.initialize(getApplicationContext());

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }


    final Observer<User> userObserver = new Observer<User>() {
        @Override
        public void onChanged(User user) {
            if (user == null)
                return;
            Bitmap profilePic = user.getProfilePic();
            if (profilePic != null) {
                Drawable d = new BitmapDrawable(getResources(), profilePic);
                ActionBar actionBar = getSupportActionBar();
                actionBar.setDisplayUseLogoEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setLogo(d);
            }
        }
    };

    @Override
    public void locationFound(Location location) {
        viewModel.setLocation(location.getLatitude(), location.getLongitude());
    }
}