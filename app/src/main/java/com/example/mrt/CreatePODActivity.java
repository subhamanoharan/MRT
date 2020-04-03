package com.example.mrt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.mrt.services.api.ImageUploadService;
import com.example.mrt.services.api.ServiceGenerator;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CreatePODActivity extends AppCompatActivity {

    String currentPhotoPath;

    static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pod);
    }

    public void onScan(View v) {
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
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.mrt", photoFile);
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
            scanBarCode();
            barcodeImgView.setImageBitmap(imageBitmap);
        }
    }

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

    private void scanBarCode() {
        final TextView barcodeTextView = findViewById(R.id.barcode_content);
        final TextView barcodeErrorView = findViewById(R.id.scan_error);
        final Button uploadBtn = findViewById(R.id.upload_button);
        String lrNO = detectLRNo();
        if (!lrNO.isEmpty()) {
            barcodeTextView.setVisibility(View.VISIBLE);
            barcodeTextView.setText(lrNO);
            barcodeErrorView.setVisibility(View.INVISIBLE);
            uploadBtn.setEnabled(true);
        } else {
            barcodeTextView.setVisibility(View.INVISIBLE);
            barcodeErrorView.setVisibility(View.VISIBLE);
            uploadBtn.setEnabled(false);
        }
    }


    private String detectLRNo(){

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        final Bitmap barcodeBitmap = BitmapFactory.decodeFile(currentPhotoPath, options);
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

    public void onUpload(View view) {
        uploadFile(currentPhotoPath);
    }

    private void uploadFile(String filePath) {
        ImageUploadService service = ServiceGenerator.createService(ImageUploadService.class);
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("Image/jpg"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        Call<ResponseBody> call = service.upload(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                onUploadSuccess();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                onUploadFailure();
            }
        });
    }

    private void onUploadFailure() {
        Toast.makeText(this, R.string.upload, Toast.LENGTH_LONG).show();
    }

    private void onUploadSuccess() {
        Toast.makeText(this, R.string.upload_success, Toast.LENGTH_LONG).show();
        recreate();
    }
}
