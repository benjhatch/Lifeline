package com.example.lifeline.ui.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.lifeline.EditProfile;
import com.example.lifeline.MainActivity;
import com.example.lifeline.databinding.FragmentHomeBinding;

import java.util.Calendar;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    private String name;
    private String sex;
    private int age;
    private String hometown;
    private int weight;
    private int height;
    private Bitmap profilePic;

    private Bundle profile;

    private Button editProfile;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        MainActivity activity = (MainActivity) getActivity();
        profile = activity.getProfile();

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // set up UI
        extractData(profile);
        setupUI();

        editProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditProfile.class);
                intent.putExtras(profile);
                startActivity(intent);
            }
        });

        return root;
    }

    private void extractData(Bundle profile) {
        name = profile.getString("NAME");
        sex = profile.getString("SEX");
        age = profile.getInt("AGE");
        hometown = profile.getString("CITY") + ", " + profile.getString("COUNTRY");
        weight = profile.getInt("WEIGHT");
        height = profile.getInt("HEIGHT");
        profilePic = profile.getParcelable("PIC");
    }

    private void setupUI() {
        editProfile = binding.editProfile;
        binding.name.setText(name);
        binding.sex.setText(sex);
        binding.age.setText(age + "");
        binding.hometown.setText(hometown);
        binding.feet.setText((height / 12) + "");
        binding.inches.setText((height % 12) + "");
        binding.weight.setText(weight + "");
        binding.imageView.setImageBitmap(profilePic);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}