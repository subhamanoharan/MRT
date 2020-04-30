package com.example.mrt.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    final int COUNT = 2;

    public ViewPagerAdapter(FragmentActivity fa) {
        super(fa);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? new PODListFragment() : new PODSuccessListFragment();
    }

    @Override
    public int getItemCount() {
        return COUNT;
    }
}
