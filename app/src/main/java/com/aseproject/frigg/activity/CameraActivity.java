package com.aseproject.frigg.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aseproject.frigg.R;
import com.aseproject.frigg.model.FoodItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CameraActivity extends FriggActivity {

    String clientId, clientSecret, username, apiKey, fileName;
    String url = "http://107.23.188.190:3000/ScanningReceipts/";
    String url2 = "https://api.veryfi.com/api/v8/partner/documents/";
    RequestQueue requestQueue;

    private int GALLERY_RESULT_CODE = 1007;
    private int CAMERA_RESULT_CODE = 1008;

    Button camera, gallery;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        setTitle("Scan the Receipt");

        camera = findViewById(R.id.camera_button);
        gallery = findViewById(R.id.gallery_button);

        clientId = "vrfSu30dOjCFIDtGVwaAxodZFnbZshJd3PVabZ1";
        clientSecret = "PRkTmuvKsJN7ukG5auc2T5LY407zLikY8LzeevtYZZPehdnwwFpLbUk332yCfFumclA30Epy3JbVAb1dn36udDaarfg8wg7gAsDZu1UfclmotZhy96Oxym2l2dxXjUYS";
        username = "akshay.gulia25";
        apiKey = "8fd0f91d7ba1a4c75039096c1f57cd0f";
        requestQueue = Volley.newRequestQueue(this);

        imageView = findViewById(R.id.imageView);
        camera.setOnClickListener((view) -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_RESULT_CODE);
        });
        gallery.setOnClickListener((view) -> {
            Intent intentGalley = new Intent(Intent.ACTION_PICK);
            intentGalley.setType("image/*");
            startActivityForResult(intentGalley, GALLERY_RESULT_CODE);
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_RESULT_CODE && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            final String imageString = convertBitmapToString(bitmap);
            try {
                uploadImage(imageString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (requestCode == GALLERY_RESULT_CODE && resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                imageView.setImageBitmap(bitmap);
                uploadImage(convertBitmapToString(bitmap));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(String imageString) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("file_name", "test.png");
        object.put("file_data", "image/png;base64," + imageString);
        StringRequest request = new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(String response) {
                Log.d("response", response);
                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray lineItems = json.getJSONArray("line_items");
                    Log.d("res", lineItems.toString());
                    Map<String, Integer> items = new HashMap<>();
                    for (int j = 0; j < lineItems.length(); j++) {
                        JSONObject item = lineItems.getJSONObject(j);
                        items.put(item.getString("description"), items.getOrDefault(item.getString("description"), 0) + 1);
                    }
                    List<FoodItem> foodItems = new ArrayList<>();
                    for(Map.Entry<String, Integer> item: items.entrySet()) {
                        FoodItem foodItem = new FoodItem();
                        foodItem.setFood_item_name(item.getKey());
                        foodItem.setQuantity(item.getValue());
                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy");
                        Calendar c = Calendar.getInstance();
                        c.setTime(new Date());
                        c.add(Calendar.DATE, 7);
                        foodItem.setExpected_expiry_date(sdf.format(c.getTime()));
                        foodItem.setPurchase_date(sdf.format(new Date()));
                        foodItems.add(foodItem);
                    }
                    Toast.makeText(getApplicationContext(), ""+items, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(CameraActivity.this, FridgeToGroceryActivity.class);
                    intent.putExtra("foodList", (Serializable) foodItems);
                    intent.putExtra("foodListType", "FridgeList");
                    PendingIntent contentIntent = PendingIntent.getActivity(CameraActivity.this, 0, intent,
                            PendingIntent.FLAG_IMMUTABLE);
                    startActivity(intent);
                    Log.d("items", items.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("failed", "failed");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("CLIENT-ID", clientId);
                headers.put("AUTHORIZATION", "apikey " + username + ":" + apiKey);
                headers.put("Accept", "application/json");
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public byte[] getBody() {
                return object == null ? null : object.toString().getBytes(StandardCharsets.UTF_8);
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private String convertBitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}
