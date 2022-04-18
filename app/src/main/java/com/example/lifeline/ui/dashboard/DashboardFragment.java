package com.example.lifeline.ui.dashboard;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.AdapterView.OnItemSelectedListener;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.lifeline.AppViewModel;
import com.example.lifeline.User;
import com.example.lifeline.databinding.FragmentDashboardBinding;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DashboardFragment extends Fragment implements OnItemSelectedListener {

    private FragmentDashboardBinding binding;

    private AppViewModel viewModel;

    private String[] goalList = { "Lose Weight", "Maintain My Weight", "Gain Weight" };

    // goal-related variables
    public static boolean active = false;
    static int goalPos = 0;
    public static double ppw = 1;

    // elements eventually filled by view model
    public static String sex = "Male";
    public static double ageInYears = 21;
    public static double heightInInches = 69;
    public static double weight = 150;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // not user data, just minor data like activity level that doesn't
        // need to be saved to the Room Database
        if(savedInstanceState != null){
            active = savedInstanceState.getBoolean("ACTIVE");
            goalPos = savedInstanceState.getInt("goalPos");
            ppw = savedInstanceState.getInt("PPW");
        }

        viewModel = new ViewModelProvider(this).get(AppViewModel.class);
        viewModel.getUserData().observe(getViewLifecycleOwner(), userObserver);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setUpUIElements();

        return root;

    }

    final Observer<User> userObserver = new Observer<User>() {

        @SuppressLint("SetTextI18n")
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onChanged(User user) {
            if (user == null)
                return;

            sex = user.getSex();

            heightInInches = user.getHeight();
            weight = user.getWeight();

            LocalDate birthday = LocalDate.of(user.getYear(), user.getMonth(), user.getDay());
            LocalDate curr = LocalDate.now();

            ageInYears = (int) ChronoUnit.YEARS.between(birthday, curr);
        }
    };

    public static double calcBMI(double height, double weight){
        //weight / height ^ 2 * 703
        double val = weight/(height * height)*703.0;
        val = Math.round(val * 10);
        return val / 10;
    }

    public static int calcBMR(){
        // male   -> Men: BMR = 88.362 + (13.397 x weight in kg) + (4.799 x height in cm) – (5.677 x age in years)
        // female -> 447.593 + (9.247 x weight in kg) + (3.098 x height in cm) – (4.330 x age in years)
        double cm = heightInInches * 2.54;
        double kg = weight * 0.453592;
        double activeFactor = 1.2;
        if(active){
            activeFactor = 1.55;
        }
        if(sex.equals("Male")||(sex.equals("Other"))){
            return (int) (activeFactor * Math.round(88.362 + (13.397 * kg) + (4.799 * cm) - (5.677 * ageInYears)));
        }else if(sex.equals("Female")){
            return (int) (activeFactor* Math.round(447.593 + (9.247 * kg) + (3.098 * cm) - (4.330 * ageInYears)));
        }else{
            return -1;
        }
    }

    public int calcCal(double ppw, int bmr){

        if(goalPos == 0){
            binding.update.setEnabled(true);
            return (int) (bmr - (500 * ppw));
        }else if(goalPos == 1){
            binding.update.setEnabled(false);
            binding.numberpicker.setValue(1);
            return (int) bmr;
        }else{
            binding.update.setEnabled(true);
            return (int) (bmr + (500 * ppw));
        }

    }

    public void updateCalories(){
        String caltext = "You will need to consume ";
        int calories = calcCal(ppw, calcBMR());
        caltext += calories;
        caltext += " calories a day in order to reach this goal.";
        if(Math.abs(ppw)>2){
            caltext += " Losing/Gaining more than 2 pounds per week can be a very difficult goal to achieve. People have more success with a slower approach.";
        }
        if(((sex.equals("Male")||(sex.equals("Other"))) && calories < 1200) || ((sex.equals("Female") && calories < 1000))){
            caltext+=" *Warning* Eating this amount of calories per day can be a health risk.";
        }
        binding.calories.setText(caltext);
    }


    private void setUpUIElements() {
        binding.switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                active = isChecked;
                binding.textBMR.setText("Your BMR: " + calcBMR());
                updateCalories();
            }
        });

        binding.goals.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);


        // Create the instance of ArrayAdapter
        // having the list of courses
        ArrayAdapter goalsAD = new ArrayAdapter(this.getContext(), android.R.layout.simple_spinner_item, goalList);

        // set simple layout resource file
        // for each item of spinner
        goalsAD.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        binding.goals.setAdapter(goalsAD);

        binding.textBMI.setText("Your BMI: " + calcBMI(heightInInches, weight));
        binding.textBMR.setText("Your BMR: " + calcBMR());
        binding.calories.setText("0");

        binding.numberpicker.setMinValue(1);
        binding.numberpicker.setMaxValue(10);
        updateCalories();


        binding.update.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ppw = binding.numberpicker.getValue();
                updateCalories();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Performing action when ItemSelected
    // from spinner, Overriding onItemSelected method
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id)
    {
        goalPos = position;
        updateCalories();
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // Do nothing
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("ACTIVE", active);
        outState.putInt("GoalPos", goalPos);
        outState.putInt("PPW", binding.numberpicker.getValue());
    }
}
