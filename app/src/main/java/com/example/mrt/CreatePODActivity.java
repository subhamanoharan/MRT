package com.example.mrt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CreatePODActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pod);
    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    static final int REQUEST_TAKE_PHOTO = 1;

    public void dispatchTakePictureIntent(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("---------", ex.getLocalizedMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Log.i("-------", photoFile.getAbsolutePath());
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.mrt",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final ImageView barcodeImgView = findViewById(R.id.image);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            scanBarCodeF(imageBitmap);
            barcodeImgView.setImageBitmap(imageBitmap);

        }
    }

    public void scanBarCode(View view) {
        final TextView barcodeTextView = findViewById(R.id.barcode_content);
        String lrNO = detectLRNo();
            if (!lrNO.isEmpty()) {
                barcodeTextView.setText("CODE Data: " + lrNO);
            } else {
                barcodeTextView.setText("No Code found!");
                barcodeTextView.setTextColor(Color.RED);
            }
    }

    public String detectLRNo(){
        final ImageView barcodeImgView = findViewById(R.id.image);
        String barcode = "";
        try {
            Bitmap barcodeBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.podc);
            BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(getApplicationContext())
                    .build();

            Frame frame = new Frame.Builder().setBitmap(barcodeBitmap).build();
            SparseArray<Barcode> barcodes = barcodeDetector.detect(frame);
            barcode = barcodes.size() != 0 ? barcodes.valueAt(0).rawValue : "";
            barcodeImgView.setImageBitmap(barcodeBitmap);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return barcode;
    }

    public void scanBarCodeF(Bitmap b) {
        final TextView barcodeTextView = findViewById(R.id.barcode_content);
        final Bitmap bitmapBig = BitmapFactory.decodeFile(currentPhotoPath);
        String lrNO = detectLRNoF(bitmapBig);
        if (!lrNO.isEmpty()) {
            barcodeTextView.setText("CODE Data: " + lrNO);
        } else {
            barcodeTextView.setText("No Code found!");
            barcodeTextView.setTextColor(Color.RED);
        }
    }


    public String detectLRNoF(Bitmap barcodeBitmap){
        String barcode = "";
        try {
            BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(getApplicationContext())
                    .build();

            Frame frame = new Frame.Builder().setBitmap(barcodeBitmap).build();
            SparseArray<Barcode> barcodes = barcodeDetector.detect(frame);
            barcode = barcodes.size() != 0 ? barcodes.valueAt(0).rawValue : "";
        } catch (Exception e) {
            e.printStackTrace();

        }
        return barcode;
    }

}
