package io.innofang.children.reminder;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Inno Fang
 * Time: 2017/12/2 11:13
 * Description:
 */


public class ReminderViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragmentList = new ArrayList<>();
    private String[] titles;

    public ReminderViewPagerAdapter(FragmentManager fm, String[] titles) {
        super(fm);
        this.titles = titles;
    }

    public void addFragment(Fragment fragment) {
        mFragmentList.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
