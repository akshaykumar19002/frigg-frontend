package com.aseproject.frigg.fragment;

import static androidx.core.content.ContextCompat.getSystemService;
import static com.aseproject.frigg.activity.VoiceRecognitionActivity.RecordAudioRequestCode;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aseproject.frigg.R;
import com.aseproject.frigg.activity.FoodDetailActivity;
import com.aseproject.frigg.activity.NavActivity;
import com.aseproject.frigg.activity.NewFoodItemActivity;
import com.aseproject.frigg.adapter.FoodAdapter;
import com.aseproject.frigg.backgroundService.ExpiryService;
import com.aseproject.frigg.backgroundService.ThresholdItemsService;
import com.aseproject.frigg.common.AppSessionManager;
import com.aseproject.frigg.common.CommonDialogFragment;
import com.aseproject.frigg.common.FriggRecyclerView;
import com.aseproject.frigg.model.FoodItem;
import com.aseproject.frigg.service.FoodService;
import com.aseproject.frigg.service.SessionFacade;
import com.aseproject.frigg.util.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class FoodFragment extends Fragment implements FoodService.FoodServiceGetListener, FoodService.FoodServicePostListener, FoodAdapter.GroceryHolderListener, CommonDialogFragment.DialogInterface {

    private static final String TAG = "GroceryFragment";
    private String type = "";
    private Context context;
    private SessionFacade sessionFacade;
    private FriggRecyclerView groceriesRecyclerView;
    private LinearLayout mEmptyView;
    private SwipeRefreshLayout groceriesRefreshLayout;
    private static final String GET_GROCERIES_PURPOSE = "GET_GROCERIES_PURPOSE";
    private static final String GET_FRIDGE_PURPOSE = "GET_FRIDGE_PURPOSE";
    private static final String SET_GROCERIES_PURPOSE = "SET_GROCERIES_PURPOSE";
    private static final String SET_FRIDGE_PURPOSE = "SET_FRIDGE_PURPOSE";
    private static String ADD_FOOD_ITEM = "ADD_FOOD_ITEM";
    private FoodAdapter foodAdapter;
    private ImageView ivEditItems;
    private LinearLayout btnSaveEditedItems;
    private ImageView ivAddItems;
    private LinearLayout llAddItem;
    private EditText etItemName;
    private ImageView btnVoiceToText;
    private SpeechRecognizer speechRecognizer;
    private List<FoodItem> groceries = new ArrayList<>();
    private List<FoodItem> fridgeItems = new ArrayList<>();

    public FoodFragment(String type) {
        this.type = type;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sessionFacade = new SessionFacade();
        startExpiryService();
        startThresholdService();
        return inflater.inflate(R.layout.fragment_grocery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((NavActivity) context).setTitle(type);

        // Find Views
        ivEditItems = view.findViewById(R.id.ivEditItems);
        ivAddItems = view.findViewById(R.id.ivAddItems);
        groceriesRecyclerView = view.findViewById(R.id.groceriesRecyclerView);
        groceriesRefreshLayout = view.findViewById(R.id.groceriesRefreshLayout);
        mEmptyView = view.findViewById(R.id.groceries_empty_view);
        llAddItem = view.findViewById(R.id.llAddItem);
        btnSaveEditedItems = view.findViewById(R.id.btnSaveEditedItems);
        btnSaveEditedItems.setVisibility(View.GONE);
        etItemName = view.findViewById(R.id.etItemName);
        btnVoiceToText = view.findViewById(R.id.btn_voice_to_text);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);

        // Pull To Refresh
        // Refresh items
        groceriesRefreshLayout.setOnRefreshListener(this::refreshItems);

        setRecyclerView();
        handleButtonActions();
    }

    private void handleButtonActions() {
        ivEditItems.setOnClickListener(view -> {
            if (isFridge()) {
                updateUI(fridgeItems, true);
            } else {
                updateUI(groceries, true);
            }
            btnSaveEditedItems.setVisibility(View.VISIBLE);
        });

        ivAddItems.setOnClickListener(view -> {
            Intent intent = new Intent(context, NewFoodItemActivity.class);
            intent.putExtra("LIST_TYPE", type);
            startActivity(intent);
        });

        btnSaveEditedItems.setOnClickListener(view -> {
            setItems();
        });
    }

    private void setItems() {
        if (isFridge()) {
            ((NavActivity) context).showActivityIndicator(context.getString(R.string.saving_data));
            AppSessionManager.getInstance().setFridgeItems(fridgeItems);
            sessionFacade.setFridgeList(context, SET_FRIDGE_PURPOSE, this, fridgeItems);
        } else {
            ((NavActivity) context).showActivityIndicator(context.getString(R.string.saving_data));
            sessionFacade.setGroceries(context, SET_GROCERIES_PURPOSE, this, groceries);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }

    // Swipe Refresh Layout
    private void refreshItems() {
        downloadItems(getString(R.string.fetching_data));
        onRefreshItemsLoadComplete();
        btnSaveEditedItems.setVisibility(View.GONE);
    }

    private void onRefreshItemsLoadComplete() {
        // Update the adapter and notify data set changed
        Log.d(TAG, "ApptFragment onRefreshItemsLoadComplete() {\n");
        // Stop refresh animation
        groceriesRefreshLayout.setRefreshing(false);
    }

    public void downloadItems(String indicatorStr) {
        ((NavActivity) context).showActivityIndicator(indicatorStr);

        if (isFridge()) {
            sessionFacade.getFridgeList(context, GET_FRIDGE_PURPOSE, this);
        } else {
            sessionFacade.getGroceries(context, GET_GROCERIES_PURPOSE, this);
        }
    }

    private boolean isFridge() {
        if (type.equals(context.getString(R.string.fridge_title))) {
            return true;
        } else {
            return false;
        }
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

        if (isFridge()) {
            test();
        }
    }

    private void updateUI(List<FoodItem> foodItems, boolean enableEditMode) {
        if (isFridge()) {
            fridgeItems = foodItems;
            foodAdapter = new FoodAdapter((NavActivity) context, type, fridgeItems, enableEditMode, this);
        } else {
            groceries = foodItems;
            foodAdapter = new FoodAdapter((NavActivity) context, type, groceries, enableEditMode, this);
        }

        groceriesRecyclerView.setAdapter(foodAdapter);

        groceriesRecyclerView.getRecycledViewPool().clear();
        foodAdapter.notifyDataSetChanged();

        // If the page is refreshing, we want to stay at the top of the view/
        // Otherwise, we want to scroll to the newest message.
        if (groceriesRefreshLayout.isRefreshing()) {
            onRefreshItemsLoadComplete();
        } else {
            groceriesRecyclerView.scrollToPosition(0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        downloadItems(context.getString(R.string.fetching_data));
    }

    @Override
    public void notifyFetchSuccess(List<FoodItem> foodItems, String purpose) {
        updateUI(foodItems, false);
        ((NavActivity) context).hideActivityIndicator();
    }

    @Override
    public void notifyFetchError(String error, String purpose) {
        ((NavActivity) context).hideActivityIndicator();
    }

    @Override
    public void notifyPostSuccess(String response, String purpose) {
        ((NavActivity) context).hideActivityIndicator();
        if (purpose.equals(SET_FRIDGE_PURPOSE)) {
            updateUI(fridgeItems, false);
        } else if(purpose.equals(SET_GROCERIES_PURPOSE)){
            updateUI(groceries, false);
        } else {
            Toast.makeText(context, "Added to Grocery List", Toast.LENGTH_LONG).show();
        }
        btnSaveEditedItems.setVisibility(View.GONE);
    }

    @Override
    public void notifyPostError(String error, String purpose) {
        ((NavActivity) context).hideActivityIndicator();
        CommonDialogFragment dialogFragment = new CommonDialogFragment(
                this,
                context.getString(R.string.error_title),
                context.getString(R.string.save_items_error),
                context.getString(R.string.ok),
                "");
        dialogFragment.show(((NavActivity) context).getSupportFragmentManager(), "");
    }

    @Override
    public void setGroceryList(List<FoodItem> foodItems) {
        if (!isFridge())
            this.groceries = (List<FoodItem>) foodItems;
        else
            this.fridgeItems = (List<FoodItem>) foodItems;
        if (foodItems.isEmpty()) {
            setItems();
        }
    }

    @Override
    public void openDetailScreen(Object foodItem) {
        Intent intent = new Intent(context, FoodDetailActivity.class);
        intent.putExtra("FOOD_ITEM", (FoodItem) foodItem);
        intent.putExtra("TYPE", type);

        context.startActivity(intent);
    }

    @Override
    public void onSelectedPosBtn() {
        //do nothing
    }

    @Override
    public void onSelectedNegBtn() {
        //do nothing
    }

    private void startExpiryService() {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ExpiryService.class.getName().equals(service.service.getClassName())) {
                // running
                Log.d(TAG, "expiry service already running");
            }
        }
        // not running
        Log.d(TAG, "expiry service not running... starting the service");
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(context, ExpiryService.class);
        PendingIntent pintent = PendingIntent
                .getService(context, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                );
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // Start service daily
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                3*60*1000, pintent);
    }

    private void startThresholdService() {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ExpiryService.class.getName().equals(service.service.getClassName())) {
                // running
                Log.d(TAG, "threshold service already running");
            }
        }
        // not running
        Log.d(TAG, "thresold service not running... starting the service");
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(context, ThresholdItemsService.class);
        PendingIntent pintent = PendingIntent
                .getService(context, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                );

        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // Start service daily
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                3*60*1000, pintent);
    }


    private void test() {
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                switch (direction) {
                    case ItemTouchHelper.RIGHT:
                        FoodItem foodItem = new FoodItem();
                        ((NavActivity) context).showActivityIndicator(context.getString(R.string.saving_data));
                        foodItem.setFood_item_name(fridgeItems.get(pos).getItemName());
                        foodItem.setQuantity(fridgeItems.get(pos).getQuantity());
                        String url = Constants.BASE_URL + "GroceryList/AddFoodItemByName/" + AppSessionManager.getInstance().getFridgeId();
                        sessionFacade.addItem(context, foodItem, url, ADD_FOOD_ITEM, FoodFragment.this);
                        groceriesRecyclerView.getAdapter().notifyDataSetChanged();
                        break;
                    case ItemTouchHelper.LEFT:
                        fridgeItems.remove(pos);
                        groceriesRecyclerView.getAdapter().notifyDataSetChanged();
                        setItems();
                        //add to grocery
                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftActionIcon(R.drawable.delete)
                        .setSwipeLeftActionIconTint(R.color.black)
                        .addSwipeLeftLabel("Delete item")
                        .setSwipeLeftLabelColor(getResources().getColor(R.color.black))
                        .addSwipeLeftBackgroundColor(getResources().getColor(R.color.red))


                        .addSwipeRightBackgroundColor(getResources().getColor(R.color.gray))
                        .addSwipeRightActionIcon(R.drawable.food_icon)
                        .setSwipeRightActionIconTint(R.color.black)
                        .addSwipeRightLabel("Add to Grocery")
                        .setSwipeRightLabelColor(getResources().getColor(R.color.black))
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        );
        helper.attachToRecyclerView(groceriesRecyclerView);

    }
}