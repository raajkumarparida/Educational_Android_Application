package com.sansan.vikashtutorial.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.sansan.vikashtutorial.Lecture.BotanyLectureFragment;
import com.sansan.vikashtutorial.Lecture.ChemistryLectureFragment;
import com.sansan.vikashtutorial.Lecture.EnglishLectureFragment;
import com.sansan.vikashtutorial.Lecture.MathLectureFragment;
import com.sansan.vikashtutorial.Lecture.OriyaLectureFragment;
import com.sansan.vikashtutorial.Lecture.PhysicsLectureFragment;
import com.sansan.vikashtutorial.Lecture.ZoologyLectureFragment;

public class CoursePagerAdapter extends FragmentStateAdapter {
    public CoursePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 7:
                return new ZoologyLectureFragment();
            case 6:
                return new BotanyLectureFragment();
            case 5:
                return new MathLectureFragment();
            case 4:
                return new ChemistryLectureFragment();
            case 2:
                return new PhysicsLectureFragment();
            case 1:
                return new EnglishLectureFragment();
            case 0:
            default:
                return new OriyaLectureFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 7;
    }
}
