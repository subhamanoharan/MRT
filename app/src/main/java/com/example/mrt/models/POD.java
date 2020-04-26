package com.example.mrt.models;

public class POD {
    private String imageFilePath;
    private String lrNo;
    private UploadStatus uploadStatus = UploadStatus.WAITING;

    public POD(String imageFilePath){
        this.imageFilePath = imageFilePath;
    }

    public POD(String imageFilePath, String lrNo, UploadStatus uploadStatus) {
        this.imageFilePath = imageFilePath;
        this.lrNo = lrNo;
        this.uploadStatus = uploadStatus;
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

    public void setUploadStatus(UploadStatus uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public UploadStatus getUploadStatus() {
        return this.uploadStatus;
    }
}
