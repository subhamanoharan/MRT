package com.example.mrt.models;

import java.util.ArrayList;

public class PODList {
    ArrayList<POD> pods;

    public PODList(ArrayList<POD> pods){
        this.pods = pods;
    }

    private ArrayList<POD> deepCopy(){
        ArrayList<POD> newList = new ArrayList<>();
        for(POD p : pods) {
            newList.add(new POD(p.getImageFilePath(), p.getLrNo(), p.getUploadStatus()));
        }
        return newList;
    }

    public PODList add(POD currentPod) {
        ArrayList<POD> newList = this.deepCopy();
        newList.add(currentPod);
        return new PODList(newList);
    }

    public PODList remove (POD currentPod) {
        ArrayList<POD> newList = new ArrayList<>();
        for(POD p : pods) {
            if(currentPod.getImageFilePath() != p.getImageFilePath())
                newList.add(new POD(p.getImageFilePath(), p.getLrNo(), p.getUploadStatus()));
        }
        return new PODList(newList);
    }

    public PODList update (POD currentPod) {
        ArrayList<POD> newList = new ArrayList<>();
        for(POD p : pods) {
            if(currentPod.getImageFilePath() == p.getImageFilePath())
                newList.add(new POD(currentPod.getImageFilePath(), currentPod.getLrNo(), currentPod.getUploadStatus()));
            else
                newList.add(p);
        }
        return new PODList(newList);
    }

    public ArrayList<POD> getAll() {
        return deepCopy();
    }

    public int getSize() {
        return pods.size();
    }
}
