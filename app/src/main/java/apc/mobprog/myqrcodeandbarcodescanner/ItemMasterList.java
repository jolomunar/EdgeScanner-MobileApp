package apc.mobprog.myqrcodeandbarcodescanner;

import androidx.appcompat.app.AppCompatActivity;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.widget.ExpandableListView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.os.Bundle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemMasterList extends AppCompatActivity {

    ExpandableListView masterList;
    MasterListAdapter imListAdapter;
    List<String> bcMasterList;
    HashMap<String, List<String>> infoMasterList;
    private static final String TAG = "";
    private static final String url = "https://edgescanner.herokuapp.com/api/test-upload";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_master_list);

        Log.i(TAG, "Wala" + ItemMasterList.this);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        masterList = findViewById(R.id.imList);

        bcMasterList = new ArrayList<>();
        infoMasterList = new HashMap<>();

        imListAdapter = new MasterListAdapter(this, bcMasterList, infoMasterList);
        masterList.setAdapter(imListAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        makeGetRequest();
    }

    private void makeGetRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                GlobalBarcode.barcode = jsonObject.getString("barcode_number");
                                GlobalBarcode.size = jsonObject.getString("size_code");
                                GlobalBarcode.color = jsonObject.getString("color_code");
                                GlobalBarcode.stCode = jsonObject.getString("item_number");
                                // Retrieve other properties as needed

                                // Add the retrieved data to your data structures
                                bcMasterList.add(GlobalBarcode.barcode);
                                List<String> childItems = new ArrayList<>();
                                childItems.add("Size Code: " + GlobalBarcode.size);
                                childItems.add("Color Code: " + GlobalBarcode.color);
                                childItems.add("Item Number: " + GlobalBarcode.stCode);
                                infoMasterList.put(GlobalBarcode.barcode, childItems);
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imListAdapter.notifyDataSetChanged();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        queue.add(stringRequest);
    }

    @Override
    protected void onStop() {
        super.onStop();
        bcMasterList.clear();
        infoMasterList.clear();
    }
}