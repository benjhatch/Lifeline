package com.example.lifeline.ui.notifications;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.lifeline.MainActivity;
import com.example.lifeline.R;
import com.example.lifeline.databinding.FragmentNotificationsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private Bundle profile;
    private double latitude;
    private double longitude;
    private String temp;
    private String feels;
    private String skies;
    private String wind;
    private String place;
    private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=imperial";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MainActivity activity = (MainActivity) getActivity();

        profile = activity.getProfile();
        latitude = profile.getDouble("LATITUDE");
        longitude = profile.getDouble("LONGITUDE");
        String city = profile.getString("CITY");
        String country = profile.getString("COUNTRY");
        if (city != null)
            place = city + ", " + country;

        binding.place.setText(place);

        getWeather();

        binding.hikes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=hikes");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

                startActivity(mapIntent);
            }
        });

        return root;
    }

    public void getWeather() {
        AndroidNetworking.get(String.format(OPEN_WEATHER_MAP_API, latitude, longitude))
                .addHeaders("x-api-key", getString(R.string.key))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("json", response.toString());
                        try {
                            Log.d("json", response.getJSONObject("main").toString());
                            temp = response.getJSONObject("main").getDouble("temp") + " degrees";
                            feels = response.getJSONObject("main").getDouble("feels_like") + " degrees";
                            JSONObject weather = response.getJSONArray("weather").getJSONObject(0);
                            skies = weather.getString("main");
                            wind = response.getJSONObject("wind").getDouble("speed") + " mph";

                            binding.temp.setText(temp);
                            binding.feels.setText(feels);
                            binding.skies.setText(skies);
                            binding.wind.setText(wind);

                            binding.temp.setVisibility(View.VISIBLE);
                            binding.feels.setVisibility(View.VISIBLE);
                            binding.skies.setVisibility(View.VISIBLE);
                            binding.wind.setVisibility(View.VISIBLE);

                            binding.tempLabel.setVisibility(View.VISIBLE);
                            binding.feelLabel.setVisibility(View.VISIBLE);
                            binding.skiesLabel.setVisibility(View.VISIBLE);
                            binding.windLabel.setVisibility(View.VISIBLE);

                            binding.progressBar.setVisibility(View.INVISIBLE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("json", "errror: ");
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}