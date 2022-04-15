package com.example.lifeline.ui.notifications;

import android.content.Intent;
import android.net.Uri;
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
import com.example.lifeline.Weather;
import com.example.lifeline.databinding.FragmentExerciseBinding;

public class ExerciseFragment extends Fragment {

    private AppViewModel viewModel;

    private FragmentExerciseBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentExerciseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        viewModel = new ViewModelProvider(this).get(AppViewModel.class);

        viewModel.getWeatherData().observe(getViewLifecycleOwner(), weatherObserver);

        binding.hikes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:" + viewModel.getLatitude() + "," + viewModel.getLongitude() + "?q=hikes");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

                startActivity(mapIntent);
            }
        });

        return root;
    }

    final Observer<Weather> weatherObserver = new Observer<Weather>() {

        @Override
        public void onChanged(Weather weather) {
            binding.temp.setText(weather.getTemperature());
            binding.feels.setText(weather.getFeelsLike());
            binding.skies.setText(weather.getSkies());
            binding.wind.setText(weather.getWind());

            binding.temp.setVisibility(View.VISIBLE);
            binding.feels.setVisibility(View.VISIBLE);
            binding.skies.setVisibility(View.VISIBLE);
            binding.wind.setVisibility(View.VISIBLE);

            binding.tempLabel.setVisibility(View.VISIBLE);
            binding.feelLabel.setVisibility(View.VISIBLE);
            binding.skiesLabel.setVisibility(View.VISIBLE);
            binding.windLabel.setVisibility(View.VISIBLE);

            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}