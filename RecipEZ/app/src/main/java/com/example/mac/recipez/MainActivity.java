package com.example.mac.recipez;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.example.mac.recipez.adapter.SlidingMenuAdapter;
import com.example.mac.recipez.fragment.CaloriesPage;
import com.example.mac.recipez.fragment.Favourites;
import com.example.mac.recipez.fragment.HomePage;
import com.example.mac.recipez.fragment.SupermarketMap;
import com.example.mac.recipez.fragment.Login;

import com.example.mac.recipez.fragment.Search;
import com.example.mac.recipez.fragment.Settings;
import com.example.mac.recipez.fragment.Fridge;
import com.example.mac.recipez.fragment.SignUp;
import com.example.mac.recipez.model.ItemSlideMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private List<ItemSlideMenu> listSliding;


    private SlidingMenuAdapter adapter;
    private ListView listView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private AlertDialog userDialog;
    private ProgressDialog waitDialog;

    private CognitoUser user;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserDBHelper.init(getApplicationContext());

        //Get user detail if logged in
//        userInit();
        findCurrent();


        //Initialise side menu
        listView = (ListView) findViewById(R.id.sliding_menu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        listSliding = new ArrayList<>();

        listSliding.add(0, new ItemSlideMenu(R.drawable.home_icon, "Home"));
        listSliding.add(1, new ItemSlideMenu(R.drawable.search_icon, "Search"));
        listSliding.add(2, new ItemSlideMenu(R.drawable.settings_icon, "Settings"));
        listSliding.add(3, new ItemSlideMenu(R.drawable.red_heart, "Favorites"));
        listSliding.add(4, new ItemSlideMenu(R.drawable.fridge_icon, "Fridge"));
        listSliding.add(5, new ItemSlideMenu(0, "Calories"));
        listSliding.add(6, new ItemSlideMenu(0, "Supermarket Map"));
        if (!isLoggedIn()) {
            listSliding.add(7, new ItemSlideMenu(0, "Register"));
            listSliding.add(8, new ItemSlideMenu(0, "Login"));
        } else {
            listSliding.add(7, new ItemSlideMenu(0, "Logout"));
        }

        adapter = new SlidingMenuAdapter(this, listSliding);
        listView.setAdapter(adapter);

        //Set actionBar icon to go back to the home page
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Set title of the Main Activity to be "Home"
        setTitle(listSliding.get(0).getTitle());

        //Item selected
        listView.setItemChecked(0, true);
        //Close menu
        drawerLayout.closeDrawer(listView);

        //Display home_page on main page
        replacementFragment(0);
        //Clicking the item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                //Set title
                setTitle(listSliding.get(i).getTitle());
                //item selected
                listView.setItemChecked(i, true);
                //replace fragment
                replacementFragment(i);
                //close menu
                drawerLayout.closeDrawer(listView);
            }
        });


        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_opened, R.string.drawer_closed) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        checkForUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForCrashes();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterManagers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterManagers();
    }

    // For menu on the RHS
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    private void replacementFragment(int pos) {
        Fragment fragment = null;
        switch (pos) {
            case 0:
                fragment = new HomePage();
                break;
            case 1:
                fragment = new Search();
                break;
            case 2:
                fragment = new Settings();
                break;
            case 3:
                fragment = new Favourites();
                break;
            case 4:
                if (!isLoggedIn()) {
                    fragment = new Login();
                    CharSequence text = "Please login before using the fridge!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                    toast.show();
                } else {
                    fragment = new Fridge();
                }
                break;

            case 5:
                fragment = new CaloriesPage();
                break;
            case 6:
                fragment = new SupermarketMap();
                break;
            case 7:
                if (!isLoggedIn()) {
                    fragment = new SignUp();
                } else {
                    signOut();
                }
                break;
            case 8:
                fragment = new Login();
                break;
            default:
                break;
        }

        if(fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.main_content, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

//    private void userInit() {
//        Bundle extras = getIntent().getExtras();
//        username = UserDBHelper.getCurrUser();
//        if (username != null) {
//            user = UserDBHelper.getUserPool().getUser(username);
//            getDetails();
//        }
//    }

    private void findCurrent() {
        user = UserDBHelper.getUserPool().getCurrentUser();
        username = user.getUserId();
        Log.e("MainActivity", "getCurrentID: " + username);
        if(username != null) {
            UserDBHelper.setUser(username);
//            inUsername.setText(user.getUserId());
            user.getSessionInBackground(authenticationHandler);
        }
    }

    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {
            Log.e("MainAcitivity", "Auth Success");
            UserDBHelper.setCurrSession(cognitoUserSession);
            UserDBHelper.newDevice(device);
            closeWaitDialog();
            launchUser();
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String username) {
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
        }

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {
        }

        @Override
        public void onFailure(Exception exception) {
        }
    };

    private void signOut() {
        user.signOut();
        UserDBHelper.setUser(null);
        getApplicationContext().getSharedPreferences("CognitoIdentityProviderCache", 0).edit().clear().commit();
        Log.e("MainActivity", "Logout: getCurrentID: " + user.getUserId());
        returnHome();
    }

    private void launchUser() {
        Fragment fragment = new HomePage();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_content, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void returnHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void getDetails() {
        UserDBHelper.getUserPool().getUser(username).getDetailsInBackground(detailsHandler);
    }

    GetDetailsHandler detailsHandler = new GetDetailsHandler() {
        @Override
        public void onSuccess(CognitoUserDetails cognitoUserDetails) {
            closeWaitDialog();
            // Store details in the AppHandler
            UserDBHelper.setUserDetails(cognitoUserDetails);
//            showAttributes();
        }

        @Override
        public void onFailure(Exception exception) {
            closeWaitDialog();
            showDialogMessage("Could not fetch user details!", UserDBHelper.formatException(exception), true);
        }
    };

    private void closeWaitDialog() {
        try {
            waitDialog.dismiss();
        }
        catch (Exception e) {
            //
        }
    }

    private void showDialogMessage(String title, String body, final boolean exit) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                    if(exit) {
                        exit();
                    }
                } catch (Exception e) {
                    // Log failure
                    Log.e("MainActivity","Dialog dismiss failed");
                    if(exit) {
                        exit();
                    }
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    private void exit() {
        Intent intent = new Intent();
        if(username == null)
            username = "";
        intent.putExtra("name",username);
        setResult(RESULT_OK, intent);
        finish();
    }


    private void checkForCrashes() {
        CrashManager.register(this);
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this);
    }

    private void unregisterManagers() {
        UpdateManager.unregister();
    }


    private boolean isLoggedIn() {
        return username != null;
    }

    public String getUsername() {
        return username;
    }

    //TODO: for googleMap
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        List<Fragment> fragments = getSupportFragmentManager().getFragments();
//        if (fragments != null) {
//            for (Fragment fragment : fragments) {
//                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
//            }
//        }
//
//    }

}
