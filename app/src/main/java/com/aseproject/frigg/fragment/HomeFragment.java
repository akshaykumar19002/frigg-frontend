package com.aseproject.frigg.fragment;

import static com.aseproject.frigg.activity.VoiceRecognitionActivity.RecordAudioRequestCode;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aseproject.frigg.R;
import com.aseproject.frigg.activity.CameraActivity;
import com.aseproject.frigg.activity.NavActivity;
import com.aseproject.frigg.activity.NavActivity;
import com.aseproject.frigg.common.AppSessionManager;
import com.aseproject.frigg.common.CommonDialogFragment;
import com.aseproject.frigg.model.FoodItem;
import com.aseproject.frigg.service.FoodService;
import com.aseproject.frigg.service.SessionFacade;
import com.aseproject.frigg.util.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements FoodService.FoodServicePostListener, CommonDialogFragment.DialogInterface {
    private ImageView ivMicBtn;
    private Context context;
    private SpeechRecognizer speechRecognizer;
    private AppCompatEditText etFoodItem;
    private AppCompatEditText etFoodQuantity;
    private CheckBox cbFridgeList;
    private CheckBox cbGroceryList;
    private LinearLayout saveNewItem;
    private SessionFacade sessionFacade;
    private static String ADD_FOOD_ITEM = "ADD_FOOD_ITEM";
    private Button btnAddItem;
    private Button btn_scan;
    private LinearLayout llAddItem;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((NavActivity) context).setTitle("Home");


        sessionFacade = new SessionFacade();
        ivMicBtn = view.findViewById(R.id.ivMicBtn);
        etFoodItem = view.findViewById(R.id.etFoodItem);
        etFoodQuantity = view.findViewById(R.id.etFoodQuantity);
        cbFridgeList = view.findViewById(R.id.cbFridgeList);
        cbGroceryList = view.findViewById(R.id.cbGroceryList);
        saveNewItem = view.findViewById(R.id.saveNewItem);
        btn_scan = view.findViewById(R.id.btn_scan);
        btnAddItem = view.findViewById(R.id.btnAddItem);
        llAddItem = view.findViewById(R.id.llAddItem);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);

        ivMicBtn.setOnClickListener(view1 -> {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermission();
            } else {
                setMicToText();
            }
        });

        btnAddItem.setOnClickListener(view1 -> {
            if(llAddItem.getVisibility() == View.VISIBLE)
                llAddItem.setVisibility(View.GONE);
            else
                llAddItem.setVisibility(View.VISIBLE);
        });

        btn_scan.setOnClickListener(view12 -> {
            Intent intent = new Intent(context, CameraActivity.class);
            startActivity(intent);
        });

        saveNewItem.setOnClickListener(view1 -> {
            if (checkIfFieldsAreEmpty()) {
                Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_LONG).show();
                return;
            }
            FoodItem foodItem = new FoodItem();
            ((NavActivity) context).showActivityIndicator(context.getString(R.string.saving_data));
            foodItem.setFood_item_name(etFoodItem.getText().toString());
            foodItem.setQuantity(Integer.parseInt(etFoodQuantity.getText().toString()));
            String url = "";

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy");
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.DATE, 7);

            if (cbFridgeList.isChecked()) {
                foodItem.setExpected_expiry_date(sdf.format(c.getTime()));
                foodItem.setPurchase_date(foodItem.getPurchase_date());
                url = Constants.BASE_URL + "FridgeList/AddFoodItemByName/"+ AppSessionManager.getInstance().getFridgeId();
                sessionFacade.addItem(context, foodItem, url, ADD_FOOD_ITEM, this);
            }
            if (cbGroceryList.isChecked()) {
                url = Constants.BASE_URL + "GroceryList/AddFoodItemByName/"+ AppSessionManager.getInstance().getFridgeId();
                sessionFacade.addItem(context, foodItem, url, ADD_FOOD_ITEM, this);
            }
        });
    }

    private boolean checkIfFieldsAreEmpty() {
        String itemName = etFoodItem.getText().toString();
        String quantity = etFoodQuantity.getText().toString();
        boolean type = cbFridgeList.isChecked() || cbGroceryList.isChecked();
        if (itemName.isEmpty() || quantity.isEmpty() || !type)
            return true;
        return false;
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
                Toast.makeText(context, spokenData, Toast.LENGTH_SHORT).show();
                List<String> spokenDataList = new ArrayList<>(Arrays.asList(spokenData.split(" ")));
                try{
                    Integer.parseInt(spokenDataList.get(1));
                } catch (NumberFormatException e) {
                    spokenDataList.set(1, String.valueOf(convertWordToInteger(spokenDataList.get(1))));
                }
                List<String> formatList = new ArrayList<>(Arrays.asList(context.getString(R.string.add_item_format).split(" ")));
                if (spokenDataList.size() == 6) {
                    if (!formatList.get(0).equalsIgnoreCase(spokenDataList.get(0)) ||
                            !formatList.get(3).equalsIgnoreCase(spokenDataList.get(3))) {
                        Toast.makeText(context, "Please follow the correct format like mentioned...", Toast.LENGTH_LONG).show();
                    } else {
                        etFoodQuantity.setText(spokenDataList.get(1));
                        etFoodItem.setText(spokenDataList.get(2));
                        String list = spokenDataList.get(4) + " " + spokenDataList.get(5);
                        if (list.equalsIgnoreCase("Grocery List")) {
                            cbGroceryList.setChecked(true);
                        } else if (list.equalsIgnoreCase("Fridge List")) {
                            cbFridgeList.setChecked(true);
                        }
                    }
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
            ActivityCompat.requestPermissions((NavActivity) context, new String[]{Manifest.permission.RECORD_AUDIO}, RecordAudioRequestCode);
        } else {
            Toast.makeText(context, "Your android version does not support recording. Please add an item manually.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSelectedPosBtn() {

    }

    @Override
    public void onSelectedNegBtn() {

    }

    @Override
    public void notifyPostSuccess(String response, String purpose) {
        ((NavActivity) context).hideActivityIndicator();
        String message = "";
        if(cbGroceryList.isChecked() && cbFridgeList.isChecked()) {
            message = context.getString(R.string.item_added_msg, "Fridge List and Grocery List");
        } else if(cbFridgeList.isChecked()) {
            message = context.getString(R.string.item_added_msg, "Fridge List");
        } else {
            message = context.getString(R.string.item_added_msg, "Grocery List");
        }
        CommonDialogFragment dialogFragment = new CommonDialogFragment(
                this,
                context.getString(R.string.item_added),
                message,
                context.getString(R.string.ok),
                "");
        dialogFragment.show(((NavActivity) context).getSupportFragmentManager(), "");

    }

    @Override
    public void notifyPostError(String error, String purpose) {
        ((NavActivity) context).hideActivityIndicator();

        CommonDialogFragment dialogFragment = new CommonDialogFragment(
                this,
                context.getString(R.string.error_title),
                context.getString(R.string.add_items_error),
                context.getString(R.string.ok),
                "");
        dialogFragment.show(((NavActivity) context).getSupportFragmentManager(), "");

    }

    private int convertWordToInteger(String number) {
        boolean isValidInput = true;
        long result = 0;
        long finalResult = 0;
        List<String> allowedStrings = Arrays.asList
                (
                        "zero","one","two","three","four","five","six","seven",
                        "eight","nine","ten","eleven","twelve","thirteen","fourteen",
                        "fifteen","sixteen","seventeen","eighteen","nineteen","twenty",
                        "thirty","forty","fifty","sixty","seventy","eighty","ninety",
                        "hundred","thousand","million","billion","trillion"
                );

        if(number != null && number.length()> 0)
        {
            number = number.replaceAll("-", " ");
            number = number.toLowerCase().replaceAll(" and", " ");
            String[] splittedParts = number.trim().split("\\s+");

            for(String str : splittedParts)
            {
                if(!allowedStrings.contains(str))
                {
                    isValidInput = false;
                    System.out.println("Invalid word found : "+str);
                    break;
                }
            }
            if(isValidInput)
            {
                for(String str : splittedParts)
                {
                    if(str.equalsIgnoreCase("zero")) {
                        result += 0;
                    }
                    else if(str.equalsIgnoreCase("one")) {
                        result += 1;
                    }
                    else if(str.equalsIgnoreCase("two")) {
                        result += 2;
                    }
                    else if(str.equalsIgnoreCase("three")) {
                        result += 3;
                    }
                    else if(str.equalsIgnoreCase("four")) {
                        result += 4;
                    }
                    else if(str.equalsIgnoreCase("five")) {
                        result += 5;
                    }
                    else if(str.equalsIgnoreCase("six")) {
                        result += 6;
                    }
                    else if(str.equalsIgnoreCase("seven")) {
                        result += 7;
                    }
                    else if(str.equalsIgnoreCase("eight")) {
                        result += 8;
                    }
                    else if(str.equalsIgnoreCase("nine")) {
                        result += 9;
                    }
                    else if(str.equalsIgnoreCase("ten")) {
                        result += 10;
                    }
                    else if(str.equalsIgnoreCase("eleven")) {
                        result += 11;
                    }
                    else if(str.equalsIgnoreCase("twelve")) {
                        result += 12;
                    }
                    else if(str.equalsIgnoreCase("thirteen")) {
                        result += 13;
                    }
                    else if(str.equalsIgnoreCase("fourteen")) {
                        result += 14;
                    }
                    else if(str.equalsIgnoreCase("fifteen")) {
                        result += 15;
                    }
                    else if(str.equalsIgnoreCase("sixteen")) {
                        result += 16;
                    }
                    else if(str.equalsIgnoreCase("seventeen")) {
                        result += 17;
                    }
                    else if(str.equalsIgnoreCase("eighteen")) {
                        result += 18;
                    }
                    else if(str.equalsIgnoreCase("nineteen")) {
                        result += 19;
                    }
                    else if(str.equalsIgnoreCase("twenty")) {
                        result += 20;
                    }
                    else if(str.equalsIgnoreCase("thirty")) {
                        result += 30;
                    }
                    else if(str.equalsIgnoreCase("forty")) {
                        result += 40;
                    }
                    else if(str.equalsIgnoreCase("fifty")) {
                        result += 50;
                    }
                    else if(str.equalsIgnoreCase("sixty")) {
                        result += 60;
                    }
                    else if(str.equalsIgnoreCase("seventy")) {
                        result += 70;
                    }
                    else if(str.equalsIgnoreCase("eighty")) {
                        result += 80;
                    }
                    else if(str.equalsIgnoreCase("ninety")) {
                        result += 90;
                    }
                    else if(str.equalsIgnoreCase("hundred")) {
                        result *= 100;
                    }
                    else if(str.equalsIgnoreCase("thousand")) {
                        result *= 1000;
                        finalResult += result;
                        result=0;
                    }
                    else if(str.equalsIgnoreCase("million")) {
                        result *= 1000000;
                        finalResult += result;
                        result=0;
                    }
                    else if(str.equalsIgnoreCase("billion")) {
                        result *= 1000000000;
                        finalResult += result;
                        result=0;
                    }
                    else if(str.equalsIgnoreCase("trillion")) {
                        result *= 1000000000000L;
                        finalResult += result;
                        result=0;
                    }
                }

                finalResult += result;
                result=0;
            }
        }
        return (int) finalResult;
    }
}