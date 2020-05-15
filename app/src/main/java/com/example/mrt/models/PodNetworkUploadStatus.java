package com.example.mrt.models;

import java.util.ArrayList;
import java.util.HashMap;

public class PodNetworkUploadStatus {
    private HashMap<String, UploadStatus> networkStatusMap = new HashMap<>();

    public UploadStatus getStatus(String imageFilePath){
        return networkStatusMap != null && networkStatusMap.containsKey(imageFilePath) ? networkStatusMap.get(imageFilePath) : UploadStatus.WAITING;
    }

    public void updateNetworkStatus(POD currentPod, UploadStatus status) {
        networkStatusMap.put(currentPod.getImageFilePath(), status);
    }

    public ArrayList<String> getFilesWith(UploadStatus status){
        ArrayList<String> filesUploaded = new ArrayList<>();
        for(String key: networkStatusMap.keySet()){
            if(networkStatusMap.get(key) == status)
                filesUploaded.add(key);
        }
        return filesUploaded;
    }
}
