package com.example.mrt.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.SparseArray;

import com.example.mrt.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class LRBarcodeDetector {
    private final Context ctx;

    public LRBarcodeDetector(Context ctx){
        this.ctx = ctx;
    }
    public String detectLRNo(){
        String barcode = "";
        try {
            Bitmap barcodeBitmap = BitmapFactory.decodeResource(ctx.getApplicationContext().getResources(), R.drawable.image_with_bc);
            BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(ctx.getApplicationContext()).build();

            Frame frame = new Frame.Builder().setBitmap(barcodeBitmap).build();
            SparseArray<Barcode> barcodes = barcodeDetector.detect(frame);
            barcode = barcodes.size() != 0 ? barcodes.valueAt(0).rawValue : "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return barcode;
    }
}