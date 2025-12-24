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
import com.sansan.vikashtutorial.Adapter.CoursePagerAdapter;
import com.sansan.vikashtutorial.R;

public class CourseFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private CoursePagerAdapter pagerAdapter;

    public CourseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course, container, false);

        Animation top_to_btm = AnimationUtils.loadAnimation(getActivity(), R.anim.top_to_btm);
        Animation btm_to_top = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_animation);
        View topView = view.findViewById(R.id.topCircle);
        topView.setAnimation(top_to_btm);

        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAnimation(btm_to_top);
        pagerAdapter = new CoursePagerAdapter(getActivity());
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Oriya");
                            break;
                        case 1:
                            tab.setText("English");
                            break;
                        case 2:
                            tab.setText("Physics");
                            break;
                        case 3:
                            tab.setText("Chemistry");
                            break;
                        case 4:
                            tab.setText("Mathematics");
                            break;
                        case 5:
                            tab.setText("Botany");
                            break;
                        case 6:
                            tab.setText("Zoology");
                            break;

                    }
                }).attach();

        return view;
    }
}