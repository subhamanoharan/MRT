package com.example.mrt.models;

import java.util.ArrayList;

public class PodViewItemsList {
    private ArrayList<POD> pods;

    public PodViewItemsList(ArrayList<POD> pods){
        this.pods = pods;
    }

    private ArrayList<POD> deepCopy(){
        ArrayList<POD> newList = new ArrayList<>();
        for(POD p : pods) {
            newList.add(new POD(p.getImageFilePath(), p.getLrNo(), p.getUploadStatus()));
        }
        return newList;
    }

    public ArrayList<POD> getAll() {
        return deepCopy();
    }

    public int getSize() {
        return pods.size();
    }
}
