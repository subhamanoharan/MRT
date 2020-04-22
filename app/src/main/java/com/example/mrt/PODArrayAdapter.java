package com.example.mrt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mrt.models.POD;

import java.util.ArrayList;

class PODArrayAdapter extends ArrayAdapter<POD> {
    private final Context context;
    private final ArrayList<POD> values;

    public PODArrayAdapter(Context context, ArrayList<POD> values) {
        super(context, R.layout.pod_list_item, values);
        this.context = context;
        this.values = values;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.pod_list_item, parent, false);
        TextView textView = rowView.findViewById(R.id.pod_lr_no);
        textView.setText(values.get(position).getLrNo());
        return rowView;
    }
}
