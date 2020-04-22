package com.example.mrt.models;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mrt.services.ImageUploadCb;
import com.example.mrt.services.PODFileManager;

import java.util.ArrayList;

public class PODListViewModel extends ViewModel {
    private MutableLiveData<ArrayList<POD>> podList = new MutableLiveData<>(new ArrayList<POD>());

    public LiveData<ArrayList<POD>> getPOD() {
        if (podList == null) {
            podList = new MutableLiveData<>(new ArrayList<POD>());
        }
        Log.i("---", "GET POD" + podList.getValue());
        return podList;
    }

    private ArrayList<POD> deepCopy(){
        ArrayList<POD> newList = new ArrayList<POD>();
        for(POD p : podList.getValue()) {
            newList.add(new POD(p.getImageFilePath(), p.getLrNo()));
        }
        return newList;
    }

    public void add(final POD currentPod) {
        Log.i("---", "ADD POD" + currentPod.getLrNo());
        addTo(currentPod);
        ImageUploadCb imageUploadCb = new ImageUploadCb() {
            @Override
            public void onUploadSuccess() {
                rem(currentPod);
            }

            @Override
            public void onUploadFailure() {
                Log.i("---", "Failed to upload" + currentPod.getLrNo());
            }
        };
        PODFileManager.uploadImageFile(currentPod, imageUploadCb);
    }

    private void rem (POD currentPod) {
        Log.i("---", "REM POD" + currentPod.getLrNo());
        ArrayList<POD> newList = new ArrayList<POD>();
        for(POD p : podList.getValue()) {
            if(currentPod.getImageFilePath() != p.getImageFilePath())
            newList.add(new POD(p.getImageFilePath(), p.getLrNo()));
        }
        podList.setValue(newList);
    }

    private void addTo(POD currentPod) {
        ArrayList<POD> newList = deepCopy();
        newList.add(currentPod);
        podList.setValue(newList);
    }
}
