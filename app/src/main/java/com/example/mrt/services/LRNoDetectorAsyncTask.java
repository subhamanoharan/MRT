package com.example.mrt.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class LRNoDetectorAsyncTask extends AsyncTask<String, Void, String> {
    private final BarcodeDetector barcodeDetector;
    private LRDetectorCallback lRDetectorCallback;

    public LRNoDetectorAsyncTask(Context context, LRDetectorCallback lRDetectorCallback){
        this.barcodeDetector = new BarcodeDetector.Builder(context).build();
        this.lRDetectorCallback = lRDetectorCallback;
    }

    @Override
    protected String doInBackground(String... strings) {
        return detect(strings[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(lRDetectorCallback != null) {
            lRDetectorCallback.onScanCompletion(s);
        }
    }

    private String detect(String filePath){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        final Bitmap barcodeBitmap = BitmapFactory.decodeFile(filePath, options);
        String barcode = "";
        try {
            Frame frame = new Frame.Builder().setBitmap(barcodeBitmap).build();
            SparseArray<Barcode> barcodes = barcodeDetector.detect(frame);
            barcode = barcodes.size() != 0 ? barcodes.valueAt(0).rawValue : "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return barcode;
    }
}
