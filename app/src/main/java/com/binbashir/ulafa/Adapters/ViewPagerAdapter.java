package com.binbashir.ulafa.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.binbashir.ulafa.R;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final List<Fragment> listFragment = new ArrayList<>();

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_lost, R.string.tab_text_found,
    };
// private final Context mContext;

    private final ArrayList<Fragment> arrayList = new ArrayList<>();

    public ViewPagerAdapter(FragmentActivity fa) {
        super(fa);
    }

    public void addFragment(Fragment fragment) {
        arrayList.add(fragment);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // return your fragment that corresponds to this 'position'
        return arrayList.get(position);
    }
}
