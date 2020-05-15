package com.example.mrt.models;

import java.util.ArrayList;

public class PODList {
    private ArrayList<POD> pods;

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

    public ArrayList<POD> getAll() {
        return deepCopy();
    }

    public int getSize() {
        return pods.size();
    }
}
