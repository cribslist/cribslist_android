package com.codepath.cribslist.constants;

import java.util.ArrayList;

public enum ItemCategory {
    CLOTHING_AND_SHOES("Clothing & Shoes"),
    FOODS("Foods"),
    DIAPERING("Diapering"),
    TOYS("Toys"),
    CAR_SEATS_AND_ACCESSORIES("Car Seats & Accessories"),
    STROLLERS_AND_ACCESSORIES("Strollers & Accessories"),
    OTHERS("Others");

    private final String text;

    ItemCategory(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public int getIndex() {
        if (text.equals(CLOTHING_AND_SHOES.toString())) {
            return 0;
        } else if (text.equals(FOODS.toString())){
            return 1;
        } else if (text.equals(DIAPERING.toString())){
            return 2;
        } else if (text.equals(TOYS.toString())){
            return 3;
        } else if (text.equals(CAR_SEATS_AND_ACCESSORIES.toString())){
            return 4;
        } else if (text.equals(STROLLERS_AND_ACCESSORIES.toString())){
            return 5;
        } else if (text.equals(OTHERS.toString())){
            return 6;
        }  else {
            return -1;
        }
    }

    public static ArrayList<String> getArray() {
        ArrayList<String> category = new ArrayList<>();
        category.add(CLOTHING_AND_SHOES.toString());
        category.add(FOODS.toString());
        category.add(DIAPERING.toString());
        category.add(TOYS.toString());
        category.add(CAR_SEATS_AND_ACCESSORIES.toString());
        category.add(STROLLERS_AND_ACCESSORIES.toString());
        category.add(OTHERS.toString());
        return category;
    }
}
