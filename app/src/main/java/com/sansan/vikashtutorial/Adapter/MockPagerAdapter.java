package com.sansan.vikashtutorial.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.sansan.vikashtutorial.BlankFragment;
import com.sansan.vikashtutorial.Mock.BotanyMockFragment;
import com.sansan.vikashtutorial.Mock.ChemistryMockFragment;
import com.sansan.vikashtutorial.Mock.EnglishMockFragment;
import com.sansan.vikashtutorial.Mock.MathMockFragment;
import com.sansan.vikashtutorial.Mock.OriyaMockFragment;
import com.sansan.vikashtutorial.Mock.PhysicsMockFragment;
import com.sansan.vikashtutorial.Mock.ZoologyMockFragment;


public class MockPagerAdapter extends FragmentStateAdapter {
    public MockPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 6:
                return new ZoologyMockFragment();
            case 5:
                return new BotanyMockFragment();
            case 4:
                return new MathMockFragment();
            case 3:
                return new ChemistryMockFragment();
            case 2:
                return new PhysicsMockFragment();
            case 1:
                return new EnglishMockFragment();
            case 0:
            default:
                return new OriyaMockFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 7;
    }
}
