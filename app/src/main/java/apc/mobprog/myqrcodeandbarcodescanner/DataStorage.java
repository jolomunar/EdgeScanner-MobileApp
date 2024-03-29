package apc.mobprog.myqrcodeandbarcodescanner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataStorage extends AppCompatActivity {

    private static final String TAG = "";
    Button send;
    Button scanAgain;
    @SuppressLint("StaticFieldLeak")
    static ExpandableListView ds;
    @SuppressLint("StaticFieldLeak")
    static StorageAdapter dsa;
    List<String> bcnm;

    private TextView dataCountTextView;

    HashMap<String, JSONObject> bcin1;
    private HashMap<String, List<String>> bcin = new HashMap<>();


    String size, color, stCode, brand, unitPrice,
            quantity, remarks, fName, lName, locCode;

    String esEndpoint = "https://edgescanner.herokuapp.com/api/ess-api/create";
//    String esEndpoint = "https://eok6418nj8g0skh.m.pipedream.net";

    ArrayList<String> barcodeList;
    BarcodeStorage barcodeStorage = BarcodeStorage.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_storage);

        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        PromoData.userRole = preferences.getString("UserRole", "");


        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ds = findViewById(R.id.dataStorageView);

        dataCountTextView = findViewById(R.id.dataCountTextView);

        beginOnClick();

        viewList();
    }

    private void viewList() {
        bcnm = new ArrayList<>();
        bcin = new HashMap<>();
        bcin1 = new HashMap<>();

        JSONArray ja = barcodeStorage.getList();
        for (int i = 0 ; i < ja.length(); i++) {
            JSONObject jo = null;
            try {
                jo = ja.getJSONObject(i);
                String jo_barcode = jo.getString("barcode");
                String jo_size = jo.getString("size");
                String jo_color = jo.getString("color");
                String jo_stCode = jo.getString("stCode");
                String jo_brand = jo.getString("brand");
                String jo_unitPrice = jo.getString("unitPrice");
                String jo_quantity = jo.getString("quantity");
                String jo_remarks_age_gender = jo.getString("remarks_age_gender");
                String jo_firstname = jo.getString("firstname");
                String jo_lastname = jo.getString("lastname");
                String jo_locationCode = jo.getString("locationCode");

                List<String> newInfo = new ArrayList<>();
                newInfo.add("Size Code: " + jo_size);
                newInfo.add("Color Code: " + jo_color);
                newInfo.add("Item Number: " + jo_stCode);
                newInfo.add("Brand: " + jo_brand);
                newInfo.add("Unit Price: " + jo_unitPrice);
                newInfo.add("Quantity: " + jo_quantity);
                newInfo.add("Remarks Age/Gender: " + jo_remarks_age_gender);
                newInfo.add("Sender: " + jo_firstname + " " + jo_lastname);
                newInfo.add("Store Location: " + jo_locationCode);
                bcin.put(jo_barcode, newInfo);
                bcnm.add(jo_barcode);

                bcin1.put(jo_barcode, jo);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        dsa = new StorageAdapter(this, bcnm, bcin);
        ds.setAdapter(dsa);

        // Notify the adapter of the data change
        dsa.notifyDataSetChanged();

        // Update the data count
        updateDataCount();

        Log.i(TAG, "Barcode Information:" + bcin);
    }

    private void updateDataCount() {
        int itemCount = bcin.size();
        dataCountTextView.setText("Data to be Sent: " + itemCount);
    }


    private void beginOnClick() {
        scanAgain = findViewById(R.id.button8);
        send = findViewById(R.id.button5);


        //Scan Again Button
        scanAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reScan();
            }
        });

        //Send Data Button
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendDataToEndpoint();
            }
        });
    }

    public void reScan() {
        bcnm.clear();
        bcin.clear();

        Intent intent = new Intent(this, DataStorage.class);
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setDesiredBarcodeFormats(intentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setCaptureActivity(CapturePortrait.class);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult results = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (results != null && results.getContents() != null) {
            String scannedBarcode = results.getContents();

            // Check if the scanned barcode is already present in the list
            if (!bcnm.contains(scannedBarcode)) {
                // Add the scanned barcode to the list
                bcnm.add(scannedBarcode);

                // Find the position of the scanned barcode in the group list
                int position = dsa.getGroupPosition(scannedBarcode);

                // If the scanned barcode is not present in the group list, add it as a new group
                if (position == -1) {
                    position = dsa.addGroup(scannedBarcode);
                }

                // Get the barcode information for the scanned barcode
                List<String> barcodeInfo = new ArrayList<>();
                barcodeInfo.add("Size Code: " + size);
                barcodeInfo.add("Color Code: " + color);
                barcodeInfo.add("Item Number: " + stCode);
                barcodeInfo.add("Brand: " + brand);
                barcodeInfo.add("Unit Price: " + unitPrice);
                barcodeInfo.add("Quantity: " + quantity);
                barcodeInfo.add("Remarks Age/Gender: " + remarks);
                barcodeInfo.add("Sender: " + fName + " " + lName);
                barcodeInfo.add("Store Location: " + locCode);

                // Notify the adapter of the data change
                dsa.setChildData(position, barcodeInfo);
            }

            // Start the display activity and pass the scanned barcode
            Intent intent = new Intent(this, DisplayActivity.class);
            intent.putExtra("barcode_nr", scannedBarcode);
            startActivity(intent);
            finish();


        }
        Log.i(TAG, "Expandable Information" + bcin);
    }
    private void sendDataToEndpoint() {
        final ProgressDialog pD = new ProgressDialog(DataStorage.this);
        pD.setMessage("Processing Results...");
        pD.setCancelable(false);
        pD.show();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, esEndpoint,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "Panalo" + response);

                        if (ds.getAdapter() != null) {
                            Toast.makeText(DataStorage.this, "Successfully inserted the data", Toast.LENGTH_SHORT).show();
                            barcodeStorage.clearList();
                            ds.setAdapter((BaseExpandableListAdapter)null);
                            pD.dismiss();

                            dataCountTextView.setText("Data to be Sent: 0");
                        } else if (!isNetworkAvailable()) {
                            Toast.makeText(DataStorage.this, "No internet connection available", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            Toast.makeText(DataStorage.this, "Failed to send the data", Toast.LENGTH_SHORT).show();
                        }

                        // Dismiss the progress dialog if it is showing
                        if (pD != null && pD.isShowing()) {
                            pD.dismiss();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error response

                        // Dismiss the progress dialog if it is showing
                        if (pD != null && pD.isShowing()) {
                            if(ds.getAdapter() == null) {
                                Toast.makeText(DataStorage.this, "Failed to send the data", Toast.LENGTH_SHORT).show();
                                pD.dismiss();
                            }
                        }
                    }
                }) {
            @Override
            public String getBodyContentType() {
                // Specify that the request body is in JSON format
                return "application/json";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                // Build the JSON object for the request body

                JSONArray itemsArray = new JSONArray();
                try {
                    JSONArray ja = barcodeStorage.getList();
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);
                        JSONObject itemObject = new JSONObject();
                        itemObject.put("firstname", jo.getString("firstname"));
                        itemObject.put("lastname", jo.getString("lastname"));
                        itemObject.put("barcode_number", jo.getString("barcode"));
                        itemObject.put("stock_code", jo.getString("stCode"));
                        itemObject.put("color", jo.getString("color"));
                        itemObject.put("size", jo.getString("size"));
                        itemObject.put("unit_price", jo.getString("unitPrice"));
                        itemObject.put("total_quantity", jo.getString("quantity"));
                        itemObject.put("brand", jo.getString("brand"));
                        itemObject.put("outlet", jo.getString("locationCode"));
                        itemObject.put("remarks", jo.getString("remarks_age_gender"));
                        itemsArray.put(itemObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return itemsArray.toString().getBytes();
            }
        };
        requestQueue.add(stringRequest);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
    @Override
    public void onBackPressed() {
        // Clear the data from the array or perform any other cleanup tasks
        barcodeStorage.clearList();

        // Call the super implementation to allow the default back button behavior
        super.onBackPressed();
    }
}