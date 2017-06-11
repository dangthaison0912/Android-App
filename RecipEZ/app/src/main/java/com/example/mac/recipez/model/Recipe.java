package com.example.mac.recipez.model;

/**
 * Created by Yvonne Kong on 31/5/2017.
 */

public class Recipe {

    private int image;
    private String name;

    public Recipe(int image, String name) {
        this.image = image;
        this.name = name;
    }

    public Recipe(String name){
        this.name = name;
    }
    public int getImage() {
        return image;
    }

    public String getName() {
        return name;
    }
}
