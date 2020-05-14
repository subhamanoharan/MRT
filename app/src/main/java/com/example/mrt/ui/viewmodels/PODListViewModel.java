package com.example.mrt.ui.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.example.mrt.db.LocalPOD;
import com.example.mrt.db.PodRepository;
import com.example.mrt.models.POD;
import com.example.mrt.models.PODList;
import com.example.mrt.models.UploadStatus;
import com.example.mrt.services.ImageUploadCb;
import com.example.mrt.services.PODFileRepository;

import java.util.ArrayList;
import java.util.List;

import static com.example.mrt.models.UploadStatus.FAILURE;
import static com.example.mrt.models.UploadStatus.IN_PROGRESS;

public class PODListViewModel extends AndroidViewModel {
    private final PodRepository podRepository;
    private MutableLiveData<List<String>> filesFailed = new MutableLiveData<>();
    private LiveData<List<LocalPOD>> localPODs;
    private MutableLiveData<PODList> podsUploaded = new MutableLiveData<>(new PODList(new ArrayList<POD>()));

    MediatorLiveData<PODList> podList = new MediatorLiveData<PODList>();

    private LiveData<ArrayList<String>> lrsUploaded = Transformations.map(podsUploaded, new Function<PODList, ArrayList<String>>() {
        @Override
        public ArrayList<String> apply(PODList input) {
            final ArrayList<POD> pods = input.getAll();
            ArrayList<String> lrs = new ArrayList<>();
            for (POD pod : pods) lrs.add(pod.getLrNo());
            return lrs;
        }
    });

    public PODListViewModel(Application application){
        super(application);
        podRepository = new PodRepository(application);
        localPODs = podRepository.getAll();
        Observer<List<LocalPOD>> observer = new Observer<List<LocalPOD>>() {
            @Override
            public void onChanged(List<LocalPOD> input) {
                Log.i("---", "DB OBSERVER");
                ArrayList<POD> pods = new ArrayList<>();
                for(LocalPOD l: input) {
                    final List<String> value = filesFailed.getValue();
                    final UploadStatus uploadStatus = value != null && value.contains(l.imageFilePath) ? FAILURE : IN_PROGRESS;
                    pods.add(new POD(l.imageFilePath, l.lrNo, uploadStatus));
                }
                podList.setValue(new PODList(pods));
            }
        };
        Observer<List<String>> observer2 = new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> inputs) {
                Log.i("---", "FAIL OBSERVER");
                ArrayList<POD> pods = new ArrayList<>();
                for(POD l: podList.getValue().getAll()) {
                    final UploadStatus uploadStatus = inputs.contains(l.getImageFilePath()) ? FAILURE : IN_PROGRESS;
                    pods.add(new POD(l.getImageFilePath(), l.getLrNo(), uploadStatus));
                }
                podList.setValue(new PODList(pods));
            }
        };
        podList.addSource(localPODs, observer);
        podList.addSource(filesFailed, observer2);
    }

    public LiveData<PODList> getPOD() {
//        if (podList == null) {
//            podList = new MutableLiveData<>(new PODList(new ArrayList<POD>()));
//        }
        return podList;
    }

    public LiveData<ArrayList<String>> getLRNOsUploaded() {
        return lrsUploaded;
    }

    public void add(final POD currentPod) {
        currentPod.setUploadStatus(IN_PROGRESS);
        podRepository.add(currentPod);
        //        podList.setValue(podList.getValue().add(currentPod));
        upload(currentPod);
    }

    public void retryUpload(POD currentPod) {
        currentPod.setUploadStatus(IN_PROGRESS);
//        podList.setValue(podList.getValue().update(currentPod));
        final List<String> newList = filesFailed.getValue();
        newList.remove(currentPod.getImageFilePath());
        filesFailed.setValue(newList);
        upload(currentPod);
    }

    private void upload(final POD currentPod) {
        ImageUploadCb imageUploadCb = new ImageUploadCb() {
            @Override
            public void onUploadSuccess() {
                currentPod.setUploadStatus(UploadStatus.SUCCESS);
                PODFileRepository.deleteImageFile(currentPod.getImageFilePath());
//                podList.setValue(podList.getValue().remove(currentPod));
                podRepository.remove(currentPod);
                podsUploaded.setValue(podsUploaded.getValue().add(currentPod));
            }

            @Override
            public void onUploadFailure() {
                currentPod.setUploadStatus(FAILURE);
//                podList.setValue(podList.getValue().update(currentPod));
                final List<String> newList = filesFailed.getValue() != null? filesFailed.getValue() : new ArrayList<String>();
                Log.i("---", "ON FAIL" + newList);
                newList.add(currentPod.getImageFilePath());
                filesFailed.setValue(newList);

            }
        };
        PODFileRepository.uploadImageFile(currentPod, imageUploadCb);
    }

    public int getCount() {
        return this.getPOD().getValue().getSize();
    }
}
