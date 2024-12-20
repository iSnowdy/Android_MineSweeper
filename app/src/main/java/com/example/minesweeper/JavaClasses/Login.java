package com.example.minesweeper.JavaClasses;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minesweeper.Activities.MainActivity;
import com.example.minesweeper.R;
import com.example.minesweeper.JavaClasses.Utils.JavaMailUtil;
import com.example.minesweeper.JavaClasses.Utils.SharedPreferencesUtil;
import com.example.minesweeper.JavaClasses.Utils.ToastUtil;

import java.util.Map;
import java.util.Random;

public class Login extends AppCompatActivity {
    private EditText
            usernameEditText, userPasswordEditText;
    private String
            username, emailAddress, password;
    private Button
            loginButton, signInButton;
    private TextView forgotPasswordTextView;
    private int oneTimeCode;
    private Map<String, ?> allUsers;

    // Forgot password resources
    private EditText
            usernameEditTextPopUpWindow, emailEditTextPopUpWindow, oneTimeCodeEditText, forgotPasswordEditText, forgotRepeatPasswordEditText;
    private Button
            sendEmailButton, submitCodeButton, changePasswordButton;
    private LayoutInflater popUpWindowLayoutInflater;
    private View popUpView;
    private PopupWindow popupWindow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        getAllUsers();
        printAllUsers();
        loadLoginViews();
        setLoginListeners();
    }

    private void getAllUsers() {
        this.allUsers = SharedPreferencesUtil.getAllUsers(this);
    }

    private void printAllUsers() { // TODO: Remove later on
        System.out.println("Printing all users in Shared Preferences:");
        for (Map.Entry<String, ?> entry : this.allUsers.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    // Helper methods
    private void redirectMessageToast(String message) {
        ToastUtil.createToast(this, message);
    }

    private String retrieveInformationFromViewAsString(EditText view) {
        return view.getText().toString();
    }

    private void loadLoginViews() {
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
        usernameEditTextPopUpWindow = this.popupWindow.getContentView().findViewById(R.id.usernameForPopUpWindow);
        emailEditTextPopUpWindow = this.popupWindow.getContentView().findViewById(R.id.emailPopUpWindow);
        sendEmailButton = this.popupWindow.getContentView().findViewById(R.id.sendEmailButtonPopUpWindow);
        oneTimeCodeEditText = this.popupWindow.getContentView().findViewById(R.id.oneTimeCodePopUpWindow);
        forgotPasswordEditText = this.popupWindow.getContentView().findViewById(R.id.forgotPasswordPopUpWindow);
        forgotRepeatPasswordEditText = this.popupWindow.getContentView().findViewById(R.id.forgotRepeatPasswordPopUpWindow);
        submitCodeButton = this.popupWindow.getContentView().findViewById(R.id.submitCodeButtonPopUpWindow);
        changePasswordButton = this.popupWindow.getContentView().findViewById(R.id.changePasswordPopUpWindow);
    }

    private void hideView(View view) {
        view.setVisibility(View.INVISIBLE);
    }

    private void revealView(View view) {
        view.setVisibility(View.VISIBLE);
    }

    private void setForgotPasswordEditTextVisibilityOn() {
        revealView(forgotPasswordEditText);
        revealView(forgotRepeatPasswordEditText);
    }

    private void setLoginListeners() {
        loginButton.setOnClickListener(v -> setLoginOnClickListener());
        signInButton.setOnClickListener(v -> setRegisterOnClickListener());
        forgotPasswordTextView.setOnClickListener(v -> setForgotPasswordOnClickListener());
    }

    private void setLoginOnClickListener() {
        if (verifyLogin()) {
            this.username = retrieveInformationFromViewAsString(usernameEditText);
            redirectToMainActivity();
        }
    }
    // TODO: Somehow can login if I don't type anything
    private boolean verifyLogin() {
        this.username = retrieveInformationFromViewAsString(usernameEditText);

        String savedUserPassword = SharedPreferencesUtil.getUserInformation(this, this.username, SharedPreferences_Keys.PASSWORD.toString(), "");
        return isUsernameValid()
                && isUserPasswordValid(savedUserPassword);
    }

    private boolean isUsernameValid() {
        String storedEmail = SharedPreferencesUtil.getUserInformation(this, this.username, SharedPreferences_Keys.EMAIL_ADDRESS.toString(), "");
        if (storedEmail == null) {
            redirectMessageToast("User does not exist");
            return false;
        }
        // The email is valid and therefore set it to instance variable
        this.emailAddress = storedEmail;
        return true;
    }

    private boolean isUserPasswordValid(String userPassword) {
        String givenPassword = retrieveInformationFromViewAsString(userPasswordEditText);

        if (!userPassword.equals(givenPassword)) {
            redirectMessageToast("Username or password is not valid");
            return false;
        }
        // Password is correct
        this.password = givenPassword;
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
        createPopUpWindow();

        setListenerForSendEmailButton();
        setListenerForSubmitCodeButton();
    }

    // https://www.youtube.com/watch?v=wxqgtEewdfo
    // https://stackoverflow.com/questions/27259614/android-popupwindow-elevation-does-not-show-shadow/50211489#50211489
    public void createPopUpWindow() {
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
    }

    private void setListenerForSendEmailButton() {
        sendEmailButton.setOnClickListener(v -> sendEmailButtonOnClickListener());
    }

    private void sendEmailButtonOnClickListener() {
        String userEmailAddress = retrieveInformationFromViewAsString(emailEditTextPopUpWindow);
        String usernameInput = retrieveInformationFromViewAsString(usernameEditTextPopUpWindow);

        if (isEmailAddressValid(usernameInput, userEmailAddress)) {
            sendEmailToUserToResetPassword();
            redirectMessageToast("An email has been sent to you");
        } else {
            redirectMessageToast("Invalid email address");
        }
    }

    private boolean isEmailAddressValid(String usernameInput, String emailAddressInput) {
        // Iterates through every email in the SharedPreferences in search
        // for the user's email address

        System.out.println("Username input: " + usernameInput);
        System.out.println("Email address input: " + emailAddressInput);

        for (Map.Entry<String, ?> entry : this.allUsers.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();

            // Check for matches of username AND email address
            if (value.equals(emailAddressInput)) {

                System.out.println("Inside IF");

                this.username = usernameInput;
                this.emailAddress = emailAddressInput;

                //System.out.println("Found matching username and email in Shared Preferences: " + usernameInput + " - " + emailAddressInput);

                hideView(usernameEditTextPopUpWindow);
                hideView(emailEditTextPopUpWindow);
                hideView(sendEmailButton);
                revealView(oneTimeCodeEditText);
                revealView(submitCodeButton);
                return true; // Valid username and email found
            }
        }
        System.out.println("No matches found");
        return false;
    }

    private void sendEmailToUserToResetPassword() {
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
        return this.oneTimeCode;
    }

    private void setListenerForSubmitCodeButton() {
        submitCodeButton.setOnClickListener(v -> submitCodeButtonOnClickListener());
    }

    private void submitCodeButtonOnClickListener() {
        String oneTimeCodeAsInput = retrieveInformationFromViewAsString(oneTimeCodeEditText);

        if (isOneTimeCodeValid(oneTimeCodeAsInput)) {
            handleCorrectCode();
        } else {
            handleIncorrectCode();
        }
    }

    private boolean isOneTimeCodeValid(String oneTimeCodeAsInput) {
        return Integer.parseInt(oneTimeCodeAsInput) == this.oneTimeCode;
    }

    private void handleCorrectCode() {
        setForgotPasswordEditTextVisibilityOn();
        hideView(oneTimeCodeEditText);
        hideView(submitCodeButton);
        revealView(changePasswordButton);
        setChangePasswordButtonListener();
    }

    private void handleIncorrectCode() {
        redirectMessageToast("Incorrect code");
        this.popupWindow.dismiss();
    }

    private void setChangePasswordButtonListener() {
        changePasswordButton.setOnClickListener(v -> processPasswordChangeAction());
    }

    private void processPasswordChangeAction() {
        if (isNewPasswordValid()) {
            updatePasswordInSharedPreferences(this.username);
            redirectMessageToast("Password changed successfully");
            this.popupWindow.dismiss();
        } else {
            redirectMessageToast("Invalid Passwords. Please try again");
            this.popupWindow.dismiss();
        }
    }
    // Validation Regex for Password found in Register
    // Password must be at least 6 characters long and contain at least one letter and one number
    private boolean isNewPasswordValid() {
        String newPassword = retrieveInformationFromViewAsString(forgotPasswordEditText);
        String repeatPassword = retrieveInformationFromViewAsString(forgotRepeatPasswordEditText);

        return newPassword.equals(repeatPassword) &&
                newPassword.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$");
    }

    private void updatePasswordInSharedPreferences(String username) {
        String newPassword = retrieveInformationFromViewAsString(forgotPasswordEditText);
        // Deletes old password before creating the new one
        SharedPreferencesUtil.deleteUserInformation(this, username, SharedPreferences_Keys.PASSWORD.toString());
        // Now, save the new one
        SharedPreferencesUtil.saveUserInformation(this, username, SharedPreferences_Keys.PASSWORD.toString(), newPassword);
    }
}