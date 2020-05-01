package com.example.mrt.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.ListFragment;

import com.example.mrt.PODArrayAdapter;
import com.example.mrt.RetryCallback;
import com.example.mrt.models.POD;
import com.example.mrt.models.UploadStatus;

import java.util.ArrayList;

public class PODListFragment extends ListFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ArrayList<POD> values = new ArrayList<POD>();
        values.add(new POD("1","1", UploadStatus.IN_PROGRESS));
        values.add(new POD("1","2", UploadStatus.IN_PROGRESS));
        values.add(new POD("1","3", UploadStatus.IN_PROGRESS));
        final PODArrayAdapter podArrayAdapter = new PODArrayAdapter(getActivity(), values, new RetryCallback() {
            @Override
            public void retryUpload(POD pod) {
                Log.i("---", "RETTT");
            }
        });
        setListAdapter(podArrayAdapter);
    }
}