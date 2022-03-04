package com.example.lifeline.ui.dashboard;

import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.lifeline.MainActivity;
import com.example.lifeline.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment implements OnItemSelectedListener {

    private FragmentDashboardBinding binding;

    private String[] goalList = { "Lose Weight", "Maintain My Weight", "Gain Weight" };
    double activityFactor = 1.2;
    private NumberPicker ppwInput;
    private TextView calTextView;
    boolean active = false;
    int goalPos = 0;
    double ppw = 0;
    private String sex = "Male";
    private double ageInYears = 21;
    private double heightInInches = 72;
    private double weight = 150;
    private Button button;
    Switch sw;
    private Bundle profile;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if(savedInstanceState != null){
            active = savedInstanceState.getBoolean("ACTIVE");
            goalPos = savedInstanceState.getInt("goalPos");
            ppw = savedInstanceState.getInt("PPW");
        }

        MainActivity activity = (MainActivity) getActivity();
        profile = activity.getProfile();

        sex = profile.getString("SEX");
        Log.d("SEX", "sex: " + sex);
        ageInYears = profile.getInt("AGE");
        heightInInches = profile.getInt("HEIGHT");
        weight = profile.getInt("WEIGHT");

        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sw = binding.switch1;
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    active = true;
                } else {
                    active = false;
                }
                TextView textBMR = binding.textBMR;
                textBMR.setText("Your BMR: " + calcBMR());
                updateCalories();
            }
        });

        Spinner goalsSpinner = binding.goals;
        goalsSpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);



        // Create the instance of ArrayAdapter
        // having the list of courses
        ArrayAdapter goalsAD = new ArrayAdapter(this.getContext(), android.R.layout.simple_spinner_item, goalList);

        // set simple layout resource file
        // for each item of spinner
        goalsAD.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        goalsSpinner.setAdapter(goalsAD);

        final TextView textBMI = binding.textBMI;
        textBMI.setText("Your BMI: " + calcBMI(heightInInches, weight));

        final TextView textBMR = binding.textBMR;
        textBMR.setText("Your BMR: " + calcBMR());

        calTextView = binding.calories;
        calTextView.setText(0+"");
        ppwInput = binding.numberpicker;
        ppwInput.setMinValue(1);
        ppwInput.setMaxValue(10);
        button = binding.update;
        updateCalories();
        ppwInput.setValue(1);


        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ppw = ppwInput.getValue();
                updateCalories();
            }
        });

        return root;

    }

    public double calcBMI(double height, double weight){
        //weight / height ^ 2 * 703
        double val = weight/(height * height)*703.0;
        val = Math.round(val * 10);
        return val / 10;
    }

    public int calcBMR(){
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
        Log.d("temp", ppw+"");
        if(goalPos == 0){
            button.setEnabled(true);
            return (int) (bmr - (500 * ppw));
        }else if(goalPos == 1){
            button.setEnabled(false);
            ppwInput.setValue(0);
            return (int) bmr;
        }else{
            button.setEnabled(true);
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
        calTextView.setText(caltext);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Performing action when ItemSelected
    // from spinner, Overriding onItemSelected method
    @Override
    public void onItemSelected(AdapterView<?> arg0,
                               View arg1,
                               int position,
                               long id)
    {

        goalPos = position;
        updateCalories();
        // make toastof name of course
        // which is selected in spinner
        Toast.makeText(this.getContext(),
                goalList[position],
                Toast.LENGTH_LONG)
                .show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("ACTIVE", active);
        outState.putInt("GoalPos", goalPos);
        outState.putInt("PPW", ppwInput.getValue());
    }
}