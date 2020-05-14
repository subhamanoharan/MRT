package com.example.mrt.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pods")
public class LocalPOD {
    @PrimaryKey
    @NonNull
    public String imageFilePath;
    public String lrNo;

    public LocalPOD(String imageFilePath, String lrNo) {
        this.imageFilePath = imageFilePath;
        this.lrNo = lrNo;
    }
}
