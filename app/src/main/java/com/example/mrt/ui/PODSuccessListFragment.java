package com.example.mrt.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.fragment.app.ListFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mrt.models.PODListViewModel;

import java.util.ArrayList;

public class PODSuccessListFragment extends ListFragment{
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PODListViewModel podListViewModel = ViewModelProviders.of(requireActivity()).get(PODListViewModel.class);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1);
        setListAdapter(adapter);

        Observer<ArrayList<String>> observer = new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> lrs) {
                adapter.clear();
                adapter.addAll(lrs);
                adapter.notifyDataSetChanged();
            }
        };
        podListViewModel.getLRNOsUploaded().observe(this, observer);
    }
}