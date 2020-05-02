package com.example.mrt.ui.viewmodels;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.mrt.models.POD;
import com.example.mrt.models.PODList;
import com.example.mrt.models.UploadStatus;
import com.example.mrt.services.ImageUploadCb;
import com.example.mrt.services.PODFileRepository;

import java.util.ArrayList;

public class PODListViewModel extends ViewModel {
    private MutableLiveData<PODList> podList = new MutableLiveData<>(new PODList(new ArrayList<POD>()));
    private MutableLiveData<PODList> podsUploaded = new MutableLiveData<>(new PODList(new ArrayList<POD>()));

    private LiveData<ArrayList<String>> lrsUploaded = Transformations.map(podsUploaded, new Function<PODList, ArrayList<String>>() {
        @Override
        public ArrayList<String> apply(PODList input) {
            final ArrayList<POD> pods = input.getAll();
            ArrayList<String> lrs = new ArrayList<>();
            for (POD pod : pods) lrs.add(pod.getLrNo());
            return lrs;
        }
    });

    public MutableLiveData<PODList> getPOD() {
        if (podList == null) {
            podList = new MutableLiveData<>(new PODList(new ArrayList<POD>()));
        }
        return podList;
    }

    public LiveData<ArrayList<String>> getLRNOsUploaded() {
        return lrsUploaded;
    }

    public void add(final POD currentPod) {
        currentPod.setUploadStatus(UploadStatus.IN_PROGRESS);
        podList.setValue(podList.getValue().add(currentPod));
        upload(currentPod);
    }

    public void retryUpload(POD currentPod) {
        currentPod.setUploadStatus(UploadStatus.IN_PROGRESS);
        podList.setValue(podList.getValue().update(currentPod));
        upload(currentPod);
    }

    private void upload(final POD currentPod) {
        ImageUploadCb imageUploadCb = new ImageUploadCb() {
            @Override
            public void onUploadSuccess() {
                currentPod.setUploadStatus(UploadStatus.SUCCESS);
                PODFileRepository.deleteImageFile(currentPod.getImageFilePath());
                podList.setValue(podList.getValue().remove(currentPod));
                podsUploaded.setValue(podsUploaded.getValue().add(currentPod));
            }

            @Override
            public void onUploadFailure() {
                currentPod.setUploadStatus(UploadStatus.FAILURE);
                podList.setValue(podList.getValue().update(currentPod));
            }
        };
        PODFileRepository.uploadImageFile(currentPod, imageUploadCb);
    }

    public int getCount() {
        return this.getPOD().getValue().getSize();
    }
}
