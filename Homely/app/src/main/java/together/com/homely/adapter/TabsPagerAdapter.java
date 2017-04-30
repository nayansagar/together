package together.com.homely.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import together.com.homely.fragments.AppsFragment;
import together.com.homely.fragments.ChatsFragment;
import together.com.homely.fragments.FeedFragment;

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
                return new FeedFragment();
            case 1:
                return new ChatsFragment();
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
