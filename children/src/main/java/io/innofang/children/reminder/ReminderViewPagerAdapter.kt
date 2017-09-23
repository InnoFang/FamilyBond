
package cn.edu.nuc.studies

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter



/**
 * Author: Inno Fang
 * Time: 2017/8/15 22:58
 * Description:
 */


class ReminderViewPagerAdapter
constructor(fm: FragmentManager, private val titles: Array<String>) : FragmentPagerAdapter(fm) {

    private val mFragmentList =  java.util.ArrayList<Fragment>()

    fun addFragment(fragment: Fragment) {
        mFragmentList.add(fragment)
    }

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return titles[position]
    }
}