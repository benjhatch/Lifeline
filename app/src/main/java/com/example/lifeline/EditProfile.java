package com.example.lifeline;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.lifeline.databinding.ActivityEditProfileBinding;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import android.os.Bundle;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditProfile extends AppCompatActivity implements OnItemSelectedListener, LocationSubscriber {

    private AppViewModel viewModel;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private ActivityEditProfileBinding binding;

    // private variables used for sex picker for user data
    private String[] sexList = {"Male", "Female", "Other"};
    private String selectedSex;
    private ArrayAdapter sexAD;

    private LocationFetcher locationFetcher;

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    if (locationFetcher != null)
                        locationFetcher.getLocation();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Edit Profile");

        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());

        sexAD = new ArrayAdapter(this, android.R.layout.simple_spinner_item, sexList);

        setUpSexPicker();
        setUpNumberPickers();

        viewModel = new ViewModelProvider(this).get(AppViewModel.class);
        viewModel.getUserData().observe(this, userObserver);

        locationFetcher = new LocationFetcher(this, requestPermissionLauncher);
        locationFetcher.getLocation();

        setupButtons();

        setContentView(binding.getRoot());
    }

    final Observer<User> userObserver = new Observer<User>() {
        @Override
        public void onChanged(User user) {
            if (user == null)
                return;
            binding.editName.setText(user.getName());
            int sexPos = sexAD.getPosition(user.getSex());
            binding.sexSpinner.setSelection(sexPos);
            binding.datePicker.updateDate(user.getYear(), user.getMonth() - 1, user.getDay());
            binding.editCity.setText(user.getCity());
            binding.editCountry.setText(user.getCountry());
            binding.feetPicker.setValue(user.getHeight() / 12);
            binding.inchesPicker.setValue(user.getHeight() % 12);
            binding.weightPicker.setValue(user.getWeight());
            binding.imageView.setImageBitmap(user.getProfilePic());
        }
    };

    private void goBackToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void getCityFromLocation(double latitude, double longitude) {
        if (TextUtils.isEmpty(binding.editCity.getText().toString()) && TextUtils.isEmpty(binding.editCountry.getText().toString())) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses.size() > 0) {
                    binding.editCity.setText(addresses.get(0).getLocality());
                    binding.editCountry.setText(addresses.get(0).getCountryName());
                }
            } catch (IOException e) {

            }
        }
    }

    private void alertMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private boolean validateEntries(String name, Calendar cal, String city, String country, Bitmap profilePic) {
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

    private void setupButtons() {

        binding.imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } catch (ActivityNotFoundException e) {
                    // display error state to the user
                }
            }
        });

        binding.saveProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = binding.editName.getText().toString();
                String sex = selectedSex;

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, binding.datePicker.getYear());
                cal.set(Calendar.MONTH, binding.datePicker.getMonth());
                cal.set(Calendar.DATE, binding.datePicker.getDayOfMonth());


                String city = binding.editCity.getText().toString();
                String country = binding.editCountry.getText().toString();
                int height = binding.feetPicker.getValue() * 12 + binding.inchesPicker.getValue();
                int weightReturn = binding.weightPicker.getValue();

                Bitmap newProfilePic = ((BitmapDrawable)binding.imageView.getDrawable()).getBitmap();


                if (validateEntries(name, cal, city, country, newProfilePic)) {
                    viewModel.setUser(name, sex, binding.datePicker.getYear(), binding.datePicker.getMonth() + 1,
                            binding.datePicker.getDayOfMonth(), city, country, height, weightReturn, newProfilePic);
                    goBackToMain();
                }

            }
        });
    }

    private void setUpSexPicker() {
        binding.sexSpinner.setOnItemSelectedListener(this);

        sexAD.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.sexSpinner.setAdapter(sexAD);
    }

    private void setUpNumberPickers() {
        binding.weightPicker.setMinValue(0);
        binding.weightPicker.setMaxValue(1000);
        binding.weightPicker.setValue(150);

        binding.feetPicker.setMinValue(0);
        binding.feetPicker.setMaxValue(9);
        binding.feetPicker.setValue(5);

        binding.inchesPicker.setMinValue(0);
        binding.inchesPicker.setMaxValue(11);
        binding.inchesPicker.setValue(9);
    }

    @Override
    public void onItemSelected(AdapterView<?> arg, View view, int pos, long id) {
        selectedSex = sexList[pos];
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg) {
        // do nothing
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            binding.imageView.setImageBitmap(imageBitmap);
        }
    }

    @Override
    public void locationFound(Location location) {
        viewModel.setLocation(location.getLatitude(), location.getLongitude());
        getCityFromLocation(location.getLatitude(), location.getLongitude());
    }
}