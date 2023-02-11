package com.aseproject.frigg.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FoodItem implements Serializable {
    private Integer food_item_id;
    private String food_item_name = "";
    private Integer quantity = 1;
    private String expected_expiry_date = "";
    private String purchase_date = "";
    private boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public Integer getFood_item_id() {
        return food_item_id;
    }

    public String getItemName() {
        return food_item_name;
    }

    public void setFood_item_name(String food_item_name) {
        this.food_item_name = food_item_name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getExpected_expiry_date() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        Date localDate = new Date();
        if (!expected_expiry_date.isEmpty()) {
            try {
                localDate = sdf.parse(expected_expiry_date);
            } catch (ParseException e) {
            }
        }
        sdf = new SimpleDateFormat("dd MMM, yyyy");
        return sdf.format(localDate);
    }

    public void setExpected_expiry_date(String expected_expiry_date) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());
        Date localDate = new Date();
        try {
            localDate = sdf.parse(expected_expiry_date);
        } catch (ParseException e) {
        }

        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        this.expected_expiry_date = sdf.format(localDate);
    }

    public String getPurchase_date() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        Date localDate = new Date();
        if (!purchase_date.isEmpty()) {
            try {
                localDate = sdf.parse(purchase_date);
            } catch (ParseException e) {
            }
        }
        sdf = new SimpleDateFormat("dd MMM, yyyy");
        return sdf.format(localDate);
    }

    public void setPurchase_date(String purchase_date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());
        Date localDate = new Date();
        try {
            localDate = sdf.parse(purchase_date);
        } catch (ParseException e) {
        }

        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        this.purchase_date = sdf.format(localDate);
    }
}
