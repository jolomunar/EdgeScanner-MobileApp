package apc.mobprog.myqrcodeandbarcodescanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import java.util.HashMap;
import java.util.List;

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> bcList;
    private HashMap<String, List<String>> infoList;

    public ExpandableListViewAdapter(Context context, List<String> bcList, HashMap<String, List<String>> infoList) {
        this.context = context;
        this.bcList = bcList;
        this.infoList = infoList;
    }

    @Override
    public int getGroupCount() {
        return this.bcList.size();
    }

    @Override
    public int getChildrenCount(int pos) {
        String group = this.bcList.get(pos);
        List<String> children = this.infoList.get(group);

        if (children != null) {
            return children.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getGroup(int pos) {
        return this.bcList.get(pos);
    }

    @Override
    public Object getChild(int pos, int cPos) {
        return this.infoList.get(this.bcList.get(pos)).get(cPos);
    }

    @Override
    public long getGroupId(int pos) {
        return pos;
    }

    @Override
    public long getChildId(int pos, int cPos) {
        return cPos;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int pos, boolean isExpanded, View convertView, ViewGroup parent) {
        GlobalBarcode.barcode = (String) getGroup(pos);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.bc_list, null);
        }

        TextView barcodeNumber = convertView.findViewById(R.id.barcodeNumber);
        barcodeNumber.setText(GlobalBarcode.barcode);

        return convertView;
    }

    @Override
    public View getChildView(int pos, int cPos, boolean isLastChild, View convertView, ViewGroup parent) {
        String group = bcList.get(pos);
        String child = infoList.get(group).get(cPos);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.info_list, null);
        }

        TextView informationText = convertView.findViewById(R.id.infoText);
        informationText.setText(child);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int pos, int cPos) {
        return true;
    }

    public void setChildData(int pos, List<String> strings) {
        String barcode = bcList.get(pos);
        infoList.put(barcode, strings);
        notifyDataSetChanged();
    }

    public boolean hasChildData(int pos) {
        return infoList.containsKey(pos);
    }
}
