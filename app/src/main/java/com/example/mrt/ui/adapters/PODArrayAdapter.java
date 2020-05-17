package com.example.mrt.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mrt.R;
import com.example.mrt.models.POD;
import com.example.mrt.models.UploadStatus;

import java.util.ArrayList;

public class PODArrayAdapter extends ArrayAdapter<POD> {
    private final Context context;
    private final ArrayList<POD> values;
    private RetryCallback retryCallback;

    public PODArrayAdapter(Context context, ArrayList<POD> values, RetryCallback retryCallback) {
        super(context, R.layout.pod_list_item, values);
        this.context = context;
        this.values = values;
        this.retryCallback = retryCallback;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.pod_list_item, parent, false);
        final POD pod = values.get(position);
        final UploadStatus uploadStatus = pod.getUploadStatus();

        TextView textView = rowView.findViewById(R.id.pod_lr_no);
        final View progessIndicator = rowView.findViewById(R.id.progress);
        final View retryIndicator = rowView.findViewById(R.id.retry);
        textView.setText(pod.getLrNo());

        if(uploadStatus == UploadStatus.IN_PROGRESS){
            progessIndicator.setVisibility(View.VISIBLE);
            retryIndicator.setVisibility(View.GONE);
        } else if(uploadStatus == UploadStatus.FAILURE){
            Toast.makeText(this.context, "Failed to upload " + pod.getLrNo(), Toast.LENGTH_LONG).show();
            showRetry(pod, progessIndicator, retryIndicator);
        } else if(uploadStatus == UploadStatus.WAITING){
            showRetry(pod, progessIndicator, retryIndicator);
        }
        return rowView;
    }

    private void showRetry(final POD pod, View progessIndicator, View retryIndicator) {
        progessIndicator.setVisibility(View.GONE);
        retryIndicator.setVisibility(View.VISIBLE);
        retryIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            retryCallback.retryUpload(pod);
            }
        });
    }
}
