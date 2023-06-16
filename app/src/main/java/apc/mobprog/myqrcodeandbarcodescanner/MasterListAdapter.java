package apc.mobprog.myqrcodeandbarcodescanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class MasterListAdapter extends BaseExpandableListAdapter {
    private ItemMasterList itemMasterList;
    private List<String> bcMasterList;
    private HashMap<String, List<String>> infoMasterList;

    public MasterListAdapter(ItemMasterList itemMasterList, List<String> bcMasterList, HashMap<String, List<String>> infoMasterList) {
        this.itemMasterList = itemMasterList;
        this.bcMasterList = bcMasterList;
        this.infoMasterList = infoMasterList;
    }

    @Override
    public int getGroupCount() {
        return this.bcMasterList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        String group = this.bcMasterList.get(i);
        List<String> children = this.infoMasterList.get(group);

        if (children != null) {
            return children.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getGroup(int i) {
        return this.bcMasterList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return this.infoMasterList.get(this.bcMasterList.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        GlobalBarcode.barcode = (String) getGroup(i);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.itemMasterList.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.bcml_list, null);
        }

        TextView barcodeNumber = view.findViewById(R.id.bcMaster);
        barcodeNumber.setText(GlobalBarcode.barcode);

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        String group = bcMasterList.get(i);
        String child = infoMasterList.get(group).get(i1);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.itemMasterList.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.infomaster_list, null);
        }

        TextView barcodeNumber = view.findViewById(R.id.infoMaster);
        barcodeNumber.setText(child);

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    public void setChildData(int i, List<String> strings) {
        String barcode = bcMasterList.get(i);
        infoMasterList.put(barcode, strings);
        notifyDataSetChanged();
    }

    public boolean hasChildData(int i) {
        return infoMasterList.containsKey(i);
    }
}