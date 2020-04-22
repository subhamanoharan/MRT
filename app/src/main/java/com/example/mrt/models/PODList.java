package com.example.mrt.models;

import java.util.ArrayList;

public class PODList {
    ArrayList<POD> pods;

    PODList(ArrayList<POD> pods){
        this.pods = pods;
    }

    private ArrayList<POD> deepCopy(){
        ArrayList<POD> newList = new ArrayList<>();
        for(POD p : pods) {
            newList.add(new POD(p.getImageFilePath(), p.getLrNo()));
        }
        return newList;
    }

    PODList add(POD currentPod) {
        ArrayList<POD> newList = this.deepCopy();
        newList.add(currentPod);
        return new PODList(newList);
    }

    PODList remove (POD currentPod) {
        ArrayList<POD> newList = new ArrayList<>();
        for(POD p : pods) {
            if(currentPod.getImageFilePath() != p.getImageFilePath())
                newList.add(new POD(p.getImageFilePath(), p.getLrNo()));
        }
        return new PODList(newList);
    }

    public ArrayList<POD> getAll() {
        return deepCopy();
    }
}
