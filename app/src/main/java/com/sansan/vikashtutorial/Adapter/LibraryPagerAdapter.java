package com.sansan.vikashtutorial.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.sansan.vikashtutorial.CBSEFragment;
import com.sansan.vikashtutorial.CHSEFragment;
import com.sansan.vikashtutorial.JEEFragment;
import com.sansan.vikashtutorial.Library.BotanyLibraryFragment;
import com.sansan.vikashtutorial.Library.ChemistryLibraryFragment;
import com.sansan.vikashtutorial.Library.EnglishLibraryFragment;
import com.sansan.vikashtutorial.Library.MathLibraryFragment;
import com.sansan.vikashtutorial.Library.OriyaLibraryFragment;
import com.sansan.vikashtutorial.Library.PhysicsLibraryFragment;
import com.sansan.vikashtutorial.Library.ZoologyLibraryFragment;
import com.sansan.vikashtutorial.NEETFragment;
import com.sansan.vikashtutorial.OUATFragment;

public class LibraryPagerAdapter extends FragmentStateAdapter {


    public LibraryPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 4:
                return new OUATFragment();
            case 3:
                return new NEETFragment();
            case 2:
                return new JEEFragment();
            case 1:
                return new CBSEFragment();
            case 0:
            default:
                return new CHSEFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
