package io.innofang.parents;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.alibaba.android.arouter.facade.annotation.Route;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.innofang.base.util.common.BottomNavigationViewHelper;
import io.innofang.parents.communication.CommunicationFragment;
import io.innofang.parents.home.HomeFragment;
import io.innofang.parents.medically_exam.MedicallyExamFragment;
import io.innofang.parents.settings.SettingsFragment;
import io.innofang.parents.sms.SmsFragment;

@Route(path = "/parents/1")
public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R2.id.view_pager)
    ViewPager mViewPager;
    @BindView(R2.id.bottom_navigation_view)
    BottomNavigationView mBottomNavigationView;

    private MenuItem mMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        BottomNavigationViewHelper.disableShiftMode(mBottomNavigationView);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (null != mMenuItem) {
                    mMenuItem.setChecked(false);
                } else {
                    mBottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                mMenuItem = mBottomNavigationView.getMenu().getItem(position);
                mMenuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        List<Fragment> list = new ArrayList<>();
        list.add(SmsFragment.newInstance());
        list.add(MedicallyExamFragment.newInstance());
        list.add(HomeFragment.newInstance());
        list.add(CommunicationFragment.newInstance());
        list.add(SettingsFragment.newInstance());
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), list);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(2);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R2.id.item_sms:
                mViewPager.setCurrentItem(0);
                break;
            case R2.id.item_medically_exam:
                mViewPager.setCurrentItem(1);
                break;
            case R2.id.item_home:
                mViewPager.setCurrentItem(2);
                break;
            case R2.id.item_communication:
                mViewPager.setCurrentItem(3);
                break;
            case R2.id.item_settings:
                mViewPager.setCurrentItem(4);
                break;
            default:
                break;
        }
        return false;
    }
}
