package com.example.mrt.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mrt.ui.viewmodels.PODListViewModel;

import java.util.ArrayList;

public class PODSuccessListFragment extends ListFragment{
    ArrayAdapter<String> adapter;
    private PODListViewModel podListViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        podListViewModel = ViewModelProviders.of(requireActivity()).get(PODListViewModel.class);
        adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1);
        setListAdapter(adapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Observer<ArrayList<String>> observer = new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> lrs) {
                adapter.clear();
                adapter.addAll(lrs);
                adapter.notifyDataSetChanged();
            }
        };
        podListViewModel.getLRNOsUploaded().observe(getViewLifecycleOwner(), observer);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}