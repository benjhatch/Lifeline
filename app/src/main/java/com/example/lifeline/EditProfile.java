package com.example.lifeline;

import androidx.appcompat.app.AppCompatActivity;
import com.example.lifeline.databinding.ActivityEditProfileBinding;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.provider.MediaStore;
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

import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;

public class EditProfile extends AppCompatActivity implements OnItemSelectedListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private ActivityEditProfileBinding binding;
    private String[] sexList = { "Male", "Female", "Other" };

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Edit Profile");

        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());

        linkUIComponents();

        sexAD = new ArrayAdapter(this, android.R.layout.simple_spinner_item, sexList);

        setUpSexPicker();
        setUpNumberPickers();

        Intent receivedIntent = getIntent();
        Bundle profile = receivedIntent.getExtras();
        if (profile != null)
            restoreState(profile);

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
                    returnEditedProfile(name, sex, city, country, height, weightReturn);
                }

            }
        });

        setContentView(binding.getRoot());
    }

    private void restoreState(Bundle profile) {
        editName.setText(profile.getString("NAME"));
        int sexPos = sexAD.getPosition(profile.getString("SEX"));
        sexSpinner.setSelection(sexPos);
        Log.d("date", "Date: " + profile.getInt("YEAR") + ", " + profile.getInt("MONTH") + ", " + profile.getInt("DAY"));
        bdayPicker.updateDate(profile.getInt("YEAR"), profile.getInt("MONTH") - 1, profile.getInt("DAY"));
        editCity.setText(profile.getString("CITY"));
        editCountry.setText(profile.getString("COUNTRY"));
        feet.setValue(profile.getInt("HEIGHT") / 12);
        inches.setValue(profile.getInt("HEIGHT") % 12);
        weight.setValue(profile.getInt("WEIGHT"));
        profilePic = profile.getParcelable("PIC");
        imageView.setImageBitmap(profilePic);
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
        }
        else if (enteredDate.after(currentDate)) {
            alertMessage("Enter a valid birthday");
            return false;
        }
        else if (city.equals("")) {
            alertMessage("Enter a city");
            return false;
        }
        else if (country.equals("")) {
            alertMessage("Enter a country");
            return false;
        }
        else if (profilePic == null) {
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
}