package com.example.mrt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

class PODArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public PODArrayAdapter(Context context, String[] values) {
        super(context, R.layout.pod_list_item, values);
        this.context = context;
        this.values = values;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.pod_list_item, parent, false);
        TextView textView = rowView.findViewById(R.id.pod_lr_no);
        textView.setText(values[position]);
        return rowView;
    }
}
