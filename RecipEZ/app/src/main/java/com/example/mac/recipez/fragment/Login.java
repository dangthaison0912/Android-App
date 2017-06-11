package com.example.mac.recipez.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.NewPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.example.mac.recipez.MainActivity;
import com.example.mac.recipez.R;
import com.example.mac.recipez.UserDBHelper;
import com.example.mac.recipez.fragment.HomePage;

import java.util.Locale;

/**
 * Created by dexterfung on 7/6/2017.
 */

public class Login extends Fragment implements View.OnClickListener{

    private static View view;
    private static FragmentManager fragmentManager;

    private EditText usernameET;
    private EditText passwordET;
    private LinearLayout loginLayout;

    private String usernameInput;
    private String passwordInput;
    private Button loginButton;

    private ProgressDialog waitDialog;
    private AlertDialog userDialog;

    private MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation;
    private NewPasswordContinuation newPasswordContinuation;

    public Login() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_login, container, false);

        UserDBHelper.init(getActivity().getApplicationContext());

        initViews();
        setListeners();
        return view;
    }

    private void initViews() {
        fragmentManager = getActivity().getSupportFragmentManager();

        usernameET = (EditText) view.findViewById(R.id.login_username);
        passwordET = (EditText) view.findViewById(R.id.login_password);
        loginButton = (Button) view.findViewById(R.id.login_submit);
        loginLayout = (LinearLayout) view.findViewById(R.id.login_layout);
    }

    private void setListeners() {
        loginButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_submit:
                usernameInput = usernameET.getText().toString();
                passwordInput = passwordET.getText().toString();
//                UserDBHelper.getUserPool().getUser(usernameInput).getSessionInBackground(authenticationHandler);
                signInUser();
                break;
            default:
                break;

        }

        InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {
            Log.e("MainAcitivity", "Auth Success");
            UserDBHelper.setCurrSession(cognitoUserSession);
//            UserDBHelper.newDevice(device);
            closeWaitDialog();
            launchUser();
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String username) {
            closeWaitDialog();
            Locale.setDefault(Locale.UK);
            getUserAuthentication(authenticationContinuation, username);
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
            closeWaitDialog();
        }

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {
            closeWaitDialog();
//            if ("NEW_PASSWORD_REQUIRED".equals(continuation.getChallengeName())) {
//                // This is the first sign-in attempt for an admin created user
//                newPasswordContinuation = (NewPasswordContinuation) continuation;
//                UserDBHelper.setUserAttributeForDisplayFirstLogIn(newPasswordContinuation.getCurrentUserAttributes(),
//                        newPasswordContinuation.getRequiredAttributes());
//                closeWaitDialog();
//                firstTimeSignIn();
//            }
        }

        @Override
        public void onFailure(Exception exception) {
            closeWaitDialog();
            showDialogMessage("Forgot password failed",UserDBHelper.formatException(exception));
        }
    };

    private void getUserAuthentication(AuthenticationContinuation continuation, String username) {
        if(username != null) {
            usernameInput = username;
            UserDBHelper.setUser(username);
        }
        if(this.passwordInput == null) {
            usernameET.setText(username);
            passwordInput = passwordET.getText().toString();
            if(passwordInput == null) {
                TextView label = (TextView) view.findViewById(R.id.textViewLoginPasswordMessage);
                label.setText(passwordET.getHint()+" enter password");
//                passwordET.setBackground(getDrawable(R.drawable.text_border_error));
                return;
            }

            if(passwordInput.length() < 1) {
                TextView label = (TextView) view.findViewById(R.id.textViewLoginPasswordMessage);
                label.setText(passwordET.getHint()+" enter password");
//                passwordET.setBackground(getDrawable(R.drawable.text_border_error));
                return;
            }
        }
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(usernameInput, passwordInput, null);
        continuation.setAuthenticationDetails(authenticationDetails);
        continuation.continueTask();
    }

//    private void mfaAuth(MultiFactorAuthenticationContinuation continuation) {
//        multiFactorAuthenticationContinuation = continuation;
//        Intent mfaActivity = new Intent(this, MFAActivity.class);
//        mfaActivity.putExtra("mode", multiFactorAuthenticationContinuation.getParameters().getDeliveryMedium());
//        startActivityForResult(mfaActivity, 5);
//    }


    private void launchUser() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("name", usernameInput);
        startActivity(intent);
    }


    private void closeWaitDialog() {
        try {
            waitDialog.dismiss();
        }
        catch (Exception e) {
            //
        }
    }

    private void showDialogMessage(String title, String body) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                } catch (Exception e) {
                    //
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }


    private void signInUser() {

        usernameInput = usernameET.getText().toString();
        if(usernameInput == null || usernameInput.length() < 1) {
            TextView label = (TextView) view.findViewById(R.id.textViewLoginIdMessage);
            label.setText(usernameET.getHint()+" cannot be empty");
//            usernameET.setBackground(getDrawable(R.drawable.text_border_error));
            return;
        }

        UserDBHelper.setUser(usernameInput);

        passwordInput = passwordET.getText().toString();
        if(passwordInput == null || passwordInput.length() < 1) {
            TextView label = (TextView) view.findViewById(R.id.textViewLoginPasswordMessage);
            label.setText(passwordET.getHint()+" cannot be empty");
//            inPassword.setBackground(getDrawable(R.drawable.text_border_error));
            return;
        }

        showWaitDialog("Signing in...");
        UserDBHelper.getUserPool().getUser(usernameInput).getSessionInBackground(authenticationHandler);
    }

    private void showWaitDialog(String message) {
        closeWaitDialog();
        waitDialog = new ProgressDialog(getActivity());
        waitDialog.setTitle(message);
        waitDialog.show();
    }


}
