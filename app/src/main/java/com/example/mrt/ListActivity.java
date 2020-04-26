package com.example.mrt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mrt.models.POD;
import com.example.mrt.models.PODList;
import com.example.mrt.models.PODListViewModel;
import com.example.mrt.models.UploadStatus;

import java.util.ArrayList;


public class ListActivity extends AppCompatActivity {
    private PODListViewModel podViewModel;
    public static final int REQUEST_ADD_POD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        podViewModel = ViewModelProviders.of(this).get(PODListViewModel.class);

        final ListView listView = findViewById(R.id.pod_list);
        final ArrayAdapter<POD> adapter = new PODArrayAdapter(this, new ArrayList<POD>());
        listView.setAdapter(adapter);

        Observer<PODList> observer = new Observer<PODList>() {
            @Override
            public void onChanged(PODList podList) {
                adapter.clear();
                adapter.addAll(podList.getAll());
                adapter.notifyDataSetChanged();
            }
        };
        podViewModel.getPOD().observe(this, observer);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    public void onAddPOD(View view) {
        Intent uploadPODIntent = new Intent(this, CreatePODActivity.class);
        startActivityForResult(uploadPODIntent, REQUEST_ADD_POD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        final String lrNo = data.getStringExtra(CreatePODActivity.LR_NO_EXTRA);
        final String imageFilePath = data.getStringExtra(CreatePODActivity.IMAGE_FILE_PATH_EXTRA);
        final POD pod = new POD(imageFilePath, lrNo, UploadStatus.WAITING);
        podViewModel.add(pod);
    }
}
