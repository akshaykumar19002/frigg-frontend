package com.aseproject.frigg.model;

import java.util.List;

public class Ingredient {
    private List<String> fridge;
    private List<String> grocery;
    private String directions;

    public List<String> getFridge() {
        return fridge;
    }

    public List<String> getGrocery() {
        return grocery;
    }

    public String getDirections() {
        return directions;
    }
}
