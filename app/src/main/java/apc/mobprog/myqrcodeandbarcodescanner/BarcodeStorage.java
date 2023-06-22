package apc.mobprog.myqrcodeandbarcodescanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class BarcodeStorage {
    private static BarcodeStorage BarcodeStorageInstance = null;
    private JSONArray ja = new JSONArray();

    public static synchronized BarcodeStorage getInstance() {
        if (BarcodeStorageInstance == null) {
            BarcodeStorageInstance = new BarcodeStorage();
        }

        return BarcodeStorageInstance;
    }

    public JSONArray getList() {
        return ja;
    }

    public void addList(JSONObject jo) {
        this.ja.put(jo);
    }

    public void clearList() {
        ja = new JSONArray();
    }
}
