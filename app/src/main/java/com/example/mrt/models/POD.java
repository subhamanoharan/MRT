package com.example.mrt.models;

public class POD {
    private String imageFilePath;
    private String lrNo;

    public POD(String imageFilePath){
        this.imageFilePath = imageFilePath;
    }

    public POD(String imageFilePath, String lrNo) {
        this.imageFilePath = imageFilePath;
        this.lrNo = lrNo;
    }

    public String getImageFilePath() {
        return imageFilePath;
    }

    public void setLRNo(String lrNO) {
        this.lrNo = lrNO;
    }

    public String getLrNo() {
        return lrNo;
    }
}
