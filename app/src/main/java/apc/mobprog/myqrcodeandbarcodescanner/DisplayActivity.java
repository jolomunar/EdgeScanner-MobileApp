package apc.mobprog.myqrcodeandbarcodescanner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplayActivity extends AppCompatActivity {

    private static final String TAG = "";
    Button dataStorage;
    ExpandableListView listView;
    ExpandableListViewAdapter newAdapter;
    EditText unitPrice, tQuantity, remarks;
    Spinner brand;

    HashMap<String, JSONObject> info;
    HashMap<String, List<String>> info_barcodes;

    TextView textView2;
    TextView textView;

    FirebaseDatabase node;
    DatabaseReference ref;

    BarcodeStorage barcodeStorage = BarcodeStorage.getInstance();

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        info_barcodes = new HashMap<>();
        info = new HashMap<>();

        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        PromoData.userRole = preferences.getString("UserRole", "");

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        node = FirebaseDatabase.getInstance();

        getFirstName();

        textView2 = findViewById(R.id.textView2);
        textView = findViewById(R.id.textView);


        Intent intent = getIntent();
        GlobalBarcode.barcode = intent.getStringExtra("barcode_nr");

        PromoData.firstname = intent.getStringExtra("firstname");
        PromoData.lastname = intent.getStringExtra("lastname");
        PromoData.locationCode = intent.getStringExtra("locationcode");

        listView = findViewById(R.id.sampleExpandableListView);
        dataStorage = findViewById(R.id.button7);

        newAdapter = new ExpandableListViewAdapter(this, GlobalBarcode.arrayList, info_barcodes);
        listView.setAdapter(newAdapter);

        openList();

//        userInformation();

        Toast.makeText(DisplayActivity.this, "Scanned Barcode: " + GlobalBarcode.barcode, Toast.LENGTH_SHORT).show();

        beginOnClick();

        unitPrice = findViewById(R.id.unitprice);
        tQuantity = findViewById(R.id.totalquantity);

        //Brand Drop Down
        brand = findViewById(R.id.brand);
        ArrayAdapter<String> shoeBrand = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.brand));
        shoeBrand.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brand.setAdapter(shoeBrand);

    }

    public void openList() {

        GlobalBarcode.arrayList.add(GlobalBarcode.barcode);

        Log.i(TAG, "Barcode Number: " + GlobalBarcode.barcode);
        Log.i(TAG, "Item Number: " + GlobalBarcode.stCode);
        Log.i(TAG, "Size Code: " +  GlobalBarcode.size);
        Log.i(TAG, "Color 123 " + GlobalBarcode.color);

        getItemInfo();

        newAdapter.notifyDataSetChanged();

        for (String item : GlobalBarcode.arrayList) {
            List<String> information = new ArrayList<>();
            information.add("Size Code: " + GlobalBarcode.size);
            information.add("Color Code: " + GlobalBarcode.color);
            information.add("Item Number: " + GlobalBarcode.stCode);
            info_barcodes.put(item, information);

            Log.i(TAG, "itmfo" + information);
        }

        if (GlobalBarcode.arrayList.size() > 1) {
            GlobalBarcode.arrayList.remove(0);
        }

    }

    public void getFirstName() {
        String userURL = "https://edgescanner.herokuapp.com/api/getFirstname?firstname=" + PromoData.firstname;
        Log.d(TAG, "expected" + userURL);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, userURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String userResponse) {
                        Log.i(TAG, "Talo: " + userResponse);
                        try {
                            JSONObject jo = new JSONObject(userResponse);
                            PromoData.firstname = jo.getString("firstname");
                            PromoData.lastname = jo.getString("lastname");
                            PromoData.locationCode = jo.getString("location_code");

                            textView2.setText(PromoData.firstname + " " + PromoData.lastname);
                            textView.setText(PromoData.locationCode);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                Log.e(TAG, "onErrorResponse: Request failed: " + error.getMessage());
            }

        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        queue.add(stringRequest);
    }

    public void getItemInfo() {

        String url = "https://edgescanner.herokuapp.com/api/test-upload?barcode_number=" + GlobalBarcode.barcode;
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: Got response: " + response);
                        try {
                            JSONObject jo = new JSONObject(response);
                            GlobalBarcode.barcode = jo.getString("barcode_number");
                            GlobalBarcode.size = jo.getString("size_code");
                            GlobalBarcode.color = jo.getString("color_code");
                            GlobalBarcode.stCode = jo.getString("item_number");

                                // Update child data for the barcode
                                int groupPosition = findGroupPosition(newAdapter, GlobalBarcode.barcode);
                                if (groupPosition != -1) {
                                    List<String> information = new ArrayList<>();
                                    information.add("Size Code: " + GlobalBarcode.size);
                                    information.add("Color Code: " + GlobalBarcode.color);
                                    information.add("Item Number: " + GlobalBarcode.stCode);
                                    newAdapter.setChildData(groupPosition, information);
                                }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                Log.e(TAG, "onErrorResponse: Request failed: " + error.getMessage());
            }

        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        queue.add(stringRequest);
    }

    private int findGroupPosition(ExpandableListViewAdapter adapter, String barcode) {
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            if (adapter.getGroup(i).equals(barcode)) {
                return i;
            }
        }
        return -1;
    }

    public void beginOnClick() {
        dataStorage = findViewById(R.id.button7);

        dataStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataStorage();
            }
        });
    }

    public void dataStorage() {

        remarks = findViewById(R.id.remarks);
        unitPrice = findViewById(R.id.unitprice);
        brand = findViewById(R.id.brand);
        tQuantity = findViewById(R.id.totalquantity);

        GlobalBarcode.remarksAgeGender = remarks.getText().toString();
        GlobalBarcode.uPrice = unitPrice.getText().toString();
        GlobalBarcode.totalQuantity = tQuantity.getText().toString();
        GlobalBarcode.brand = brand.getSelectedItem().toString();

        ArrayList<String> barcodeList = new ArrayList<>();
        barcodeList.add(GlobalBarcode.barcode);

        if (TextUtils.isEmpty(GlobalBarcode.uPrice)) {
            unitPrice.setError("This Field is Required");
            return;
        } else if (TextUtils.isEmpty(GlobalBarcode.remarksAgeGender)) {
            remarks.setError("This Field is Required");
            return;
        } else if (brand.getSelectedItem().toString().equals("Select Brand")) {
            TextView errorText = (TextView) brand.getSelectedView();
            errorText.setError("Please Select Brand");
            errorText.setTextColor(Color.RED);
            errorText.setText("Select Brand");
            return;
        } else if (TextUtils.isEmpty(GlobalBarcode.totalQuantity)) {
            tQuantity.setError("This Field is Required");
            return;
        } else if (GlobalBarcode.size == null) {
            Toast.makeText(DisplayActivity.this, "Invalid Input. Please Scan Again", Toast.LENGTH_LONG).show();
        } else if (GlobalBarcode.color == null) {
            Toast.makeText(DisplayActivity.this, "Invalid Input. Please Scan Again", Toast.LENGTH_LONG).show();
        } else if (GlobalBarcode.stCode == null) {
            Toast.makeText(DisplayActivity.this, "Invalid Input. Please Scan Again", Toast.LENGTH_LONG).show();
        } else {
            try {
                JSONObject jo = new JSONObject();

                jo.put("barcode", GlobalBarcode.barcode);
                jo.put("size", GlobalBarcode.size);
                jo.put("color", GlobalBarcode.color);
                jo.put("stCode", GlobalBarcode.stCode);
                jo.put("brand", GlobalBarcode.brand);
                jo.put("unitPrice", GlobalBarcode.uPrice);
                jo.put("quantity", GlobalBarcode.totalQuantity);
                jo.put("remarks_age_gender", GlobalBarcode.remarksAgeGender);
                jo.put("firstname", PromoData.firstname);
                jo.put("lastname", PromoData.lastname);
                jo.put("locationCode", PromoData.locationCode);

                Log.e("jo", jo.toString());

                barcodeStorage.addList(jo);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(DisplayActivity.this, DataStorage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            startActivity(intent);
            finish();

            Log.i(TAG, "Wrong Intent" + intent);
        }

    }
}