package com.example.lifeline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends AppCompatActivity {

    private AppViewModel viewModel;

    private Button login;
    private Button signUp;
    private EditText nameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Login");

        viewModel = new ViewModelProvider(this).get(AppViewModel.class);
        viewModel.getUserData().observe(this, userObserver);

        setContentView(R.layout.activity_login);

        setupButtons();
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void goToEditProfile() {
        Intent intent = new Intent(this, EditProfile.class);
        startActivity(intent);
    }

    final Observer<User> userObserver = new Observer<User>() {
        @Override
        public void onChanged(User user) {
            if (user != null) {
                goToMain();
            }
        }
    };

    private void setupButtons() {
        login = (Button) findViewById(R.id.loginButton);
        signUp = (Button) findViewById(R.id.signupButton);
        nameInput = (EditText) findViewById(R.id.nameinput);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.loginUser(nameInput.getText().toString());
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToEditProfile();
            }
        });
    }
}