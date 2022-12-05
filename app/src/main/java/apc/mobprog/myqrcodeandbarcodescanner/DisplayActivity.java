package apc.mobprog.myqrcodeandbarcodescanner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.app.ProgressDialog;
import android.text.TextUtils;
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


import javax.net.ssl.HttpsURLConnection;

public class DisplayActivity extends AppCompatActivity {

    private static final String TAG = "";
    Button scanAgain;
    Button sendData;
    ListView listView;
    EditText fullName, mobileNumber, outlet, stockCode, color, size,
    unitPrice, runningTotal, tQuantity, remarks, brand;
    ArrayAdapter<String> arr;
    String esEndpoint = "https://edgescanner.herokuapp.com/api/ess-api/create";
//   String esEndpoint = "http://localhost:8000/api/ess-api/create";
//   String esEndpoint = "https://eok6418nj8g0skh.m.pipedream.net";
//   String esEndpoint = "https://e7bf9b6b00c727d40a25426a9ec5c20e.m.pipedream.net";
//   String esEndpoint = "https://eotwyaq96coc31b.m.pipedream.net";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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
        color = findViewById(R.id.color);
        size = findViewById(R.id.size);
        unitPrice = findViewById(R.id.unitprice);
        runningTotal = findViewById(R.id.runningtotal);
        remarks = findViewById(R.id.remarks);
        tQuantity = findViewById(R.id.totalquantity);
        brand = findViewById(R.id.brand);
        outlet = findViewById(R.id.outlet);
        fullName = findViewById(R.id.fullName);
        mobileNumber = findViewById(R.id.mobileNumber);
    }


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
                GlobalBarcode.fName = fullName.getText().toString();
                GlobalBarcode.outlet = outlet.getText().toString();
                GlobalBarcode.mobNum = mobileNumber.getText().toString();
                GlobalBarcode.stCode = stockCode.getText().toString();
                GlobalBarcode.size = size.getText().toString();
                GlobalBarcode.color = color.getText().toString();
                GlobalBarcode.uPrice = unitPrice.getText().toString();
                GlobalBarcode.rTotal = runningTotal.getText().toString();
                GlobalBarcode.totalQuantity = tQuantity.getText().toString();
                GlobalBarcode.remarks = remarks.getText().toString();
                GlobalBarcode.brand = brand.getText().toString();


                if (TextUtils.isEmpty(GlobalBarcode.stCode)) {
                    stockCode.setError("This Field is Required");
                    return;
                } else if (TextUtils.isEmpty(GlobalBarcode.color)) {
                    color.setError("This Field is Required");
                    return;
                } else if (TextUtils.isEmpty(GlobalBarcode.size)) {
                    size.setError("This Field is Required");
                    return;
                } else if (TextUtils.isEmpty(GlobalBarcode.uPrice)) {
                    unitPrice.setError("This Field is Required");
                    return;
                } else if (TextUtils.isEmpty(GlobalBarcode.rTotal)) {
                    runningTotal.setError("This Field is Required");
                    return;
                } else if (TextUtils.isEmpty(GlobalBarcode.remarks)) {
                    remarks.setError("This Field is Required");
                    return;
                } else if (TextUtils.isEmpty(GlobalBarcode.totalQuantity)) {
                    tQuantity.setError("This Field is Required");
                    return;
                } else if (TextUtils.isEmpty(GlobalBarcode.brand)) {
                    brand.setError("This Field is Required");
                    return;
                } else if (TextUtils.isEmpty(GlobalBarcode.outlet)) {
                    outlet.setError("This Field is Required");
                    return;
                } else if (TextUtils.isEmpty(GlobalBarcode.fName)) {
                    fullName.setError("This Field is Required");
                    return;
                } else if (TextUtils.isEmpty(GlobalBarcode.mobNum)) {
                    mobileNumber.setError("This Field is Required");
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

            try {

                JSONObject jsonObject = new JSONObject(s);

                JSONArray jsonArray1 = jsonObject.getJSONArray("barcode_number");

                int index_no = 1;
                JSONObject jsonObject1 = jsonArray1.getJSONObject(index_no);


            } catch (JSONException e) {
                e.printStackTrace();
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
            return response;
        }

        private String getPostDataString(HashMap<String, ArrayList<String>> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            for (Map.Entry<String, ArrayList<String>> entry : params.entrySet()) {
                result.append("{");
                result.append("\"full_name\":\"" + GlobalBarcode.fName + "\",");
                result.append("\"outlet\":\"" + GlobalBarcode.outlet + "\",");
                result.append("\"mobile_number\":\"" + GlobalBarcode.mobNum + "\",");
                result.append("\"stock_code\":\"" + GlobalBarcode.stCode + "\",");
                result.append("\"size\":\"" + GlobalBarcode.size + "\",");
                result.append("\"color\":\"" + GlobalBarcode.color + "\",");
                result.append("\"running_total\":\"" + GlobalBarcode.rTotal + "\",");
                result.append("\"total_quantity\":\"" + GlobalBarcode.totalQuantity + "\",");
                result.append("\"unit_price\":\"" + GlobalBarcode.uPrice + "\",");
                result.append("\"brand\":\"" + GlobalBarcode.brand + "\",");
                result.append("\"remarks_age_gender\":\"" + GlobalBarcode.remarks + "\",");
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