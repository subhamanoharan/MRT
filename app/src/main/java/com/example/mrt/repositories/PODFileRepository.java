package com.example.mrt.repositories;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PODFileRepository {
    public static File createImageFile(File storageDirectory)  {
        File imageFile = null;
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            imageFile = File.createTempFile(imageFileName, ".jpg", storageDirectory);
        }catch (IOException ex){
            Log.e("---------", ex.getLocalizedMessage());
        }
        return imageFile;
    }

    public static void deleteImageFile(String imageFilePath) {
        new File(imageFilePath).delete();
    }
}
