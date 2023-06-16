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

                        Log.i("ItemMasterList", "bcMasterList: " + bcMasterList);
                        Log.i("ItemMasterList", "infoMasterList: " + infoMasterList);
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            Log.i(TAG, "Respond" + response);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                GlobalBarcode.barcode = jsonObject.getString("group_item");
                                bcMasterList.add(GlobalBarcode.barcode);
                                Log.i("ItemMasterList", "Group Item: " + GlobalBarcode.barcode);

                                JSONArray childArray = jsonObject.getJSONArray("child_items");
                                List<String> childItems = new ArrayList<>();

                                for (int j = 0; j < childArray.length(); j++) {
                                    String childItem = childArray.getString(j);
                                    childItems.add(childItem);
                                    Log.i("ItemMasterList", "Child Items: " + childItems);
                                }

                                infoMasterList.put(GlobalBarcode.barcode, childItems);
                            }

                            imListAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
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
        queue.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        bcMasterList.clear();
        infoMasterList.clear();
    }
}