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
        podList.setValue(podList.getValue().add(currentPod));
        ImageUploadCb imageUploadCb = new ImageUploadCb() {
            @Override
            public void onUploadSuccess() {
                podList.setValue(podList.getValue().remove(currentPod));
            }

            @Override
            public void onUploadFailure() {
                Log.i("---", "Failed to upload" + currentPod.getLrNo());
            }
        };
        PODFileManager.uploadImageFile(currentPod, imageUploadCb);
    }
}
