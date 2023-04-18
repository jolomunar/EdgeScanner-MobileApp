package apc.mobprog.myqrcodeandbarcodescanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.app.ProgressDialog;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.content.Context;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.net.URL;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import javax.net.ssl.HttpsURLConnection;

public class DisplayActivity extends AppCompatActivity {

    private static final String TAG = "";
    Button scanAgain;
    Button sendData;
    ListView listView;
    EditText outlet, stockCode, unitPrice, tQuantity;
    Spinner brand, size, color;
    ArrayAdapter<String> arr;

    TextView textView2;
    TextView textView;

    FirebaseDatabase node;
    DatabaseReference ref;


   String esEndpoint = "https://edgescanner.herokuapp.com/api/ess-api/create";
//   String esEndpoint = "https://edgescanner.herokuapp.com/api/ess-api/create";
//   String esEndpoint = "http://localhost:8000/api/ess-api/create";
//   String esEndpoint = "https://eok6418nj8g0skh.m.pipedream.net";
//   String esEndpoint = "https://e7bf9b6b00c727d40a25426a9ec5c20e.m.pipedream.net";
//   String esEndpoint = "https://eotwyaq96coc31b.m.pipedream.net";
//   String esEndpoint = "https://edgescanner.myapc.edu.ph/api/ess-api/create";

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);


        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        node = FirebaseDatabase.getInstance();

        getFirstName();
        getItemInfo();

        textView2 = findViewById(R.id.textView2);
        textView = findViewById(R.id.textView);

        Intent intent = getIntent();
        GlobalBarcode.barcode = intent.getStringExtra("barcode_nr");
        listView = findViewById(R.id.sampleListView);
        arr = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,
                GlobalBarcode.arrayList);
        System.out.println(arr);
        listView.setAdapter(arr);
        Log.e(TAG, "55555  - RESULT : " + GlobalBarcode.barcode);
        GlobalBarcode.arrayList.add(GlobalBarcode.barcode);
        if (GlobalBarcode.arrayList.size() > 1) {
            GlobalBarcode.arrayList.remove(0);
        }
        Log.e(TAG, "55555  - RESULT : " + GlobalBarcode.arrayList.toString());
//        arr.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), GlobalBarcode.barcode, Toast.LENGTH_SHORT).show();
        beginOnClick();

//        Log.e(TAG, "55555  - RESULT : " + GlobalBarcode.arrayList.toString());
        stockCode = findViewById(R.id.stockcode);
        unitPrice = findViewById(R.id.unitprice);
        tQuantity = findViewById(R.id.totalquantity);

        brand = findViewById(R.id.brand);
        color = findViewById(R.id.color);

        //Size Drop Down
        size = findViewById(R.id.size);
        ArrayAdapter<String> shoeSize = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.size));
        shoeSize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       size.setAdapter(shoeSize);

//        //Brand Drop Down
        brand = findViewById(R.id.brand);
        ArrayAdapter<String> shoeBrand = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.brand));
        shoeBrand.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brand.setAdapter(shoeBrand);

        //Outlet Drop Down
