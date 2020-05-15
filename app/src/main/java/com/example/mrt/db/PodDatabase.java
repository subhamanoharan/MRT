package com.example.mrt.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {LocalPOD.class}, version = 1, exportSchema = false)
public abstract class PodDatabase extends RoomDatabase {

    public abstract PodDao podDao();

    private static volatile PodDatabase INSTANCE;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(2);
    public static PodDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (PodDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            PodDatabase.class, "pod_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}