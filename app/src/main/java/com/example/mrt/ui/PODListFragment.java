package com.example.mrt.ui;

import android.os.Bundle;

import androidx.fragment.app.ListFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mrt.PODArrayAdapter;
import com.example.mrt.RetryCallback;
import com.example.mrt.models.POD;
import com.example.mrt.models.PODList;
import com.example.mrt.models.PODListViewModel;

import java.util.ArrayList;

public class PODListFragment extends ListFragment implements RetryCallback{
    private PODListViewModel podListViewModel;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        podListViewModel = ViewModelProviders.of(requireActivity()).get(PODListViewModel.class);

        final PODArrayAdapter podArrayAdapter = new PODArrayAdapter(getActivity(), new ArrayList<POD>(), this);
        setListAdapter(podArrayAdapter);

        Observer<PODList> observer = new Observer<PODList>() {
            @Override
            public void onChanged(PODList podList) {
                podArrayAdapter.clear();
                podArrayAdapter.addAll(podList.getAll());
                podArrayAdapter.notifyDataSetChanged();
            }
        };
        podListViewModel.getPOD().observe(this, observer);
    }

    @Override
    public void retryUpload(POD pod) {
        podListViewModel.retryUpload(pod);
    }
}