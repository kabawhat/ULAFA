package com.binbashir.ulafa.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.binbashir.ulafa.Adapters.ViewPagerAdapter;
import com.binbashir.ulafa.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainFragment extends Fragment {


    private NavController navController;

    public MainFragment() {
        // Required empty public constructor
    }


    TabLayout tabs;
    ViewPager2 viewPager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        tabs = view.findViewById(R.id.tab_layout);
       // fab = view.findViewById(R.id.floatingActionButton);
        viewPager = view.findViewById(R.id.view_pager);
        // Inflate the layout for this fragment
        return view;
    }


    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        ViewPagerAdapter viewPagerAdapter =
                new ViewPagerAdapter(getActivity());

        viewPagerAdapter.addFragment(new LostFragment());
        viewPagerAdapter.addFragment(new FoundFragment());
        viewPager.setAdapter(viewPagerAdapter);
        tabNames(tabs, viewPager);

    }




    private void tabNames(TabLayout tabs, ViewPager2 viewPager) {

        new TabLayoutMediator(tabs, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

                        if (position == 0) {
                            tab.setText("Lost");
                        }
                        if (position == 1) {
                            tab.setText("Found");
                        }

                    }
                }).attach();


    }






    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_account:
              navController.navigate(R.id.accountSettingsFragment);
              return true;
        }
        return super.onOptionsItemSelected(item);
    }

}