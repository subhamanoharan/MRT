package com.example.mrt.ui.viewmodels;

import android.app.Application;

import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.mrt.repositories.PodRepository;
import com.example.mrt.models.POD;
import com.example.mrt.models.PODList;

import java.util.ArrayList;

public class PODListViewModel extends AndroidViewModel {
    private PodRepository podRepository;
    private LiveData<PODList> podViewItems;

    private LiveData<ArrayList<String>> lrsUploaded;

    public PODListViewModel(Application application){
        super(application);
        podRepository = new PodRepository(application);
        podViewItems = podRepository.getPodViewItems();
        lrsUploaded = Transformations.map(podRepository.getPodsUploaded(), new Function<PODList, ArrayList<String>>() {
            @Override
            public ArrayList<String> apply(PODList input) {
                final ArrayList<POD> pods = input.getAll();
                ArrayList<String> lrs = new ArrayList<>();
                for (POD pod : pods) lrs.add(pod.getLrNo());
                return lrs;
            }
        });
    }

    public LiveData<PODList> getPOD() {
        return podViewItems;
    }

    public LiveData<ArrayList<String>> getLRNOsUploaded() {
        return lrsUploaded;
    }

    public void add(final POD currentPod) {
        podRepository.add(currentPod);
    }

    public void retryUpload(POD currentPod) {
        podRepository.retry(currentPod);
    }

    public int getCount() {
        final PODList podList = this.getPOD().getValue();
        return podList == null ? 0 : podList.getSize();
    }
}
