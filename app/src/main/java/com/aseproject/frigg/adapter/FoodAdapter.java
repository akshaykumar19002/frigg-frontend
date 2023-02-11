package com.aseproject.frigg.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.aseproject.frigg.R;
import com.aseproject.frigg.activity.DishRecipeActivity;
import com.aseproject.frigg.activity.NavActivity;
import com.aseproject.frigg.common.CommonDialogFragment;
import com.aseproject.frigg.model.FoodItem;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.GroceryHolder> {

    private static final String TAG = FoodAdapter.class.getSimpleName();
    private Activity context;
    private final boolean enableEditMode;
    private final GroceryHolderListener listener;
    private final String type;
    private List<FoodItem> foodList = new ArrayList<>();

    public FoodAdapter(Activity context, String type, List<FoodItem> foodItems, boolean enableEditMode, GroceryHolderListener listener) {
        Log.d(TAG, "ADAPTER: " + foodItems);
        this.listener = listener;
        this.type = type;

        if (foodItems != null) {
            this.foodList = foodItems;
        }
        this.context = context;
        this.enableEditMode = enableEditMode;
    }

    @Override
    public GroceryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return new GroceryHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(GroceryHolder holder, int position) {
        holder.bind(foodList.get(position));
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + foodList.size());
        return foodList.size();
    }

    public interface GroceryHolderListener {
        void setGroceryList(List<FoodItem> foodItems);

        void openDetailScreen(Object foodItem);
    }

    public class GroceryHolder extends RecyclerView.ViewHolder implements CommonDialogFragment.DialogInterface {

        private TextView tvAlreadyInFridge;
        private CheckBox cbItemCheck;
        private TextView tvExpiryDate;
        private LinearLayout llExpiryDate;
        private LinearLayout llFoodItem;
//        private ImageView ivItemImage;
        private LinearLayout llAddSubtract;
        private TextView tvFoodCountCrack;
        private ImageView ivSubtractItem;
        private ImageView ivAddItem;
        private Object foodItem;
        private TextView tvItemName;
        private TextView tvItemAmount;
        private TextView tvPurchaseDate;

        private GroceryHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.food_item, parent, false));

            llFoodItem = itemView.findViewById(R.id.ll_food_item);
            tvItemName = itemView.findViewById(R.id.foodItemName);
//            ivItemImage = itemView.findViewById(R.id.iv_item_image);
            tvItemAmount = itemView.findViewById(R.id.foodAmt);
            ivSubtractItem = itemView.findViewById(R.id.subtract_item);
            ivAddItem = itemView.findViewById(R.id.add_item);
            llAddSubtract = itemView.findViewById(R.id.llAddSubtract);
            tvFoodCountCrack = itemView.findViewById(R.id.food_count_track);
            llExpiryDate = itemView.findViewById(R.id.llExpiryDate);
            tvExpiryDate = itemView.findViewById(R.id.tvExpiryDate);
            tvPurchaseDate = itemView.findViewById(R.id.tvPurchaseDate);
            cbItemCheck = itemView.findViewById(R.id.cbItemCheck);
            tvAlreadyInFridge = itemView.findViewById(R.id.tvAlreadyInFridge);

            llFoodItem.setOnClickListener(view -> {
                listener.openDetailScreen(foodItem);
            });
        }

        public void bind(FoodItem foodItem) {

            if (type.equals(context.getString(R.string.recommend_me))) {
                cbItemCheck.setVisibility(View.VISIBLE);
                if (foodItem.isChecked()) {
                    cbItemCheck.setChecked(true);
                    llAddSubtract.setVisibility(View.VISIBLE);
                    tvAlreadyInFridge.setVisibility(View.GONE);
                } else {
                    tvAlreadyInFridge.setVisibility(View.VISIBLE);
                    cbItemCheck.setChecked(false);
                    llAddSubtract.setVisibility(View.GONE);
                }
            } else {
//                ivItemImage.setVisibility(View.VISIBLE);
                if (enableEditMode) {
                    llAddSubtract.setVisibility(View.VISIBLE);
                }
            }

            cbItemCheck.setOnCheckedChangeListener((compoundButton, b) -> {
                foodItem.setChecked(b);
                if (b) {
                    llAddSubtract.setVisibility(View.VISIBLE);
                } else {
                    llAddSubtract.setVisibility(View.GONE);
                }
                listener.setGroceryList(foodList);
            });

            if (!type.equals(context.getString(R.string.fridge_title))) {
                llExpiryDate.setVisibility(View.GONE);
            } else {
                tvExpiryDate.setText("Expiry date: " + foodItem.getExpected_expiry_date());
                tvPurchaseDate.setText("Purchase date: " + foodItem.getPurchase_date());
            }
            this.foodItem = foodItem;
            tvItemName.setText(foodItem.getItemName());
            tvItemAmount.setText("Amount: " + foodItem.getQuantity());
            tvFoodCountCrack.setText(Integer.toString(foodItem.getQuantity()));

            ivAddItem.setOnClickListener(view -> {
                int qty = foodItem.getQuantity();
                qty++;
                foodItem.setQuantity(qty);
                tvItemAmount.setText("Amount: " + foodItem.getQuantity());
                tvFoodCountCrack.setText(Integer.toString(foodItem.getQuantity()));
                listener.setGroceryList(foodList);
            });

//            ExecutorService executor = Executors.newSingleThreadExecutor();
//            Handler handler = new Handler();
//            executor.execute(() -> {
//                try {
//                    String imageURL = context.getString(R.string.image_url, foodItem.getItemName().toLowerCase());
//                    InputStream inputStream = new URL(imageURL).openStream();
//                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//
//
//                    // Only for making changes in UI
//                    handler.post(() -> ivItemImage.setImageBitmap(bitmap));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });

            ivSubtractItem.setOnClickListener(view -> {
                int qty = foodItem.getQuantity();
                qty--;
                if (qty < 1) {
                    //show alert and remove the item from list
                    CommonDialogFragment dialogFragment =
                            new CommonDialogFragment(
                                    this,
                                    context.getResources().getString(R.string.grocery_delete_item_title),
                                    context.getResources().getString(R.string.grocery_delete_item_message),
                                    context.getResources().getString(R.string.yes),
                                    context.getResources().getString(R.string.no));
                    if (context instanceof DishRecipeActivity)
                        dialogFragment.show(((DishRecipeActivity) context).getSupportFragmentManager(), "");
                    else
                        dialogFragment.show(((NavActivity) context).getSupportFragmentManager(), "");

                } else {
                    foodItem.setQuantity(qty);
                    tvItemAmount.setText("Amount: " + foodItem.getQuantity());
                    tvFoodCountCrack.setText(Integer.toString(foodItem.getQuantity()));
                }
                listener.setGroceryList(foodList);
            });
        }

        @Override
        public void onSelectedPosBtn() {
            //remove item
            foodList.remove(foodItem);
            if (foodList.isEmpty()) {
                listener.setGroceryList(foodList);
            }
            notifyDataSetChanged();
        }

        @Override
        public void onSelectedNegBtn() {
            //do nothing
        }
    }
}
