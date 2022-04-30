package com.example.lifeline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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

    private SensorManager mSensorManager;
    private Sensor mLinearAccelerometer;
    private final double mThreshold = 2;

    private double last_x, last_y;
    private double now_x, now_y;

    boolean on = false;

    private Sensor mStepCounter;


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

        //Get sensor manager
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //Get the default light sensor
        mLinearAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        //Get the default step counter
        mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

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

    private SensorEventListener mListener = new SensorEventListener() {


        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            boolean originalOn = on;

            if (sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

                //Get the acceleration rates along the y and z axes
                now_x = sensorEvent.values[0];
                now_y = sensorEvent.values[1];

                double dx = Math.abs(last_x - now_x);
                double dy = Math.abs(last_y - now_y);

                if (dx > mThreshold) {
                    Log.i("gesture", "step counter off");
                    on = false;
                } else if (dy > mThreshold) {
                    Log.i("gesture", "step counter on");
                    on = true;
                }

                if (on != originalOn) {
                    viewModel.setStepCounterOn(on);
                    Log.i("StepCounter", "setting to " + on);
                }
                last_x = now_x;
                last_y = now_y;
            }

            if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                Log.d("stepCount", String.valueOf(sensorEvent.values[0]));
                if (on) {
                    viewModel.setStepCount((int) sensorEvent.values[0]);
                }
            }
        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if(mLinearAccelerometer!=null){
            mSensorManager.registerListener(mListener,mLinearAccelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        }
        if(mStepCounter!=null){
            mSensorManager.registerListener(mListener,mStepCounter,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mLinearAccelerometer!=null){
            mSensorManager.unregisterListener(mListener);
        }
        if(mStepCounter!=null){
            mSensorManager.unregisterListener(mListener);
        }
    }
}