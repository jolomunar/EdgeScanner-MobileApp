package apc.mobprog.myqrcodeandbarcodescanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class StorageAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> group;
    private HashMap<String, List<String>> child;

    public StorageAdapter(Context context, List<String> group, HashMap<String, List<String>> child) {
        this.context = context;
        this.group = group;
        this.child = child;
    }

    @Override
    public int getGroupCount() {
        return this.group.size();
    }

    @Override
    public int getChildrenCount(int i) {
        String parent = this.group.get(i);
        List<String> children = this.child.get(parent);

        if (children != null) {
            return children.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getGroup(int i) {
        return this.group.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        String parent = this.group.get(i);
        return this.child.get(parent).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1)

    {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String pItem = group.get(i);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.dsgroup, null);
        }

        TextView barcodeNumber = view.findViewById(R.id.grpData);
        barcodeNumber.setText(pItem);

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        String pItem = group.get(i);
        String cItem = child.get(pItem).get(i1);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.dschild, null);
        }

        TextView informationText = view.findViewById(R.id.chData);
        informationText.setText(cItem);

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    public void setChildData(int i, List<String> information) {
        String barcode = group.get(i);
        child.put(barcode, information);
        notifyDataSetChanged();
    }

    public int getGroupPosition(String group) {
        return this.group.indexOf(group);
    }

    public int addGroup(String group) {
        this.group.add(group);
        return this.group.size() - 1;
    }

    public boolean hasChildData(int i) {
        return child.containsKey(i);
    }
}
