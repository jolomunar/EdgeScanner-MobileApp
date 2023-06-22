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
import android.widget.SearchView;

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
    SearchView searchView;
    private static final String TAG = "";
    private static final String url = "https://edgescanner.herokuapp.com/api/test-upload";

    String barcode;
    String sizeCode;
    String colorCode;
    String itemNumber;

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

        searchView = findViewById(R.id.search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle query submission (e.g., perform a search)
                handleSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle query text changes (e.g., filter the data)
                handleSearch(newText);

                searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                    @Override
                    public boolean onClose() {
                        // Clear the search results and restore the original data
                        imListAdapter = new MasterListAdapter(ItemMasterList.this, bcMasterList, infoMasterList);
                        masterList.setAdapter(imListAdapter);
                        return false;
                    }
                });
                return false;
            }
        });


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
                                barcode = jsonObject.getString("barcode_number");
                                sizeCode = jsonObject.getString("size_code");
                                colorCode = jsonObject.getString("color_code");
                                itemNumber = jsonObject.getString("item_number");
                                // Retrieve other properties as needed

                                // Add the retrieved data to your data structures
                                bcMasterList.add(barcode);
                                List<String> childItems = new ArrayList<>();
                                childItems.add("Size Code: " + sizeCode);
                                childItems.add("Color Code: " + colorCode);
                                childItems.add("Item Number: " + itemNumber);
                                infoMasterList.put(barcode, childItems);
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

    private void handleSearch(String query) {
        List<String> filteredBcMasterList = new ArrayList<>();
        HashMap<String, List<String>> filteredInfoMasterList = new HashMap<>();

        // Filter the data based on the query
        for (String barcode : bcMasterList) {
            List<String> childItems = infoMasterList.get(barcode);
            List<String> filteredChildItems = new ArrayList<>();

            // Check if the barcode matches the query
            if (barcode.toUpperCase().contains(query.toUpperCase())) {
                filteredBcMasterList.add(barcode);
                filteredInfoMasterList.put(barcode, childItems);
            } else {
                // Check if any child value matches the query
                for (String childItem : childItems) {
                    if (childItem.toUpperCase().contains(query.toUpperCase())) {
                        filteredChildItems.add(childItem);
                    }
                }

                if (!filteredChildItems.isEmpty()) {
                    filteredBcMasterList.add(barcode);
                    filteredInfoMasterList.put(barcode, filteredChildItems);
                }
            }
        }

        // Update the adapter with filtered data
        imListAdapter = new MasterListAdapter(this, filteredBcMasterList, filteredInfoMasterList);
        masterList.setAdapter(imListAdapter);
    }

}