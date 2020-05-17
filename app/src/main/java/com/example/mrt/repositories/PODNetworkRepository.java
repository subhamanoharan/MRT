package com.example.mrt.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.mrt.models.POD;
import com.example.mrt.models.PodNetworkUploadStatus;
import com.example.mrt.models.UploadStatus;
import com.example.mrt.services.api.ImageUploadService;
import com.example.mrt.services.api.ServiceGenerator;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class PODNetworkRepository {

    private MutableLiveData<PodNetworkUploadStatus> networkStatusMap = new MutableLiveData<>(new PodNetworkUploadStatus());

    void uploadImageFile(final POD pod, final ImageUploadCb imageUploadCb){
        updateNetworkStatus(pod, UploadStatus.IN_PROGRESS);
        ImageUploadService service = ServiceGenerator.createService(ImageUploadService.class);
        File file = new File(pod.getImageFilePath());
        RequestBody requestFile = RequestBody.create(MediaType.parse("Image/jpg"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", pod.getLrNo(), requestFile);
        Call<ResponseBody> call = service.upload(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                updateNetworkStatus(pod, UploadStatus.SUCCESS);
                imageUploadCb.onUploadSuccess();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("----CALL FAILED", t.getMessage());
                updateNetworkStatus(pod, UploadStatus.FAILURE);
                imageUploadCb.onUploadFailure();
            }
        });
    }

    MutableLiveData<PodNetworkUploadStatus> getNetworkStatusMap() {
        return networkStatusMap;
    }

    UploadStatus getStatus(String imageFilePath) {
        final PodNetworkUploadStatus podNetworkUploadStatus = networkStatusMap.getValue() != null ? networkStatusMap.getValue() : new PodNetworkUploadStatus();
        return podNetworkUploadStatus.getStatus(imageFilePath);
    }

    private void updateNetworkStatus(POD pod, UploadStatus status) {
        final PodNetworkUploadStatus podNetworkUploadStatus = networkStatusMap.getValue() != null ? networkStatusMap.getValue() : new PodNetworkUploadStatus();
        podNetworkUploadStatus.updateNetworkStatus(pod, status);
        networkStatusMap.setValue(podNetworkUploadStatus);
    }
}
