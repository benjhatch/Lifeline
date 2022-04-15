package com.example.lifeline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.lifeline.databinding.ActivityEditProfileBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import com.google.android.gms.location.LocationRequest;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import android.os.Bundle;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditProfile extends AppCompatActivity implements OnItemSelectedListener {

    private AppViewModel viewModel;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private ActivityEditProfileBinding binding;
    private String[] sexList = {"Male", "Female", "Other"};

    private Button saveProfile;
    private ImageButton imageView;
    private EditText editName;
    private Spinner sexSpinner;
    private DatePicker bdayPicker;
    private EditText editCity;
    private EditText editCountry;
    private NumberPicker weight;
    private NumberPicker feet;
    private NumberPicker inches;

    private Bitmap profilePic;

    private String selectedSex;
    private ArrayAdapter sexAD;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient locationProviderClient;


    private String city;
    private String country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Edit Profile");

        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());

        linkUIComponents();

        sexAD = new ArrayAdapter(this, android.R.layout.simple_spinner_item, sexList);

        setUpSexPicker();
        setUpNumberPickers();

        viewModel = new ViewModelProvider(this).get(AppViewModel.class);
        viewModel.getUserData().observe(this, userObserver);

        getLocation();

        setupButtons();

        setContentView(binding.getRoot());
    }

    final Observer<User> userObserver = new Observer<User>() {
        @Override
        public void onChanged(User user) {
            editName.setText(user.getName());
            int sexPos = sexAD.getPosition(user.getSex());
            sexSpinner.setSelection(sexPos);
            bdayPicker.updateDate(user.getYear(), user.getMonth() - 1, user.getDay());
            city = user.getCity();
            country = user.getCountry();
            editCity.setText(city);
            editCountry.setText(country);
            feet.setValue(user.getHeight() / 12);
            inches.setValue(user.getHeight() % 12);
            weight.setValue(user.getWeight());
            profilePic = user.getProfilePic();
            imageView.setImageBitmap(profilePic);
        }
    };

    private void setupButtons() {

        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } catch (ActivityNotFoundException e) {
                    // display error state to the user
                }
            }
        });

        saveProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = editName.getText().toString();
                String sex = selectedSex;

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, bdayPicker.getYear());
                cal.set(Calendar.MONTH, bdayPicker.getMonth());
                cal.set(Calendar.DATE, bdayPicker.getDayOfMonth());


                String city = editCity.getText().toString();
                String country = editCountry.getText().toString();
                int height = feet.getValue() * 12 + inches.getValue();
                int weightReturn = weight.getValue();


                if (validateEntries(name, cal, city, country)) {
                    viewModel.setUser(name, sex, bdayPicker.getYear(), bdayPicker.getMonth() + 1,
                            bdayPicker.getDayOfMonth(), city, country, height, weightReturn, profilePic);
                    goBackToMain();
                }

            }
        });
    }

    private void goBackToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }



    private void getCityFromLocation(double latitude, double longitude) {
        if (TextUtils.isEmpty(editCity.getText().toString()) && TextUtils.isEmpty(editCountry.getText().toString())) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses.size() > 0) {
                    city = addresses.get(0).getLocality();
                    country = addresses.get(0).getCountryName();
                    editCity.setText(city);
                    editCountry.setText(country);
                }
            } catch (IOException e) {

            }
        }
    }


    private void returnEditedProfile(String name, String sex, String city, String country, int height,
                                     int weight) {
        Intent intent = new Intent(this, MainActivity.class);
        Bundle profileBundle = new Bundle();
        profileBundle.putString("NAME", name);
        profileBundle.putString("SEX", sex);
        profileBundle.putInt("YEAR", bdayPicker.getYear());
        profileBundle.putInt("MONTH", bdayPicker.getMonth() + 1);
        profileBundle.putInt("DAY", bdayPicker.getDayOfMonth());
        profileBundle.putString("CITY", city);
        profileBundle.putString("COUNTRY", country);
        profileBundle.putInt("HEIGHT", height);
        profileBundle.putInt("WEIGHT", weight);
        profileBundle.putParcelable("PIC", profilePic);
        intent.putExtras(profileBundle);
        startActivity(intent);
    }


    private boolean validateEntries(String name, Calendar cal, String city, String country) {
        Date enteredDate = cal.getTime();
        Date currentDate = new Date();

        if (name.equals("")) {
            alertMessage("Enter your name");
            return false;
        } else if (enteredDate.after(currentDate)) {
            alertMessage("Enter a valid birthday");
            return false;
        } else if (city.equals("")) {
            alertMessage("Enter a city");
            return false;
        } else if (country.equals("")) {
            alertMessage("Enter a country");
            return false;
        } else if (profilePic == null) {
            alertMessage("Select profile picture");
            return false;
        }

        return true;
    }

    private void alertMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    private void linkUIComponents() {
        saveProfile = binding.saveProfile;
        imageView = binding.imageView;
        editName = binding.editName;
        bdayPicker = binding.datePicker;
        editCity = binding.editCity;
        editCountry = binding.editCountry;
        sexSpinner = binding.sexSpinner;
        weight = binding.weightPicker;
        feet = binding.feetPicker;
        inches = binding.inchesPicker;
    }

    private void setUpSexPicker() {
        sexSpinner.setOnItemSelectedListener(this);

        sexAD.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sexSpinner.setAdapter(sexAD);
    }

    private void setUpNumberPickers() {
        weight.setMinValue(0);
        weight.setMaxValue(1000);
        weight.setValue(150);

        feet.setMinValue(0);
        feet.setMaxValue(9);
        feet.setValue(5);

        inches.setMinValue(0);
        inches.setMaxValue(11);
        inches.setValue(9);
    }

    private void getLocation() {
        Log.d("getloc", "fetching");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);
        }
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {

                    @Override
                    public void onSuccess(Location location) {

                        if (location != null) {
                            Log.d("getLoc", "fetched cache");
                            getCityFromLocation(location.getLatitude(), location.getLongitude());
                        }
                        else
                            getCurrentLocation();
                    }
                });

    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);
        }

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(0);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                if (locationResult == null) {
                    Log.d("getloc", "null");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        Log.d("getloc", location.getLatitude() + ", " + location.getLongitude());
                        getCityFromLocation(location.getLatitude(), location.getLongitude());
                        locationProviderClient.removeLocationUpdates(locationCallback);
                    }
                }
            }
        };
        Log.d("getloc", "callback start");
        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    @Override
    public void onItemSelected(AdapterView<?> arg, View view, int pos, long id) {
        selectedSex = sexList[pos];
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg) {
        // implement
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            profilePic = imageBitmap;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}