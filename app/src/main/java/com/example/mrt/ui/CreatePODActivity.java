package com.example.mrt.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.example.mrt.R;
import com.example.mrt.models.POD;
import com.example.mrt.services.LRDetectorCallback;
import com.example.mrt.services.LRNoDetectorAsyncTask;
import com.example.mrt.repositories.PODFileRepository;

import java.io.File;


public class CreatePODActivity extends AppCompatActivity {

    POD currentPod;

    static final int REQUEST_TAKE_PHOTO = 1;
    public static final String LR_NO_EXTRA = "LR_NO";
    public static final String IMAGE_FILE_PATH_EXTRA = "IMAGE_FILE_PATH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pod);
        setupToolBar();
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setupToolBar() {
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.add_pod);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void onScan(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(currentPod != null) clearExistingPOD();
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            final File imageFile = PODFileRepository.createImageFile(getExternalFilesDir(Environment.DIRECTORY_PICTURES));
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

    private void clearExistingPOD() {
        PODFileRepository.deleteImageFile(currentPod.getImageFilePath());
        currentPod = null;
    }

    public void onUpload(View view) {
        final Intent data = new Intent();
        data.putExtra(LR_NO_EXTRA, currentPod.getLrNo());
        data.putExtra(IMAGE_FILE_PATH_EXTRA, currentPod.getImageFilePath());
        setResult(RESULT_OK, data);
        finish();
    }

    private void scanBarCode() {
        final LRDetectorCallback lRDetectorCallback = new LRDetectorCallback() {
            @Override
            public void onScanCompletion(String lrNO) {
                if (!lrNO.isEmpty()) {
                    currentPod.setLRNo(lrNO);
                    showScanSuccess(lrNO);
                } else {
                    showScanFailure(R.string.scan_error_message);
                }
            }
        };
        showScanInProgress();
        new LRNoDetectorAsyncTask(getApplicationContext(), lRDetectorCallback)
                .execute(currentPod.getImageFilePath());
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

    private void showScanInProgress() {
        final TextView barcodeTextView = findViewById(R.id.barcode_content);
        final TextView barcodeErrorView = findViewById(R.id.scan_error);
        final Button uploadBtn = findViewById(R.id.upload_button);
        barcodeErrorView.setVisibility(View.INVISIBLE);
        barcodeTextView.setVisibility(View.VISIBLE);
        barcodeTextView.setText(R.string.scan_in_progress);
        uploadBtn.setEnabled(false);
    }
}
