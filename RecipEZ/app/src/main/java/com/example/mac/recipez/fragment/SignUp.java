package com.example.mac.recipez.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mac.recipez.MainActivity;
import com.example.mac.recipez.R;
import com.example.mac.recipez.UserDBHelper;
import com.example.mac.recipez.fragment.HomePage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class SignUp extends Fragment {

    private EditText username;
    private EditText name;
    private EditText password;
    private EditText email;

    private Button signUp;
    private AlertDialog userDialog;
    private ProgressDialog waitDialog;

    private String usernameInput;
    private String userpasswordInput;
    private String nameInput;
    private String emailInput;

    private String userPassword;

    public static final String USERNAME = "Username";
    public static final String NAME = "Name";

    private static View view;

    public SignUp() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_sign_up, container, false);
        UserDBHelper.init(getActivity().getApplicationContext());
        init();

        return view;
    }


//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sign_up);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//
//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            String value = extras.getString("TODO");
//            if (value.equals("exit")) {
//                onBackPressed();
//            }
//        }
//
//    }

    public View getView() {
        return view;
    }


    private void init() {
        username = (EditText) getView().findViewById(R.id.sign_up_username);
        name = (EditText) getView().findViewById(R.id.sign_up_name);
        email = (EditText) getView().findViewById(R.id.sign_up_email);
        password = (EditText) getView().findViewById(R.id.sign_up_password);

        signUp = (Button) getView().findViewById(R.id.sign_up_submit);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Read user data and register
                CognitoUserAttributes userAttributes = new CognitoUserAttributes();

                usernameInput = username.getText().toString();
                if (usernameInput == null || usernameInput.isEmpty()) {
                    TextView view = (TextView) getView().findViewById(R.id.textViewRegUserIdMessage);
                    view.setText(username.getHint() + " cannot be empty");
                    //username.setBackground(getDrawable(R.drawable.text_border_error));
                    return;
                }

                userpasswordInput = password.getText().toString();
                userPassword = userpasswordInput;
                if (userpasswordInput == null || userpasswordInput.isEmpty()) {
                    TextView view = (TextView) getView().findViewById(R.id.textViewRegUserPasswordMessage);
                    view.setText(password.getHint() + " cannot be empty");
                    //password.setBackground(getDrawable(R.drawable.text_border_error));
                    return;
                }

                nameInput = name.getText().toString();
                if (nameInput != null) {
                    if (nameInput.length() > 0) {
                        userAttributes.addAttribute(UserDBHelper.getSignUpFieldsC2O().get(name.getHint()).toString(), nameInput);
                    }
                }

                emailInput = email.getText().toString();
                if (emailInput != null) {
                    if (emailInput.length() > 0) {
                        userAttributes.addAttribute(UserDBHelper.getSignUpFieldsC2O().get(email.getHint()).toString(), emailInput);
                    }
                }

                showWaitDialog("Signing up...");
//                Toast.makeText(getActivity().getApplicationContext(), "Signing up...", Toast.LENGTH_SHORT);

                UserDBHelper.getUserPool().signUpInBackground(usernameInput, userpasswordInput, userAttributes, null, signUpHandler);

                InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            }

        });
    }

    SignUpHandler signUpHandler = new SignUpHandler() {
        @Override
        public void onSuccess(CognitoUser user, boolean signUpConfimationState,
                              CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            closeWaitDialog();
            Boolean regState = signUpConfimationState;
            if (signUpConfimationState) {

                JSONObject userJSON = new JSONObject();
                try {
                    Log.e("Signup", usernameInput);
                    Log.e("Signup", nameInput);
                    userJSON.put(USERNAME, usernameInput);
                    userJSON.put(NAME, nameInput);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                addIntoDatabase(userJSON);
                showDialogMessage("Sign up successful!", usernameInput + " has been confirmed", true);
//                Toast.makeText(getActivity().getApplicationContext(), "Sign up successful!", Toast.LENGTH_SHORT);


            } else {
                //User is not confirmed
                Toast.makeText(getActivity().getApplicationContext(), "Error on signing up, try again!"
                        , Toast.LENGTH_LONG);
            }
        }

        @Override
        public void onFailure(Exception exception) {
            closeWaitDialog();
            TextView label = (TextView) getView().findViewById(R.id.textViewRegUserIdMessage);
            label.setText("Sign up failed");
            //username.setBackground(getDrawable(R.drawable.text_border_error));
            showDialogMessage("Sign up failed", UserDBHelper.formatException(exception), false);

        }
    };

    private void addIntoDatabase(final JSONObject userJSON) {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String url = "http://www.doc.ic.ac.uk/project/2016/271/g1627110/web/api/register.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response:", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response:", error.toString());
                    }
                }
        ){
            @Override
            public byte[] getBody() throws AuthFailureError {
                return userJSON.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        requestQueue.add(postRequest);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if(resultCode == RESULT_OK){
                String name = null;
                if(data.hasExtra("name")) {
                    name = data.getStringExtra("name");
                }
                exit(name, userPassword);
            }
        }
    }

    private void showDialogMessage(String title, String body, final boolean exit) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                    if(exit) {
                        UserDBHelper.getUserPool().getUser(usernameInput).getSessionInBackground(authenticationHandler);
                        exit(usernameInput);
                    }
                } catch (Exception e) {
                    if(exit) {
                        exit(usernameInput);
                    }
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    private void showWaitDialog(String message) {
        closeWaitDialog();
        waitDialog = new ProgressDialog(getActivity());
        waitDialog.setTitle(message);
        waitDialog.show();
    }

    private void closeWaitDialog() {
        try {
            waitDialog.dismiss();
        }
        catch (Exception e) {
            //
        }
    }

    private void exit(String username) {
        exit(username, null);
    }

    private void exit(String username, String password) {
        Intent intent = new Intent();
        Fragment fragment = new HomePage();
        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        intent.putExtra("name", username);
        intent.putExtra("password", password);
        getActivity().setResult(RESULT_OK, intent);

        FragmentTransaction t = this.getFragmentManager().beginTransaction();
        t.replace(R.id.main_content, fragment);
        t.commit();

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
//            showDialogMessage("Forgot password failed",UserDBHelper.formatException(exception));
        }
    };

    private void getUserAuthentication(AuthenticationContinuation continuation, String username) {
        if(username != null) {
            usernameInput = username;
            UserDBHelper.setUser(username);
        }

        AuthenticationDetails authenticationDetails = new AuthenticationDetails(usernameInput, userpasswordInput, null);
        continuation.setAuthenticationDetails(authenticationDetails);
        continuation.continueTask();
    }

    private void launchUser() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("name", usernameInput);
        startActivity(intent);
    }



}
