package com.example.mrt.repositories;

import android.util.Log;

import com.example.mrt.models.POD;
import com.example.mrt.services.ImageUploadCb;
import com.example.mrt.services.api.ImageUploadService;
import com.example.mrt.services.api.ServiceGenerator;

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

    public static void uploadImageFile(POD pod, final ImageUploadCb imageUploadCb){
        ImageUploadService service = ServiceGenerator.createService(ImageUploadService.class);
        File file = new File(pod.getImageFilePath());
        RequestBody requestFile = RequestBody.create(MediaType.parse("Image/jpg"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", pod.getLrNo(), requestFile);
        Call<ResponseBody> call = service.upload(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                imageUploadCb.onUploadSuccess();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("----CALL FAILED", t.getMessage());
                imageUploadCb.onUploadFailure();
            }
        });
    }

    public static void deleteImageFile(String imageFilePath) {
        new File(imageFilePath).delete();
    }
}
