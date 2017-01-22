package moe.codeest.ecardflowdemo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import moe.codeest.ecardflowdemo.fragment.PagerFragment;

/**
 * Created by codeest on 2017/1/8.
 */

public class PagerAdapter extends FragmentPagerAdapter{

    private List<PagerFragment> mList;

    public PagerAdapter(FragmentManager fm, List<PagerFragment> mList) {
        super(fm);
        this.mList = mList;
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }
}
