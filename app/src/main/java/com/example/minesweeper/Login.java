package com.example.minesweeper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {
    private EditText userEmail;
    private EditText userPassword;
    private Button loginButton;
    private Button signInButton;
    private TextView forgotPasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        loadViews();
        setListeners();
    }

    private void redirectMessageToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private String retrieveInformationFromViewAsString(EditText view) {
        return view.getText().toString();
    }


    private void loadViews() {
        userEmail = findViewById(R.id.userEmail);
        userPassword = findViewById(R.id.userPassword);
        loginButton = findViewById(R.id.loginButton);
        signInButton = findViewById(R.id.registerButton);
        forgotPasswordText = findViewById(R.id.forgotPassword);
    }

    private void setListeners() {
        loginButton.setOnClickListener(v -> setLoginOnClickListener());
        signInButton.setOnClickListener(v -> setRegisterOnClickListener());
        forgotPasswordText.setOnClickListener(v -> setForgotPasswordOnClickListener());
    }

    private void setLoginOnClickListener() {
        // TODO: Login logic
    }

    private boolean verifyLogin() {
        // TODO: Verify login logic
        return isUserEmailValid(retrieveInformationFromViewAsString(userEmail))
                && isUserPasswordValid(retrieveInformationFromViewAsString(userPassword));
    }

    private boolean isUserEmailValid(String userEmail) {
        // TODO: Verify user email logic. Check if the email is in the DB

        return true;
    }

    private boolean isUserPasswordValid(String userPassword) {
        // TODO: Verify user password logic. Check if the password is in the DB
        // TODO: The password must be hashed
        return true;
    }

    private void redirectToHome() {
        redirectMessageToast("Redirecting to home page");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void setRegisterOnClickListener() {
        redirectMessageToast("Redirecting to register page");
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    private void setForgotPasswordOnClickListener() {
        // TODO: Forgot Password logic
    }

}
