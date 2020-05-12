package com.example.mrt.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mrt.models.POD;
import com.example.mrt.models.PODList;
import com.example.mrt.ui.adapters.PODArrayAdapter;
import com.example.mrt.ui.adapters.RetryCallback;
import com.example.mrt.ui.viewmodels.PODListViewModel;

import java.util.ArrayList;

public class PODListFragment extends ListFragment implements RetryCallback {
    private PODListViewModel podListViewModel;
    private PODArrayAdapter podArrayAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        podListViewModel = ViewModelProviders.of(requireActivity()).get(PODListViewModel.class);
        podArrayAdapter = new PODArrayAdapter(requireActivity(), new ArrayList<POD>(), this);
        setListAdapter(podArrayAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Observer<PODList> observer = new Observer<PODList>() {
            @Override
            public void onChanged(PODList podList) {
                podArrayAdapter.clear();
                podArrayAdapter.addAll(podList.getAll());
                podArrayAdapter.notifyDataSetChanged();
            }
        };
        podListViewModel.getPOD().observe(getViewLifecycleOwner(), observer);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        podArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void retryUpload(POD pod) {
        podListViewModel.retryUpload(pod);
    }
}