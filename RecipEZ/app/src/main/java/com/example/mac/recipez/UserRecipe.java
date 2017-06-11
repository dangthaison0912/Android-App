package com.example.mac.recipez;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by julia on 31-May-17.
 */

public class UserRecipe extends AppCompatActivity {

    private static final int NUM_OF_FIELDS = 4;
    EditText etRecipeName, etIngredient, etAmount, etSteps;
    Button btnShare;
    String recipeName, ingredients, amount, steps;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Add a New Recipe");
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.user_recipe);

        etRecipeName = (EditText) findViewById(R.id.editTextRN);
        etIngredient = (EditText) findViewById(R.id.editTextIng);
        etAmount = (EditText) findViewById(R.id.editTextAmount);
        etSteps = (EditText) findViewById(R.id.editTextSteps);
        btnShare = (Button) findViewById(R.id.buttonShare);

        loadPreference();

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeName = etRecipeName.getText().toString();
                ingredients = etIngredient.getText().toString();
                amount = etAmount.getText().toString();
                steps = etSteps.getText().toString();

                if (recipeName.isEmpty()
                        || ingredients.isEmpty()
                        || amount.isEmpty()
                        || steps.isEmpty()) {
                    setEmptyFieldError();
                    Toast.makeText(getApplicationContext(), "Please fill in the necessary fields."
                            , Toast.LENGTH_SHORT).show();
                } else {

                    JSONObject ingredientJSON = new JSONObject();
                    try {
                        ingredientJSON.put(ingredients, amount);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONObject bodyJSON = new JSONObject();
                    try {
                        bodyJSON.put("Recipe_name", recipeName);
                        bodyJSON.put("Ingredients", ingredientJSON);
                        bodyJSON.put("Steps", steps);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    addIntoServer(bodyJSON);
                    Toast.makeText(getApplicationContext(), "Your recipe has been added successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

    }

    private void addIntoServer(final JSONObject bodyJSON) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "http://www.doc.ic.ac.uk/project/2016/271/g1627110/web/api/addRecipe.php";
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
                return bodyJSON.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        requestQueue.add(postRequest);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_recipe_clear, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.clear_button:
                clearAllFields();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clearAllFields() {
        etRecipeName.setText(null);
        etIngredient.setText(null);
        etAmount.setText(null);
        etSteps.setText(null);
    }

    @Override
    public void onBackPressed() {
        savePreferences();
        super.onBackPressed();
    }

    // To save text in the user recipe page when the back arrow or back key is pressed
    private void savePreferences() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        recipeName = etRecipeName.getText().toString();
        ingredients = etIngredient.getText().toString();
        amount = etAmount.getText().toString();
        steps = etSteps.getText().toString();

        editor.putString("recipeName", recipeName);
        editor.putString("ingredients", ingredients);
        editor.putString("amount", amount);
        editor.putString("steps", steps);

        editor.apply();
    }

    // Restore the saved preferences when back key is pressed
    private void loadPreference() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        recipeName = sharedPreferences.getString("recipeName", null);
        ingredients = sharedPreferences.getString("ingredients", null);
        amount = sharedPreferences.getString("amount", null);
        steps = sharedPreferences.getString("steps", null);

        etRecipeName.setText(recipeName);
        etIngredient.setText(ingredients);
        etAmount.setText(amount);
        etSteps.setText(steps);
    }

    private void setEmptyFieldError() {
        if (recipeName.isEmpty()) {
            etRecipeName.setError("\"Recipe Name\" cannot be empty");
        }
        if (ingredients.isEmpty()) {
            etIngredient.setError("\"Ingredients\" cannot be empty");
        }
        if (amount.isEmpty()) {
            etAmount.setError("\"Amount\" cannot be empty");
        }
        if (steps.isEmpty()) {
            etSteps.setError("\"Steps\" cannot be empty");
        }

    }
}
