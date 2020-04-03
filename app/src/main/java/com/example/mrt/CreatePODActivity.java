package com.example.mrt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.mrt.models.POD;
import com.example.mrt.services.ImageUploadCb;
import com.example.mrt.services.PODFileManager;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.File;


public class CreatePODActivity extends AppCompatActivity implements ImageUploadCb{

    POD currentPod;

    static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pod);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final ImageView barcodeImgView = findViewById(R.id.image);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            scanBarCode();
            barcodeImgView.setImageBitmap(imageBitmap);
        }
    }

    public void onScan(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            final File imageFile = PODFileManager.createImageFile(getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            if (imageFile != null) {
                currentPod = new POD(imageFile.getAbsolutePath());
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.mrt", imageFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            } else
                showScanFailure(R.string.camera_capture_failure);
        } else
            showScanFailure(R.string.camera_capture_failure);
    }

    public void onUpload(View view) {
        PODFileManager.uploadImageFile(currentPod, this);
    }

    private void scanBarCode() {
        String lrNO = detectLRNo();
        if (!lrNO.isEmpty()) {
            currentPod.setLRNo(lrNO);
            showScanSuccess(lrNO);
        } else {
            showScanFailure(R.string.scan_error_message);
        }
    }

    private String detectLRNo(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        final Bitmap barcodeBitmap = BitmapFactory.decodeFile(currentPod.getImageFilePath(), options);
        String barcode = "";
        try {
            BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(getApplicationContext()).build();
            Frame frame = new Frame.Builder().setBitmap(barcodeBitmap).build();
            SparseArray<Barcode> barcodes = barcodeDetector.detect(frame);
            barcode = barcodes.size() != 0 ? barcodes.valueAt(0).rawValue : "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return barcode;
    }

    private void showScanSuccess(String lrNO) {
        final TextView barcodeTextView = findViewById(R.id.barcode_content);
        final TextView barcodeErrorView = findViewById(R.id.scan_error);
        final Button uploadBtn = findViewById(R.id.upload_button);
        barcodeTextView.setVisibility(View.VISIBLE);
        barcodeTextView.setText(lrNO);
        barcodeErrorView.setVisibility(View.INVISIBLE);
        uploadBtn.setEnabled(true);
    }

    private void showScanFailure(int errorMsgResId) {
        final TextView barcodeTextView = findViewById(R.id.barcode_content);
        final TextView barcodeErrorView = findViewById(R.id.scan_error);
        final Button uploadBtn = findViewById(R.id.upload_button);
        barcodeTextView.setVisibility(View.INVISIBLE);
        barcodeErrorView.setVisibility(View.VISIBLE);
        barcodeErrorView.setText(errorMsgResId);
        uploadBtn.setEnabled(false);
    }

    @Override
    public void onUploadSuccess() {
        Toast.makeText(this, R.string.upload_success, Toast.LENGTH_LONG).show();
        recreate();
    }

    @Override
    public void onUploadFailure() {
        Toast.makeText(this, R.string.upload_failed, Toast.LENGTH_LONG).show();
    }
}
