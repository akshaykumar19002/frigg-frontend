package com.aseproject.frigg.fragment;

import static com.aseproject.frigg.activity.VoiceRecognitionActivity.RecordAudioRequestCode;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aseproject.frigg.R;
import com.aseproject.frigg.activity.NavActivity;
import com.aseproject.frigg.activity.NewFoodItemActivity;
import com.aseproject.frigg.common.AppSessionManager;
import com.aseproject.frigg.common.CommonDialogFragment;
import com.aseproject.frigg.model.FoodItem;
import com.aseproject.frigg.service.FoodService;
import com.aseproject.frigg.service.SessionFacade;
import com.aseproject.frigg.util.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewFoodItemFragment extends Fragment implements FoodService.FoodServicePostListener, CommonDialogFragment.DialogInterface {

    private String type = "";
    private ImageView ivMicBtn;
    private SpeechRecognizer speechRecognizer;
    private Context context;
    private TextView tvAddItemFormat;
    private AppCompatEditText etFoodItem;
    private AppCompatEditText etFoodQuantity;
    private LinearLayout llFridgeItem;
    private AppCompatEditText etFoodPurchase;
    private AppCompatEditText etFoodExpiry;
    Calendar purchaseCalendar = Calendar.getInstance();
    Calendar expiryCalendar = Calendar.getInstance();
    private Button saveNewItem;
    private SessionFacade sessionFacade;
    private static String ADD_FOOD_ITEM = "ADD_FOOD_ITEM";
    private Button cancelNewItem;

    public NewFoodItemFragment(String type) {
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
        return inflater.inflate(R.layout.fragment_new_food_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((NewFoodItemActivity) context).setTitle("Add Food Item");
        sessionFacade = new SessionFacade();

        ivMicBtn = view.findViewById(R.id.ivMicBtn);
        tvAddItemFormat = view.findViewById(R.id.tvAddItemFormat);
        etFoodItem = view.findViewById(R.id.etFoodItem);
        etFoodQuantity = view.findViewById(R.id.etFoodQuantity);
        llFridgeItem = view.findViewById(R.id.llFridgeItem);
        etFoodPurchase = view.findViewById(R.id.etFoodPurchase);
        etFoodExpiry = view.findViewById(R.id.etFoodExpiry);
        saveNewItem = view.findViewById(R.id.saveNewItem);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        cancelNewItem = view.findViewById(R.id.cancelNewItem);

        prepareView();
        setClickHandlers();
    }

    private boolean isFridge() {
        if (type.equals(context.getString(R.string.fridge_title))) {
            return true;
        } else {
            return false;
        }
    }

    private void prepareView() {
        if (isFridge()) {
            llFridgeItem.setVisibility(View.VISIBLE);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy");
        etFoodPurchase.setText(sdf.format(new Date()));
    }

    private void setClickHandlers() {
        ivMicBtn.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermission();
            } else {
                setMicToText();
            }
        });

        DatePickerDialog.OnDateSetListener expiryDate = (datePicker, year, month, day) -> {
            expiryCalendar.set(Calendar.YEAR, year);
            expiryCalendar.set(Calendar.MONTH, month);
            expiryCalendar.set(Calendar.DAY_OF_MONTH, day);
            updateExpiryLabel(expiryCalendar);
        };

        DatePickerDialog.OnDateSetListener purchaseDate = (datePicker, year, month, day) -> {
            purchaseCalendar.set(Calendar.YEAR, year);
            purchaseCalendar.set(Calendar.MONTH, month);
            purchaseCalendar.set(Calendar.DAY_OF_MONTH, day);
            updatePurchaseLabel(purchaseCalendar);
        };

        etFoodPurchase.setOnClickListener(view -> {
            //open calendar
            new DatePickerDialog(context, purchaseDate, purchaseCalendar.get(Calendar.YEAR), purchaseCalendar.get(Calendar.MONTH), purchaseCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        etFoodExpiry.setOnClickListener(view -> {
            //open calendar
            new DatePickerDialog(context, expiryDate, expiryCalendar.get(Calendar.YEAR), expiryCalendar.get(Calendar.MONTH), expiryCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });


        saveNewItem.setOnClickListener(view -> {
            if (checkIfFieldsAreEmpty()) {
                Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_LONG).show();
                return;
            }
            FoodItem foodItem = new FoodItem();
            ((NewFoodItemActivity) context).showActivityIndicator(context.getString(R.string.saving_data));
            foodItem.setFood_item_name(etFoodItem.getText().toString());
            foodItem.setQuantity(Integer.parseInt(etFoodQuantity.getText().toString()));
            String url = "";
            if (isFridge()) {
                foodItem.setExpected_expiry_date(etFoodExpiry.getText().toString());
                foodItem.setPurchase_date(etFoodPurchase.getText().toString());
                url = Constants.BASE_URL + "FridgeList/AddFoodItemByName/"+ AppSessionManager.getInstance().getFridgeId();
            } else {
                url = Constants.BASE_URL + "GroceryList/AddFoodItemByName/"+ AppSessionManager.getInstance().getFridgeId();
            }
            sessionFacade.addItem(context, foodItem, url, ADD_FOOD_ITEM, this);
        });

        cancelNewItem.setOnClickListener((v) -> {
//            RegisterFragment registerFragment = new RegisterFragment();
//            FragmentManager fragmentManager = getFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager
//                    .beginTransaction();
//            fragmentTransaction.replace(R.id.container, registerFragment).commit();

            Intent myIntent = new Intent(context, NavActivity.class);
            context.startActivity(myIntent);
        });
    }

    private boolean checkIfFieldsAreEmpty() {
        String foodItem = etFoodItem.getText().toString();
        String quantity = etFoodQuantity.getText().toString();
        if (isFridge()) {
            String purchaseDate = etFoodPurchase.getText().toString();
            String expiryDate = etFoodExpiry.getText().toString();

            if (foodItem == null || foodItem.isEmpty() || quantity == null || quantity.isEmpty() ||
                    purchaseDate == null || purchaseDate.isEmpty() || expiryDate == null || expiryDate.isEmpty())
                return true;
        } else {
            if (foodItem == null || foodItem.isEmpty() || quantity == null || quantity.isEmpty())
                return true;
        }
        return false;
    }

    private void updatePurchaseLabel(Calendar calendar) {
        String myFormat = "dd MMM, yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        etFoodPurchase.setText(dateFormat.format(calendar.getTime()));
    }

    private void updateExpiryLabel(Calendar calendar) {
        String myFormat = "dd MMM, yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        etFoodExpiry.setText(dateFormat.format(calendar.getTime()));
    }

    private void setMicToText() {
        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d("", "");
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {
                ivMicBtn.setImageResource(R.drawable.mic_off_icon);
                Toast.makeText(context, "Please hold the mic and then speak clearly!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(Bundle bundle) {
                ivMicBtn.setImageResource(R.drawable.mic_off_icon);
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String spokenData = data.get(0);

                List<String> spokenDataList = new ArrayList<>(Arrays.asList(spokenData.split(" ")));
                List<String> formatList = new ArrayList<>(Arrays.asList(context.getString(R.string.add_item_format).split(" ")));
                if (spokenDataList.size() == 3 || spokenDataList.size() == 4) {
                    if (!formatList.get(0).equalsIgnoreCase(spokenDataList.get(0))) {
                        Toast.makeText(context, "Please follow the correct format like mentioned...", Toast.LENGTH_LONG).show();
                    } else if (spokenDataList.size() == 4) {
                        etFoodQuantity.setText(spokenDataList.get(1) + " " + spokenDataList.get(2));
                    } else {
                        etFoodQuantity.setText(spokenDataList.get(1));
                    }
                    etFoodItem.setText(spokenDataList.get(spokenDataList.size() - 1));
                } else {
                    Toast.makeText(context, "Please follow the correct format like mentioned...", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        ivMicBtn.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                MediaPlayer song = MediaPlayer.create(context, R.raw.recording_end);
                song.start();
                speechRecognizer.stopListening();
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                MediaPlayer song = MediaPlayer.create(context, R.raw.recording_start);
                song.start();
                ivMicBtn.setImageResource(R.drawable.mic_on_icon);
                speechRecognizer.startListening(speechRecognizerIntent);
            }
            return false;
        });
    }


    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions((NewFoodItemActivity) context, new String[]{Manifest.permission.RECORD_AUDIO}, RecordAudioRequestCode);
        } else {
            Toast.makeText(context, "Your android version does not support recording. Please add an item manually.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void notifyPostSuccess(String response, String purpose) {
        ((NewFoodItemActivity) context).hideActivityIndicator();
        ((NewFoodItemActivity) context).onBackPressed();
    }

    @Override
    public void notifyPostError(String error, String purpose) {
        ((NewFoodItemActivity) context).hideActivityIndicator();

        CommonDialogFragment dialogFragment = new CommonDialogFragment(
                this,
                context.getString(R.string.error_title),
                context.getString(R.string.add_items_error),
                context.getString(R.string.ok),
                "");
        dialogFragment.show(((NewFoodItemActivity) context).getSupportFragmentManager(), "");

    }

    @Override
    public void onSelectedPosBtn() {

    }

    @Override
    public void onSelectedNegBtn() {

    }
}