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

    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;
    private Bundle profile;
    private double latitude;
    private double longitude;
    private String temp;
    private String feels;
    private String skies;
    private String wind;
    private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=imperial";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MainActivity activity = (MainActivity) getActivity();
        profile = activity.getProfile();

        latitude = profile.getDouble("LATITUDE");
        longitude = profile.getDouble("LONGITUDE");

        Log.d("weather", latitude + ", " + longitude);

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    getWeather(latitude, longitude);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        AndroidNetworking.get(String.format(OPEN_WEATHER_MAP_API, latitude, longitude))
                .addHeaders("x-api-key", "113d3ab578badceb2636da939d5d280f")
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("json", "errror: ");
                    }
                });

        binding.hikes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=hikes");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

                startActivity(mapIntent);
            }
        });

        return root;
    }

    private JSONObject getWeather(double latitude, double longitude) {
        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API, latitude, longitude));
            Log.d("url", url.toString());

            Log.d("json", "hello 0");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            Log.d("json", "hello 1");
            connection.addRequestProperty("x-api-key", "113d3ab578badceb2636da939d5d280f");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            Log.d("json", "hello 2");

            Log.d("json", json.toString());

            JSONObject data = new JSONObject(json.toString());

            if (data.getInt("cod") != 200) {
                return null;
            }

            return data;
        } catch (Exception e) {
            Log.d("json", e.toString());
            return null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}