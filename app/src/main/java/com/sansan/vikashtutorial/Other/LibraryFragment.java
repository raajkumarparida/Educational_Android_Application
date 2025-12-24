package com.sansan.vikashtutorial.Other;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.sansan.vikashtutorial.Adapter.LibraryPagerAdapter;
import com.sansan.vikashtutorial.R;

public class LibraryFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private LibraryPagerAdapter pagerAdapter;

    public LibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        Animation top_to_btm = AnimationUtils.loadAnimation(getActivity(), R.anim.top_to_btm);
        View topView = view.findViewById(R.id.topCircle);
        topView.setAnimation(top_to_btm);

        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);
        pagerAdapter = new LibraryPagerAdapter(getActivity());
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("CHSE");
                            break;
                        case 1:
                            tab.setText("CBSE");
                            break;
                        case 2:
                            tab.setText("JEE");
                            break;
                        case 3:
                            tab.setText("NEET");
                            break;
                        case 4:
                            tab.setText("OUAT");
                            break;
                    }
                }).attach();
        return view;
    }
}