package com.example.mrt.db;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.mrt.models.POD;

import java.util.List;

public class PodRepository {
    private PodDao podDao;
    private LiveData<List<LocalPOD>> pods;

    public PodRepository(Application application) {
        PodDatabase db = PodDatabase.getDatabase(application);
        podDao = db.podDao();
        pods = podDao.getAll();
    }

    public LiveData<List<LocalPOD>> getAll() {
        return pods;
    }

    public void add(final POD currentPod) {
        PodDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                podDao.add(new LocalPOD(currentPod.getImageFilePath(), currentPod.getLrNo()));
            }
        });
    }

    public void remove(POD currentPod) {
        podDao.delete(new LocalPOD(currentPod.getImageFilePath(), currentPod.getLrNo()));
    }
}
