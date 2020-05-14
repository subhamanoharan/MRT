package com.example.mrt.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PodDao {

    @Insert
    void add(LocalPOD pod);

    @Delete
    void delete(LocalPOD pod);

    @Query("SELECT * from pods")
    LiveData<List<LocalPOD>> getAll();

}
