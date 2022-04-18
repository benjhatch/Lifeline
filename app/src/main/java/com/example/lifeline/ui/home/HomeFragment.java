package com.example.lifeline.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.lifeline.AppViewModel;
import com.example.lifeline.EditProfile;
import com.example.lifeline.Login;
import com.example.lifeline.User;
import com.example.lifeline.databinding.FragmentHomeBinding;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class HomeFragment extends Fragment {

    private AppViewModel viewModel;

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        viewModel = new ViewModelProvider(this).get(AppViewModel.class);

        viewModel.getUserData().observe(getViewLifecycleOwner(), userObserver);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.editProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditProfile.class);
                startActivity(intent);
            }
        });

        binding.signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.logoutUser();
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
            }
        });

        return root;
    }

    final Observer<User> userObserver = new Observer<User>() {

        @SuppressLint("SetTextI18n")
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onChanged(User user) {
            if (user == null) {
                return;
            }
            binding.name.setText(user.getName());
            binding.sex.setText(user.getSex());
            binding.hometown.setText(user.getCity() + ", " + user.getCountry());
            binding.feet.setText((user.getHeight() / 12) + "");
            binding.inches.setText((user.getHeight() % 12) + "");
            binding.weight.setText(user.getWeight() + "");
            binding.imageView.setImageBitmap(user.getProfilePic());

            LocalDate birthday = LocalDate.of(user.getYear(), user.getMonth(), user.getDay());
            LocalDate curr = LocalDate.now();

            int age = (int) ChronoUnit.YEARS.between(birthday, curr);

            binding.age.setText(age + "");
        }
    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}