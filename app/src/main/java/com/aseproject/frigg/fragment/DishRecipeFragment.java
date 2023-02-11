package com.aseproject.frigg.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aseproject.frigg.R;
import com.aseproject.frigg.activity.DishRecipeActivity;
import com.aseproject.frigg.activity.NavActivity;
import com.aseproject.frigg.adapter.FoodAdapter;
import com.aseproject.frigg.common.AppSessionManager;
import com.aseproject.frigg.common.CommonDialogFragment;
import com.aseproject.frigg.common.FriggRecyclerView;
import com.aseproject.frigg.model.FoodItem;
import com.aseproject.frigg.model.Ingredient;
import com.aseproject.frigg.service.FoodService;
import com.aseproject.frigg.service.RecommendService;
import com.aseproject.frigg.service.SessionFacade;
import com.aseproject.frigg.util.Constants;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DishRecipeFragment extends Fragment implements RecommendService.RecommendListener, FoodAdapter.GroceryHolderListener, CommonDialogFragment.DialogInterface,
        RecommendService.RecommendPostListener {
    private LinearLayout btnSaveEditedItems;
    private TextView btnText;
    private SessionFacade sessionFacade;
    private LinearLayout mEmptyView;
    private FriggRecyclerView groceriesRecyclerView;
    private static final String PURPOSE_INGREDIENTS = "PURPOSE_INGREDIENTS";
    private Context context;
    private List<FoodItem> foodItems;
    private static final String SET_GROCERIES_PURPOSE = "SET_GROCERIES_PURPOSE";
    private FoodAdapter foodAdapter;
    private TextView dish_name;
    private TextView tvRecipeDirections;
    private String dishName;
    private String recipe = "";


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dish_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((DishRecipeActivity) context).setTitle("Recipe");

        dishName = ((DishRecipeActivity) context).getIntent().getStringExtra("DISH_NAME");
        mEmptyView = view.findViewById(R.id.groceries_empty_view);
        sessionFacade = new SessionFacade();
        groceriesRecyclerView = view.findViewById(R.id.groceriesRecyclerView);
        btnSaveEditedItems = view.findViewById(R.id.btnSaveEditedItems);
        dish_name = view.findViewById(R.id.dish_name);
        tvRecipeDirections = view.findViewById(R.id.tvRecipeDirections);
        btnText = btnSaveEditedItems.findViewById(R.id.btn_text);
        btnText.setText("Add to Grocery");

        prepareView();
        searchIngredients(dishName);
        setRecyclerView();
        setClickListeners();
    }

    private void prepareView() {
        dish_name.setText(context.getString(R.string.ingredients_text, dishName));
    }

    @Override
    public void setGroceryList(List<FoodItem> foodItems) {
        this.foodItems = foodItems;
        if (foodItems.isEmpty()) {
            setItems();
        }
    }

    @Override
    public void openDetailScreen(Object foodItem) {
        //do nothing
    }

    private void setRecyclerView() {
        groceriesRecyclerView.setHasFixedSize(true);
        groceriesRecyclerView.setItemAnimator(null);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        groceriesRecyclerView.setLayoutManager(layoutManager);
        groceriesRecyclerView.setEmptyView(mEmptyView);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, layoutManager.getOrientation());
        groceriesRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void updateUI(List<FoodItem> foodItems, boolean enableEditMode) {
        foodAdapter = new FoodAdapter((DishRecipeActivity) context, context.getString(R.string.recommend_me), foodItems, enableEditMode, this);
        groceriesRecyclerView.setAdapter(foodAdapter);

        groceriesRecyclerView.getRecycledViewPool().clear();
        foodAdapter.notifyDataSetChanged();
    }


    private void searchIngredients(String itemName) {
        ((DishRecipeActivity) context).showActivityIndicator(context.getString(R.string.fetching_data));
        String url = Constants.BASE_URL + "GenerateGroceryList/" + itemName + "/"+AppSessionManager.getInstance().getFridgeId();
        sessionFacade.searchIngredients(context, PURPOSE_INGREDIENTS, this, url);
    }

    @Override
    public <T> void notifyFetchSuccess(T obj, String purpose) {
        ((DishRecipeActivity) context).hideActivityIndicator();

        if (obj instanceof Ingredient) {
            List<FoodItem> foodItems = new ArrayList<>();
            for (String item : ((Ingredient) obj).getFridge()) {
                FoodItem foodItem = new FoodItem();
                foodItem.setFood_item_name(item);
                foodItem.setChecked(false);
                foodItems.add(foodItem);
            }
            for (String item : ((Ingredient) obj).getGrocery()) {
                FoodItem foodItem = new FoodItem();
                foodItem.setFood_item_name(item);
                foodItem.setChecked(true);
                foodItems.add(foodItem);
            }
            this.foodItems = foodItems;

            List<String> directions = new ArrayList<>();
            for (String d : ((Ingredient) obj).getDirections().split(Pattern.quote(". "))) {
                d = d + ".\n";
                directions.add(d);
            }

            for (String d : directions) {
                recipe = recipe + d;
            }
            updateUI(foodItems, true);
        }
    }

    private void setClickListeners() {
        btnSaveEditedItems.setOnClickListener(view -> {
            setItems();
        });

        tvRecipeDirections.setOnClickListener(view -> {
            CommonDialogFragment dialogFragment = new CommonDialogFragment(
                    this,
                    "Recipe for " + dishName,
                    recipe,
                    context.getString(R.string.ok),
                    "");
            dialogFragment.show(((DishRecipeActivity) context).getSupportFragmentManager(), "");
        });
    }

    private void setItems() {
        ((DishRecipeActivity) context).showActivityIndicator(context.getString(R.string.saving_data));
        List<FoodItem> list = new ArrayList<>();
        for (FoodItem foodItem : foodItems) {
            if (foodItem.isChecked()) {
                list.add(foodItem);
            }
        }
        sessionFacade.setIngredients(context, SET_GROCERIES_PURPOSE, this, list);
    }


    @Override
    public void notifyPostSuccess(String response, String purpose) {
        ((DishRecipeActivity) context).hideActivityIndicator();
        ((DishRecipeActivity) context).onBackPressed();
        updateUI(AppSessionManager.getInstance().getGroceries(), false);
        btnSaveEditedItems.setVisibility(View.GONE);
    }

    @Override
    public void notifyPostError(String error, String purpose) {
        ((DishRecipeActivity) context).hideActivityIndicator();
        CommonDialogFragment dialogFragment = new CommonDialogFragment(
                this,
                context.getString(R.string.error_title),
                context.getString(R.string.save_items_error),
                context.getString(R.string.ok),
                "");
        dialogFragment.show(((DishRecipeActivity) context).getSupportFragmentManager(), "");
    }

    @Override
    public void notifyFetchError(String error, String purpose) {
        ((DishRecipeActivity) context).hideActivityIndicator();
    }

    @Override
    public void onSelectedPosBtn() {

    }

    @Override
    public void onSelectedNegBtn() {

    }
}