package pushit.pushit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by Marcin on 2015-07-08.
 */
public class BSAdapter extends android.support.v4.app.FragmentPagerAdapter {
    static final int NUM_ITEMS = 10;

    Bundle data;

    public BSAdapter(FragmentManager fm, Bundle data) {
        super(fm);
        this.data = data;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public Fragment getItem(int position) {

        return RecentsFragment.newInstance(data);
    }
}