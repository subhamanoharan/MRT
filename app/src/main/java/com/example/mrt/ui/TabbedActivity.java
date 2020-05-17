package com.example.mrt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mrt.R;
import com.example.mrt.models.POD;
import com.example.mrt.ui.adapters.ViewPagerAdapter;
import com.example.mrt.ui.viewmodels.PODListViewModel;
import com.example.mrt.models.UploadStatus;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TabbedActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private PODListViewModel podViewModel;
    private static final int MAX_COUNT = 5;
    public static final int REQUEST_ADD_POD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);
        setUpViewPager();
        podViewModel = ViewModelProviders.of(this).get(PODListViewModel.class);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        Observer<POD> failureEventObserver = new Observer<POD>() {
            @Override
            public void onChanged(POD pod) {
                Toast.makeText(TabbedActivity.this, "Failed to upload " + pod.getLrNo(), Toast.LENGTH_LONG).show();
            }
        };
        podViewModel.getFailureEvent().observe(this, failureEventObserver);
    }
    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK &&  requestCode == REQUEST_ADD_POD) {
            final String lrNo = data.getStringExtra(CreatePODActivity.LR_NO_EXTRA);
            final String imageFilePath = data.getStringExtra(CreatePODActivity.IMAGE_FILE_PATH_EXTRA);
            final POD pod = new POD(imageFilePath, lrNo, UploadStatus.WAITING);
            podViewModel.add(pod);
        }
    }

    public void onAddPOD(View view) {
        final int count = podViewModel.getCount();
        if(count >= MAX_COUNT)
            Toast.makeText(this, "Please wait for the current "+count+" items to upload.", Toast.LENGTH_LONG).show();
        else {
            Intent uploadPODIntent = new Intent(this, CreatePODActivity.class);
            startActivityForResult(uploadPODIntent, REQUEST_ADD_POD);
        }
    }

    private void setUpViewPager() {
        viewPager = findViewById(R.id.pager);
        FragmentStateAdapter pagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.OnConfigureTabCallback() {
                    @Override public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(position == 0 ? "Pending" : "Completed");
                    }
                }).attach();
    }
}
