package com.example.mrt.repositories;

import android.app.Application;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.example.mrt.db.LocalPOD;
import com.example.mrt.models.POD;
import com.example.mrt.models.PodViewItemsList;
import com.example.mrt.models.PodNetworkUploadStatus;

import java.util.ArrayList;
import java.util.List;

import static com.example.mrt.models.UploadStatus.SUCCESS;

public class PodRepository {
    private final PodDbRepository podDbRepository;

    private MediatorLiveData<PodViewItemsList> podViewItems = new MediatorLiveData<>();
    private PODNetworkRepository podNetworkRepository;

    public PodRepository(Application application) {
        podDbRepository = new PodDbRepository(application);
        podNetworkRepository = new PODNetworkRepository();
        podViewItems.addSource(podDbRepository.getAll(), getDbObserver());
        podViewItems.addSource(podNetworkRepository.getNetworkStatusMap(), getNetworkObserver());
    }

    public void add(final POD currentPod) {
        podDbRepository.add(currentPod);
        upload(currentPod);
    }

    public void retry(POD currentPod) {
        upload(currentPod);
    }

    public LiveData<PodViewItemsList> getPodViewItems(){
        return podViewItems;
    }

    public LiveData<PodViewItemsList> getPodsUploaded() {
        return Transformations.map(podNetworkRepository.getUploadedFiles(), new Function<ArrayList<String>, PodViewItemsList>() {
            @Override
            public PodViewItemsList apply(ArrayList<String> filesUploaded) {
                ArrayList<POD> podsUploaded = new ArrayList<>();
                final PodViewItemsList currentPodViewItemsList = getPodViewItems().getValue();
                final PodViewItemsList podViewItemsList = currentPodViewItemsList != null ? currentPodViewItemsList : new PodViewItemsList(new ArrayList<POD>());
                for (POD p : podViewItemsList.getAll()) {
                    if(filesUploaded.contains(p.getImageFilePath()))
                        podsUploaded.add(new POD(p.getImageFilePath(), p.getLrNo(), SUCCESS));
                }
            return new PodViewItemsList(podsUploaded);
            }
        });
    }

    private void remove(POD currentPod) {
        podDbRepository.remove(currentPod);
    }

    private void upload(final POD currentPod) {
        ImageUploadCb imageUploadCb = new ImageUploadCb() {
            @Override
            public void onUploadSuccess() {
                PODFileRepository.deleteImageFile(currentPod.getImageFilePath());
                remove(currentPod);
            }

            @Override
            public void onUploadFailure() {
            }
        };
        podNetworkRepository.uploadImageFile(currentPod, imageUploadCb);
    }

    private Observer<PodNetworkUploadStatus> getNetworkObserver() {
        return new Observer<PodNetworkUploadStatus>() {
            @Override
            public void onChanged(PodNetworkUploadStatus nwStatusMap) {
                final PodViewItemsList value = podViewItems.getValue();
                ArrayList<POD> pods = value != null ? value.getAll() : new ArrayList<POD>();
                for(POD l: pods)
                    l.setUploadStatus(nwStatusMap.getStatus(l.getImageFilePath()));
                podViewItems.setValue(new PodViewItemsList(pods));
            }
        };
    }

    private Observer<List<LocalPOD>> getDbObserver() {
        return new Observer<List<LocalPOD>>() {
            @Override
            public void onChanged(List<LocalPOD> input) {
                ArrayList<POD> pods = new ArrayList<>();
                for(LocalPOD l: input)
                    pods.add(new POD(l.imageFilePath, l.lrNo, podNetworkRepository.getStatus(l.imageFilePath)));
                podViewItems.setValue(new PodViewItemsList(pods));
            }
        };
    }
}
