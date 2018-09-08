package gism.com.gism.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import gism.com.gism.fragment.AppsFragment;
import gism.com.gism.fragment.ChatsFragment;
import gism.com.gism.fragment.FeedFragment;

/**
 * Created by Sagar on 4/4/2017.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ChatsFragment();
            case 1:
                return new FeedFragment();
            case 2:
                return new AppsFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
