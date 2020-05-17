package com.example.mrt.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.mrt.db.LocalPOD;
import com.example.mrt.models.POD;
import com.example.mrt.models.PodNetworkUploadStatus;
import com.example.mrt.models.PodViewItemsList;
import com.example.mrt.ui.viewmodels.SingleLiveEvent;

import java.util.ArrayList;
import java.util.List;

public class PodRepository {
    private final PodDbRepository podDbRepository;

    private MediatorLiveData<PodViewItemsList> podViewItems = new MediatorLiveData<>();
    private MutableLiveData<ArrayList<String>> uploadedLrs = new MutableLiveData<>();
    private SingleLiveEvent<POD> failureEvent = new SingleLiveEvent<>();

    private PODNetworkRepository podNetworkRepository;

    public PodRepository(Application application) {
        podDbRepository = new PodDbRepository(application);
        podNetworkRepository = new PODNetworkRepository();
        podViewItems.addSource(podDbRepository.getAll(), getDbObserver());
        podViewItems.addSource(podNetworkRepository.getNetworkStatusMap(), getNetworkObserver());
    }

    public void add(final POD currentPod) {
        podDbRepository.add(currentPod);
        upload(currentPod);
    }

    public void retry(POD currentPod) {
        upload(currentPod);
    }

    public LiveData<PodViewItemsList> getPodViewItems(){
        return podViewItems;
    }

    public LiveData<ArrayList<String>> getPodsUploaded() {
        return uploadedLrs;
    }

    public SingleLiveEvent<POD> getFailureEvent() {
        return failureEvent;
    }

    private void remove(POD currentPod) {
        podDbRepository.remove(currentPod);
    }

    private void upload(final POD currentPod) {
        ImageUploadCb imageUploadCb = new ImageUploadCb() {
            @Override
            public void onUploadSuccess() {
                PODFileRepository.deleteImageFile(currentPod.getImageFilePath());
                remove(currentPod);
                addToUploadedLrs(currentPod);
            }

            @Override
            public void onUploadFailure() {
                failureEvent.setValue(currentPod);
            }
        };
        podNetworkRepository.uploadImageFile(currentPod, imageUploadCb);
    }

    private void addToUploadedLrs(POD pod) {
        final ArrayList<String> value = uploadedLrs.getValue();
        final ArrayList<String> lrsValue = value == null ? new ArrayList<String>() : value;
        lrsValue.add(pod.getLrNo());
        uploadedLrs.setValue(lrsValue);
    }

    private Observer<PodNetworkUploadStatus> getNetworkObserver() {
        return new Observer<PodNetworkUploadStatus>() {
            @Override
            public void onChanged(PodNetworkUploadStatus nwStatusMap) {
                final PodViewItemsList value = podViewItems.getValue();
                ArrayList<POD> pods = value != null ? value.getAll() : new ArrayList<POD>();
                for(POD l: pods)
                    l.setUploadStatus(nwStatusMap.getStatus(l.getImageFilePath()));
                podViewItems.setValue(new PodViewItemsList(pods));
            }
        };
    }

    private Observer<List<LocalPOD>> getDbObserver() {
        return new Observer<List<LocalPOD>>() {
            @Override
            public void onChanged(List<LocalPOD> input) {
                ArrayList<POD> pods = new ArrayList<>();
                for(LocalPOD l: input)
                    pods.add(new POD(l.imageFilePath, l.lrNo, podNetworkRepository.getStatus(l.imageFilePath)));
                podViewItems.setValue(new PodViewItemsList(pods));
            }
        };
    }
}
