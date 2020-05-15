package com.example.mrt.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.mrt.db.LocalPOD;
import com.example.mrt.db.PodDao;
import com.example.mrt.db.PodDatabase;
import com.example.mrt.models.POD;

import java.util.List;

public class PodDbRepository {
    private PodDao podDao;

    public PodDbRepository(Application application) {
        PodDatabase db = PodDatabase.getDatabase(application);
        podDao = db.podDao();
    }

    public void add(final POD currentPod) {
        PodDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
            podDao.add(new LocalPOD(currentPod.getImageFilePath(), currentPod.getLrNo()));
            }
        });
    }

    public void remove(final POD currentPod) {
        PodDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                podDao.delete(new LocalPOD(currentPod.getImageFilePath(), currentPod.getLrNo()));
            }
        });
    }

    public LiveData<List<LocalPOD>> getAll(){
        return podDao.getAll();
    }
}
