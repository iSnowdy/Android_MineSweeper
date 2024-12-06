package com.example.minesweeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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

    // Forgot password resources
    private EditText
            oneTimeCodeEditText, forgotPasswordEditText, forgotRepeatPasswordEditText;
    private Button submitCodeButton;
    private LayoutInflater popUpWindowLayoutInflater;
    private View popUpView;
    private PopupWindow popupWindow;


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

    private void loadForgotPasswordViews() {
        // A "casting" is needed in order to properly load the views. This is because
        // if we were to simply do findViewById(), it would throw a Null Pointer Exception
        // since it tries to find them in the root layout (R.layout.login)
        oneTimeCodeEditText = this.popupWindow.getContentView().findViewById(R.id.oneTimeCodePopUpWindow);
        forgotPasswordEditText = this.popupWindow.getContentView().findViewById(R.id.forgotPasswordPopUpWindow);
        forgotRepeatPasswordEditText = this.popupWindow.getContentView().findViewById(R.id.forgotRepeatPasswordPopUpWindow);
        submitCodeButton = this.popupWindow.getContentView().findViewById(R.id.submitCodeButtonPopUpWindow);
    }

    private void setForgotPasswordEditTextVisibilityOff() {
        forgotPasswordEditText.setVisibility(View.INVISIBLE);
        forgotRepeatPasswordEditText.setVisibility(View.INVISIBLE);

    }

    private void setForgotPasswordEditTextVisibilityOn() {
        forgotPasswordEditText.setVisibility(View.VISIBLE);
        forgotRepeatPasswordEditText.setVisibility(View.VISIBLE);
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
        String storedUsername = this.loginSharedPreferences.getString(SharedPreferences_Keys.USERNAME.toString(), "");
        String storedPassword = this.loginSharedPreferences.getString(SharedPreferences_Keys.PASSWORD.toString(), "");
        return isUsernameValid(storedUsername)
                && isUserPasswordValid(storedPassword);
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
        // TODO: The password *should* be hashed
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
        this.forgotPasswordTextView.setOnClickListener(v -> forgotPasswordOnClickListener());
    }

    private void forgotPasswordOnClickListener() {
        System.out.println("Inside listener");
        sendEmailToUserToResetPassword();
        createPopUpWindow();
    }
    // https://www.youtube.com/watch?v=wxqgtEewdfo
    // https://stackoverflow.com/questions/27259614/android-popupwindow-elevation-does-not-show-shadow/50211489#50211489
    public void createPopUpWindow() {
        System.out.println("Inside pop up window");

        this.popUpWindowLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        this.popUpView = popUpWindowLayoutInflater.inflate(R.layout.popup_email_window, null);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;

        this.popupWindow = new PopupWindow(this.popUpView, width, height, focusable);

        this.popUpView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });

        this.popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        this.popupWindow.setAnimationStyle(1);
        this.popupWindow.setElevation(50);

        this.popupWindow.showAtLocation(findViewById(R.id.loginFrameLayout), Gravity.CENTER, 0, 0);
        // Now that we have the PopUp Window created, we can proceed to load the views inside it
        loadForgotPasswordViews();

        this.submitCodeButton.setOnClickListener(v -> submitCodeButtonOnClickListener());
    }

    private void submitCodeButtonOnClickListener() {
        String oneTimeCodeAsInput = retrieveInformationFromViewAsString(oneTimeCodeEditText);

        if (oneTimeCodeAsInput.equals(String.valueOf(this.oneTimeCode))) {
            System.out.println("Correct code!");
            // Correct code. Therefore, allow the user to change passwords
            setForgotPasswordEditTextVisibilityOn();
            // TODO: Call to methods to change password
            changeUserPassword();
        } else {
            System.out.println("Invalid Code");
            // Wrong code. Feedback + dismiss window
            redirectMessageToast("One time code is not valid");
            popupWindow.dismiss();
        }
    }

    private void changeUserPassword() {
        if (validateNewPassword()) changePassword();
        else {
            // Invalid passwords. They do not match and/or invalid format. Feedback
            redirectMessageToast("Invalid Passwords. Please try again");
            this.popupWindow.dismiss();
        }
    }
    // Validation Regex from Register
    private boolean validateNewPassword() {
        String newPassword = retrieveInformationFromViewAsString(forgotPasswordEditText);
        String repeatPassword = retrieveInformationFromViewAsString(forgotRepeatPasswordEditText);
        return newPassword.equals(repeatPassword) &&
                newPassword.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$");
    }

    private void changePassword() {
        String newPassword = retrieveInformationFromViewAsString(forgotPasswordEditText);
        this.loginSharedPreferencesEditor.putString(SharedPreferences_Keys.PASSWORD.toString(), newPassword);
        this.loginSharedPreferencesEditor.apply();
        redirectMessageToast("Password changed successfully");
    }

    private void sendEmailToUserToResetPassword() {
        System.out.println("Inside email intent");

        this.emailAddress = this.loginSharedPreferences.getString(SharedPreferences_Keys.EMAIL_ADDRESS.toString(), "");

        String hostEmail = getString(R.string.emailString);
        String hostPassword = getString(R.string.emailPassword);

        JavaMailUtil javaMailUtil = new JavaMailUtil(hostEmail, hostPassword, this.emailAddress);
        loadInformationToEmailIntent(javaMailUtil);
}

    private void loadInformationToEmailIntent(JavaMailUtil mailUtil) {

        String appName = getString(R.string.app_name);
        String emailSubject = "Reset Password [" + this.emailAddress + "] [" + appName + "]";
        String emailBody = generateEmailBody(appName);

        System.out.println("Email address inside email intent: " + this.emailAddress);
        System.out.println("BODY:\n\n");
        System.out.println(emailBody);
        // Uses the external class to send mails using pure Java
        mailUtil.setEmailSubject(emailSubject);
        mailUtil.setEmailBody(emailBody);
        mailUtil.sendEmailInBackground();
    }

    private String generateEmailBody(String appName) {
        return "Hello " + this.emailAddress + ",\n\n" +
                "In order to reset your password, please type in the following code: " + this.generateRandomCode() + "\n\n" +
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