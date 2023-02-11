package com.aseproject.frigg.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aseproject.frigg.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.RecommendHolder> {

    private final Context context;
    private final RecommendListener listener;
    private String[] dishes1;
    private String[] dishes;
    private String searchText = "";

    public RecommendAdapter(Context context, String[] dishes, String[] dishes1, RecommendListener listener) {
        this.context = context;
        if (dishes.length != 0) {
            this.dishes = dishes;
            this.dishes1 = dishes;
        }
        this.listener = listener;
    }

    public String[] getDishes() {
        return dishes;
    }

    @NonNull
    @Override
    public RecommendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return new RecommendHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendHolder holder, int position) {
        holder.bind(dishes[position]);
    }

    public void filterItems(String s) {
        this.searchText = s;
        s = s.toLowerCase();
        List<String> filterDishes = new ArrayList<>();
        for (String dish : dishes1) {
            if (dish.toLowerCase().contains(s)) {
                filterDishes.add(dish);
            }
        }
        this.dishes = filterDishes.toArray(new String[0]);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dishes.length;
    }

    public interface RecommendListener {
        void onClickListener(String dishName);
    }

    public class RecommendHolder extends RecyclerView.ViewHolder {

        private final TextView foodItemName;
        private final LinearLayout llDishItem;

        public RecommendHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.recommend_food_item, parent, false));
            foodItemName = itemView.findViewById(R.id.foodItemName);
            llDishItem = itemView.findViewById(R.id.ll_dish_item);
        }

        public void bind(String itemName) {
            foodItemName.setText(itemName);
            llDishItem.setOnClickListener(view -> {
                listener.onClickListener(itemName);
            });
        }
    }

}