//        outlet = findViewById(R.id.outlet);
//        ArrayAdapter<String> address = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
//                getResources().getStringArray(R.array.outlet));
//        address.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        outlet.setAdapter(address);

        //Color Drop Down
        color = findViewById(R.id.color);
        ArrayAdapter<String> shoeColor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.color));
        shoeColor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        color.setAdapter(shoeColor);
    }

    public void getFirstName() {
        // Attach a listener to read the data at our posts reference
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference("registration-data")
                .child("user").child(userId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PromoData.firstname = dataSnapshot.child("firstname").getValue(String.class);
                PromoData.lastname = dataSnapshot.child("lastname").getValue(String.class);
                PromoData.locationCode = dataSnapshot.child("outlet").getValue(String.class);
                Toast.makeText(DisplayActivity.this, "Promodiser logged in: " + PromoData.firstname
                                + PromoData.lastname, Toast.LENGTH_LONG).show();

                textView2.setText(PromoData.firstname+ " " + PromoData.lastname);
                textView.setText(PromoData.locationCode);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void getItemInfo() {
        ref = FirebaseDatabase.getInstance().getReference("item-master-list").child("item-data")
                .child("brand");
        ref.addValueEventListener(new ValueEventListener() {

            String description;
            String itemNum;

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                GlobalBarcode.size = snapshot.child("size-code").getValue(String.class);
                GlobalBarcode.color = snapshot.child("color-code").getValue(String.class);
                description = snapshot.child("description").getValue(String.class);
                itemNum = snapshot.child("item-number").getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }

//    public void showFirstName() {
//        Intent intent = getIntent();
//        String firstName = intent.getStringExtra("firstname");
//
//        textView2.setText(firstName);
//
//    }

    public void beginOnClick() {
        scanAgain = findViewById(R.id.button2);
        sendData = findViewById(R.id.button4);

        //Scan Again Button
        scanAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reScan();
            }
        });
        //Send Data Button
        sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalBarcode.stCode = stockCode.getText().toString();
                GlobalBarcode.size = size.getSelectedItem().toString();
                GlobalBarcode.color = color.getSelectedItem().toString();
                GlobalBarcode.uPrice = unitPrice.getText().toString();
                GlobalBarcode.totalQuantity = tQuantity.getText().toString();
                GlobalBarcode.brand = brand.getSelectedItem().toString();
                GlobalBarcode.outlet = outlet.getText().toString();

                if (TextUtils.isEmpty(GlobalBarcode.stCode)) {
                    stockCode.setError("This Field is Required");
                    return;
                } else if (TextUtils.isEmpty(GlobalBarcode.outlet)) {
                    outlet.setError("This Field is Required");
                    return;
                } else if (brand.getSelectedItem().toString().equals("Select Brand")) {
                    TextView errorText = (TextView)brand.getSelectedView();
                    errorText.setError("Please Select Brand");
                    errorText.setTextColor(Color.RED);
                    errorText.setText("Select Brand");
                    return;
                } else if (size.getSelectedItem().toString().equals("Select Size")) {
                    TextView errorText = (TextView)size.getSelectedView();
                    errorText.setError("Please Select Size");
                    errorText.setTextColor(Color.RED);
                    errorText.setText("Select Size");
                    return;
                } else if (color.getSelectedItem().toString().equals("Select Color")) {
                    TextView errorText = (TextView)color.getSelectedView();
                    errorText.setError("Please Select Color");
                    errorText.setTextColor(Color.RED);
                    errorText.setText("Select Color");
                    return;
                } else if (TextUtils.isEmpty(GlobalBarcode.uPrice)) {
                    unitPrice.setError("This Field is Required");
                    return;
                } else if (TextUtils.isEmpty(GlobalBarcode.totalQuantity)) {
                    tQuantity.setError("This Field is Required");
                    return;
                } else {
                    MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
                    myAsyncTasks.execute();
                }
            }
        });
    }

    public void reScan() {

        Intent intent = getIntent();
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setDesiredBarcodeFormats(intentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setCaptureActivity(CapturePortrait.class);
        intentIntegrator.initiateScan();
        //GlobalBarcode.barcode = intent.getStringExtra("barcode_nr");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult results = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        GlobalBarcode.arrayList.add(results.getContents());
        arr.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), results.getContents(), Toast.LENGTH_SHORT).show();

    }

    public class MyAsyncTasks extends AsyncTask<String, String, String> {

        ProgressDialog pD;
        private final String TAG = "Post";
        private Context data;

        public void Empty(Context set) {
            this.data = set;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Display Progress Dialog
            pD = new ProgressDialog(DisplayActivity.this);
            pD.setMessage("Processing Results");
            pD.setCancelable(false);
            pD.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            //fetching data

            String result = "";
            try {
                URL url;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL(esEndpoint);

                    result = performPostCall(esEndpoint, new HashMap<String, ArrayList<String>>() {
                        {
                            put("barcode_number", GlobalBarcode.arrayList);
                        }
                    });

                    Log.e(TAG,"77777 - RESULT: " + result);
                    JSONObject json = new JSONObject(result);
                    return json.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pD.dismiss();

            if (s.contains("Successfully insert the data")){
                Toast.makeText(DisplayActivity.this, "Successfully Send Data!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(DisplayActivity.this, "Failed to create!", Toast.LENGTH_LONG).show();
            }

            try {

                JSONObject jsonObject = new JSONObject(s);

                JSONArray jsonArray1 = jsonObject.getJSONArray("barcode_number");

                int index_no = 1;
                JSONObject jsonObject1 = jsonArray1.getJSONObject(index_no);

                //display success message
//                Toast.makeText(DisplayActivity.this, "Successfully sent data", Toast.LENGTH_LONG).show();


            } catch (JSONException e) {
                e.printStackTrace();

                //displaying error message
//                Toast.makeText(DisplayActivity.this, "Failed to send data", Toast.LENGTH_LONG).show();
            }
        }

        public String performPostCall(String requestURL, HashMap<String, ArrayList<String>> postDataParams) {
            URL url;
            String response = "";

            try {
                url = new URL(requestURL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();
                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                } else {
                    response = "";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//            Log.e(TAG, "55555  - RESULT : " + response);
            return response;
        }

        private String getPostDataString(HashMap<String, ArrayList<String>> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            for (Map.Entry<String, ArrayList<String>> entry : params.entrySet()) {
                result.append("{");
                result.append("\"firstname\":\"" + PromoData.firstname + "\",");
                result.append("\"lastname\":\"" + PromoData.lastname + "\",");
                result.append("\"stock_code\":\"" + GlobalBarcode.stCode + "\",");
                result.append("\"size\":\"" + GlobalBarcode.size + "\",");
                result.append("\"color\":\"" + GlobalBarcode.color + "\",");
                result.append("\"total_quantity\":\"" + GlobalBarcode.totalQuantity + "\",");
                result.append("\"unit_price\":\"" + GlobalBarcode.uPrice + "\",");
                result.append("\"brand\":\"" + GlobalBarcode.brand + "\",");
                result.append("\"outlet\":\"" + GlobalBarcode.outlet + "\",");
                result.append("\"" + URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("\":[");

                for (int counter = 0; counter < GlobalBarcode.arrayList.size(); counter++) {
                    if (counter < GlobalBarcode.arrayList.size() - 1) {
                        result.append("\"" + GlobalBarcode.arrayList.get(counter) + "\"").append(",");
                    } else {
                        result.append("\"" + GlobalBarcode.arrayList.get(counter) + "\"");
                    }
                }
                result.append("]}");
            }
            Log.e(TAG, "55555 - RESULT : " + result.toString());
            return result.toString();
        }
    }
}