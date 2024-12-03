package com.example.minesweeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class Login extends AppCompatActivity {
    private EditText
            usernameEditText, emailAddressEditText, userPasswordEditText;
    private String
            username, emailAddress, password;
    private Button
            loginButton, signInButton;
    private TextView forgotPasswordTextView;
    private int oneTimeCode;
    private SharedPreferences loginSharedPreferences;
    private SharedPreferences.Editor loginSharedPreferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        createRegisterSharedPreferences();
        createRegisterSharedPreferencesEditor();

        loadViews();
        setListeners();
    }

    private void createRegisterSharedPreferences() {
        this.loginSharedPreferences = getSharedPreferences(SharedPreferences_Keys.USER_INFORMATION_SP.toString(), MODE_PRIVATE);
    }
    // Maybe not needed
    private void createRegisterSharedPreferencesEditor() {
        this.loginSharedPreferencesEditor = this.loginSharedPreferences.edit();
    }

    private void redirectMessageToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private String retrieveInformationFromViewAsString(EditText view) {
        return view.getText().toString();
    }


    private void loadViews() {
        usernameEditText = findViewById(R.id.loginUsername);
        userPasswordEditText = findViewById(R.id.userPassword);
        loginButton = findViewById(R.id.loginButton);
        signInButton = findViewById(R.id.registerButtonLogin);
        forgotPasswordTextView = findViewById(R.id.forgotPassword);
    }

    private void setListeners() {
        loginButton.setOnClickListener(v -> setLoginOnClickListener());
        signInButton.setOnClickListener(v -> setRegisterOnClickListener());
        forgotPasswordTextView.setOnClickListener(v -> setForgotPasswordOnClickListener());
    }

    private void setLoginOnClickListener() {
        if (verifyLogin()) {
            redirectToMainActivity();
        }
    }

    private boolean verifyLogin() {
        return isUsernameValid(retrieveInformationFromViewAsString(usernameEditText))
                && isUserPasswordValid(retrieveInformationFromViewAsString(userPasswordEditText));
    }

    private boolean isUsernameValid(String username) {
        this.username = retrieveInformationFromViewAsString(usernameEditText);
        if (!username.equals(this.username)) {
            redirectMessageToast("Username is not valid");
            return false;
        }
        return true;
    }

    private boolean isUserPasswordValid(String userPassword) {
        // TODO: The password should be hashed
        this.password = retrieveInformationFromViewAsString(userPasswordEditText);
        if (!userPassword.equals(this.password)) {
            redirectMessageToast("Password is not valid");
            return false;
        }
        return true;
    }

    private void redirectToMainActivity() {
        redirectMessageToast("Redirecting to home page");
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(SharedPreferences_Keys.USERNAME.toString(), this.username);
        startActivity(intent);
    }

    private void setRegisterOnClickListener() {
        redirectMessageToast("Redirecting to register page");
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    private void setForgotPasswordOnClickListener() {
        this.forgotPasswordTextView.setOnClickListener(v -> sendEmailToUserToResetPassword());
    }
    // https://www.geeksforgeeks.org/how-to-send-an-email-from-your-android-app/
    private void sendEmailToUserToResetPassword() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        loadInformationToEmailIntent(emailIntent);
    }

    private void loadInformationToEmailIntent(Intent emailIntent) {
        this.emailAddress = this.loginSharedPreferences.getString(SharedPreferences_Keys.EMAIL_ADDRESS.toString(), "");

        System.out.println("Email address inside email intent: " + this.emailAddress);

        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{this.emailAddress});
        String appName = getString(R.string.app_name);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Reset Password [" + this.emailAddress + "] [" + appName + "]");
        String emailBody = generateEmailBody(appName);
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody);
    }

    private String generateEmailBody(String appName) {
        return "Hello " + this.emailAddress + ",\n" +
                "In order to reset your password, please type in the following code: " + this.generateRandomCode() + "\n" +
                "Please take into account that this is a one-time code.\n\n" +
                "The " + appName + " Team";
    }

    private int generateRandomCode() {
        Random random = new Random();
        this.oneTimeCode = random.nextInt(9000) + 1000;
        System.out.println("One time code is: " + oneTimeCode);
        return this.oneTimeCode;
    }
}