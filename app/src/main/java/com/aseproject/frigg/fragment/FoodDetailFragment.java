package com.aseproject.frigg.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aseproject.frigg.R;
import com.aseproject.frigg.activity.FoodDetailActivity;
import com.aseproject.frigg.model.FoodItem;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FoodDetailFragment extends Fragment {

    private String type = "";
    private Context context;

    private LinearLayout llFoodItem;
    private ImageView ivItemImage;
    private Object foodItem;
    private TextView tvItemName;
    private TextView tvItemAmount;
    private LinearLayout llExpiryDate;
    private TextView tvExpiryDate;
    private TextView tvPurchaseDate;

    public FoodDetailFragment(String type) {
        this.type = type;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_food_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((FoodDetailActivity) context).setTitle(context.getString(R.string.food_detail_title));
        llFoodItem = view.findViewById(R.id.ll_food_item);
        tvItemName = view.findViewById(R.id.foodItemName);
        ivItemImage = view.findViewById(R.id.iv_item_image);
        tvItemAmount = view.findViewById(R.id.foodAmt);
        llExpiryDate = view.findViewById(R.id.llExpiryDate);
        tvExpiryDate = view.findViewById(R.id.tvExpiryDate);
        tvPurchaseDate = view.findViewById(R.id.tvPurchaseDate);

        foodItem = getActivity().getIntent().getSerializableExtra("FOOD_ITEM");

        if (foodItem instanceof FoodItem)
            setDetails((FoodItem) foodItem);
    }

    private void setDetails(FoodItem foodItem) {
        tvItemName.setText(foodItem.getItemName());
        tvItemAmount.setText("Amount: " + foodItem.getQuantity());
        if (type.equals(context.getString(R.string.grocery_title))) {
            llExpiryDate.setVisibility(View.GONE);
        } else {
            tvExpiryDate.setText("Expiry date: "+ foodItem.getExpected_expiry_date());
            tvPurchaseDate.setText("Purchase date: "+ foodItem.getPurchase_date());
        }
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler();
        executor.execute(() -> {
            try {
                String imageURL = context.getString(R.string.image_url, foodItem.getItemName());
                InputStream inputStream = new URL(imageURL).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);


                // Only for making changes in UI
                handler.post(() -> ivItemImage.setImageBitmap(bitmap));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}