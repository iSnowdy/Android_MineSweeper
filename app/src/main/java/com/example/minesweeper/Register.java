package com.example.minesweeper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minesweeper.Utils.SharedPreferencesUtil;
import com.example.minesweeper.Utils.ToastUtil;

public class Register extends AppCompatActivity {
    private EditText
            usernameEditText, userEmailEditText, userPasswordEditText, repeatUserPasswordEditText;
    private String
            username, emailAddress, password, repeatPassword;
    private Button registerButton;
    private TextView haveAnAccountTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        loadLoginViews();
        setLoginListeners();
    }

    private String retrieveInformationFromViewAsString(EditText view) {
        return view.getText().toString();
    }

    private void redirectMessageToast(String message) {
        ToastUtil.createToast(this, message);
    }

    private void loadLoginViews() {
        this.usernameEditText = findViewById(R.id.registerUsername);
        this.userEmailEditText = findViewById(R.id.registerEmail);
        this.userPasswordEditText = findViewById(R.id.registerPassword);
        this.repeatUserPasswordEditText = findViewById(R.id.registerRepeatPassword);
        this.registerButton = findViewById(R.id.registerButtonRegister);
        this.haveAnAccountTextView = findViewById(R.id.haveAnAccount);
    }

    private void setLoginListeners() {
        this.registerButton.setOnClickListener(v -> setRegisterOnClickListener());
        this.haveAnAccountTextView.setOnClickListener(v -> setHaveAnAccountOnClickListener());
    }

    private void setRegisterOnClickListener() {
        this.registerButton.setOnClickListener(v -> registerButtonListenerMethod());
    }

    private void registerButtonListenerMethod() {
        if (isRegisterInformationValid()) {
            registerUser();
            redirectMessageToast("Registration successful");
            redirectToMainActivity();
        }
    }

    private void redirectToMainActivity() {
        redirectMessageToast("Redirecting to home page");
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        // Pass username to Main Activity
        mainActivityIntent.putExtra(SharedPreferences_Keys.USERNAME.toString(), this.username);
        startActivity(mainActivityIntent);
    }

    private void setHaveAnAccountOnClickListener() {
        Intent loginActivityIntent = new Intent(this, Login.class);
        startActivity(loginActivityIntent);
    }

    // Validations
    private boolean isRegisterInformationValid() {
        return isUsernameValid() && isEmailValid() && isPasswordValid() && isRepeatPasswordValid();
    }

    private boolean isUsernameValid() {
        this.username = retrieveInformationFromViewAsString(this.usernameEditText);
        if (this.username != null && !this.username.isEmpty() && this.username.length() >= 4) return true;
        redirectMessageToast("Username must be at least 4 characters long");
        return false;
    }

    private boolean isEmailValid() {
        this.emailAddress = retrieveInformationFromViewAsString(this.userEmailEditText);
        // Any alphanumeric values followed by @ and finally . and at least 2 characters
        if (this.emailAddress.matches("^[a-zA-Z0-9_+-.]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}$")) return true;
        redirectMessageToast("Email is not valid");
        return false;
    }
    // https://stackoverflow.com/questions/19605150/regex-for-password-must-contain-at-least-eight-characters-at-least-one-number-a
    private boolean isPasswordValid() {
        this.password = retrieveInformationFromViewAsString(this.userPasswordEditText);
        // Password must be at least 6 characters long and contain at least one letter and one number
        if (this.password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$")) return true;
        redirectMessageToast("Password must be at least 6 characters long and contain at least one letter and one number");
        return false;
    }
    // Andy andylopezrey@hotmail.com Andy11

    private boolean isRepeatPasswordValid() {
        this.repeatPassword = retrieveInformationFromViewAsString(this.repeatUserPasswordEditText);
        if (this.password.equals(this.repeatPassword)) return true;
        redirectMessageToast("Passwords do not match");
        return false;
    }

    private void registerUser() {
        System.out.println("Putting the following information inside the " +
                "Shared Preferences Bundle of (" + this.username + "):");
        System.out.println("Username: " + this.username);
        System.out.println("Email address: " + this.emailAddress);
        System.out.println("Password: " + this.password);

        SharedPreferencesUtil.saveUserInformation(this, this.username, SharedPreferences_Keys.USERNAME.toString(), this.username);
        SharedPreferencesUtil.saveUserInformation(this, this.username, SharedPreferences_Keys.EMAIL_ADDRESS.toString(), this.emailAddress);
        SharedPreferencesUtil.saveUserInformation(this, this.username, SharedPreferences_Keys.PASSWORD.toString(), this.password);
    }
}