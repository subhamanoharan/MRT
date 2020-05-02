package com.example.mrt.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.mrt.ui.PODListFragment;
import com.example.mrt.ui.PODSuccessListFragment;

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
