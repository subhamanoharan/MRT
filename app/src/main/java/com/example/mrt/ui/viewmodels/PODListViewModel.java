package com.example.mrt.ui.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mrt.models.POD;
import com.example.mrt.models.PodViewItemsList;
import com.example.mrt.repositories.PodRepository;

import java.util.ArrayList;

public class PODListViewModel extends AndroidViewModel {
    private PodRepository podRepository;
    private LiveData<PodViewItemsList> podViewItems;

    public PODListViewModel(Application application){
        super(application);
        podRepository = new PodRepository(application);
        podViewItems = podRepository.getPodViewItems();
    }

    public LiveData<PodViewItemsList> getPOD() {
        return podViewItems;
    }

    public LiveData<ArrayList<String>> getLRNOsUploaded() {
        return podRepository.getPodsUploaded();
    }

    public void add(final POD currentPod) {
        podRepository.add(currentPod);
    }

    public void retryUpload(POD currentPod) {
        podRepository.retry(currentPod);
    }

    public int getCount() {
        final PodViewItemsList podViewItemsList = podViewItems.getValue();
        return podViewItemsList == null ? 0 : podViewItemsList.getSize();
    }
}
