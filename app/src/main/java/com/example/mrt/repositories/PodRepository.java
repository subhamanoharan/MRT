package com.example.mrt.repositories;

import android.app.Application;
import android.util.Log;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.example.mrt.db.LocalPOD;
import com.example.mrt.models.POD;
import com.example.mrt.models.PODList;
import com.example.mrt.models.UploadStatus;
import com.example.mrt.services.ImageUploadCb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.mrt.models.UploadStatus.FAILURE;
import static com.example.mrt.models.UploadStatus.IN_PROGRESS;
import static com.example.mrt.models.UploadStatus.SUCCESS;

public class PodRepository {
    private final PodDbRepository podDbRepository;

    private MediatorLiveData<PODList> podViewItems = new MediatorLiveData<>();
    private MutableLiveData<HashMap<String, UploadStatus>> networkStatusMap = new MutableLiveData<>(new HashMap<String, UploadStatus>());

    public PodRepository(Application application) {
        podDbRepository = new PodDbRepository(application);
        podViewItems.addSource(podDbRepository.getAll(), getDbObserver());
        podViewItems.addSource(networkStatusMap, getNetworkObserver());
    }

    public void add(final POD currentPod) {
        podDbRepository.add(currentPod);
        upload(currentPod);
    }

    public void retry(POD currentPod) {
        upload(currentPod);
    }

    public LiveData<PODList> getPodViewItems(){
        return podViewItems;
    }

    public LiveData<PODList> getPodsUploaded() {
        return Transformations.map(networkStatusMap, new Function<HashMap<String, UploadStatus>, PODList>() {
            @Override
            public PODList apply(HashMap<String, UploadStatus> nwStatusMap) {
                ArrayList<POD> podsUploaded = new ArrayList<>();
                if(nwStatusMap != null) {
                    final PODList currentPodList = getPodViewItems().getValue();
                    final PODList podList = currentPodList != null ? currentPodList : new PODList(new ArrayList<POD>());
                    for (POD p : podList.getAll()) {
                        if(nwStatusMap.get(p.getImageFilePath())== SUCCESS)
                            podsUploaded.add(new POD(p.getImageFilePath(), p.getLrNo(), SUCCESS));
                    }
                }
                return new PODList(podsUploaded);
            }
        });
    }

    private void remove(POD currentPod) {
        podDbRepository.remove(currentPod);
    }

    private void upload(final POD currentPod) {
        updateNetworkStatus(currentPod, IN_PROGRESS);

        ImageUploadCb imageUploadCb = new ImageUploadCb() {
            @Override
            public void onUploadSuccess() {
                updateNetworkStatus(currentPod, SUCCESS);
                PODFileRepository.deleteImageFile(currentPod.getImageFilePath());
                remove(currentPod);
            }

            @Override
            public void onUploadFailure() {
                updateNetworkStatus(currentPod, FAILURE);
            }
        };
        PODFileRepository.uploadImageFile(currentPod, imageUploadCb);
    }

    private void updateNetworkStatus(POD currentPod, UploadStatus status) {
        final HashMap<String, UploadStatus> value = networkStatusMap.getValue();
        HashMap<String, UploadStatus> newMap = value == null ? new HashMap<String, UploadStatus>() : value;
        newMap.put(currentPod.getImageFilePath(), status);
        networkStatusMap.setValue(newMap);
    }


    private Observer<HashMap<String, UploadStatus>> getNetworkObserver() {
        return new Observer<HashMap<String, UploadStatus>>() {
            @Override
            public void onChanged(HashMap<String, UploadStatus> nwStatusMap) {
                Log.i("---", "FAIL OBSERVER");
                final PODList value = podViewItems.getValue();
                ArrayList<POD> pods = value != null ? value.getAll() : new ArrayList<POD>();
                for(POD l: pods) {
                    final String imageFilePath = l.getImageFilePath();
                    final UploadStatus uploadStatus = nwStatusMap != null && nwStatusMap.containsKey(imageFilePath) ? nwStatusMap.get(imageFilePath) : UploadStatus.WAITING;
                    l.setUploadStatus(uploadStatus);
                }
                podViewItems.setValue(new PODList(pods));
            }
        };
    }

    private Observer<List<LocalPOD>> getDbObserver() {
        return new Observer<List<LocalPOD>>() {
            @Override
            public void onChanged(List<LocalPOD> input) {
                Log.i("---", "DB OBSERVER");
                ArrayList<POD> pods = new ArrayList<>();
                for(LocalPOD l: input) {
                    final HashMap<String, UploadStatus> n = networkStatusMap.getValue();
                    final UploadStatus uploadStatus = n != null && n.containsKey(l.imageFilePath) ? n.get(l.imageFilePath) : UploadStatus.WAITING;
                    pods.add(new POD(l.imageFilePath, l.lrNo, uploadStatus));
                }
                podViewItems.setValue(new PODList(pods));
            }
        };
    }
}
