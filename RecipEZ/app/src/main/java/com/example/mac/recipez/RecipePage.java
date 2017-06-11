package com.example.mac.recipez;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Yvonne Kong on 31/5/2017.
 */

public class RecipePage extends AppCompatActivity {

    private TextView recipeName;
    private TextView amount;
    private TextView ingredient;
    private TextView steps;
    private ImageView recipeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Recipe");
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.recipe_page);

        recipeName = (TextView) findViewById(R.id.recipe_name);
        amount = (TextView) findViewById(R.id.amount);
        ingredient = (TextView) findViewById(R.id.ingredient);
        steps = (TextView) findViewById(R.id.steps);
        recipeImage = (ImageView) findViewById(R.id.recipe_image);

        recipeName.setText("Chicken Salad (SAMPLE)");
        amount.setText("400g");
        ingredient.setText("Chicken breast");
        steps.setText("1. Wash salad.\n"
                + "2. Marinate chicken breast with salt and pepper.\n"
                + "3. Fry chicken breast\n"
                + "4. Place chicken breast and salad into bowl\n"
                + "5. Add croutons\n");
        recipeImage.setImageResource(R.drawable.chicken_salad);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favourite_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_favorite:
                // TODO: Add to favourite and set to black
                Toast.makeText(getApplicationContext(), "This recipe has been added to your favourites."
                        , Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
