package com.sansan.vikashtutorial.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.sansan.vikashtutorial.Mock.MockDownloadedFragment;
import com.sansan.vikashtutorial.Pdf.PdfDownloadedFragment;
import com.sansan.vikashtutorial.Video.VideoDownloadedFragment;

public class BookmarksPagerAdapter extends FragmentStateAdapter {
    public BookmarksPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 2:
                return new MockDownloadedFragment();
            case 1:
                return new VideoDownloadedFragment();
            case 0:
            default:
                return new PdfDownloadedFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
