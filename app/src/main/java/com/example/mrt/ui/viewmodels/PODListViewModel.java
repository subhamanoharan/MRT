package com.example.mrt.ui.viewmodels;

import android.app.Application;

import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.mrt.repositories.PodRepository;
import com.example.mrt.models.POD;
import com.example.mrt.models.PodViewItemsList;

import java.util.ArrayList;

public class PODListViewModel extends AndroidViewModel {
    private PodRepository podRepository;
    private LiveData<PodViewItemsList> podViewItems;

    private LiveData<ArrayList<String>> lrsUploaded;

    public PODListViewModel(Application application){
        super(application);
        podRepository = new PodRepository(application);
        podViewItems = podRepository.getPodViewItems();
        lrsUploaded = Transformations.map(podRepository.getPodsUploaded(), new Function<PodViewItemsList, ArrayList<String>>() {
            @Override
            public ArrayList<String> apply(PodViewItemsList input) {
                final ArrayList<POD> pods = input.getAll();
                ArrayList<String> lrs = new ArrayList<>();
                for (POD pod : pods) lrs.add(pod.getLrNo());
                return lrs;
            }
        });
    }

    public LiveData<PodViewItemsList> getPOD() {
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
        final PodViewItemsList podViewItemsList = this.getPOD().getValue();
        return podViewItemsList == null ? 0 : podViewItemsList.getSize();
    }
}
