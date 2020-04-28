package com.example.mrt.models;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mrt.services.ImageUploadCb;
import com.example.mrt.services.PODFileManager;

import java.util.ArrayList;

public class PODListViewModel extends ViewModel {
    private MutableLiveData<PODList> podList = new MutableLiveData<>(new PODList(new ArrayList<POD>()));

    public MutableLiveData<PODList> getPOD() {
        if (podList == null) {
            podList = new MutableLiveData<>(new PODList(new ArrayList<POD>()));
        }
        return podList;
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
                podList.setValue(podList.getValue().remove(currentPod));
            }

            @Override
            public void onUploadFailure() {
                currentPod.setUploadStatus(UploadStatus.FAILURE);
                podList.setValue(podList.getValue().update(currentPod));
            }
        };
        PODFileManager.uploadImageFile(currentPod, imageUploadCb);
    }
}
