
package io.innofang.children.reminder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.nuc.studies.ReminderViewPagerAdapter;
import io.innofang.base.base.BaseActivity;
import io.innofang.children.R;
import io.innofang.children.R2;
import io.innofang.children.reminder.text_reminder.TextReminderFragment;
import io.innofang.children.reminder.voice_reminder.VoiceReminderFragment;

/**
 * Author: Inno Fang
 * Time: 2017/9/23 11:06
 * Description:
 */


public class ReminderActivity extends BaseActivity {

    @BindView(R2.id.reminder_tab_layout)
    TabLayout mReminderTabLayout;
    @BindView(R2.id.reminder_view_pager)
    ViewPager mReminderViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        ButterKnife.bind(this);

        String[] titles = {
                getString(R.string.voice_reminder),
                getString(R.string.text_reminder)
        };

        ReminderViewPagerAdapter adapter = new ReminderViewPagerAdapter(getSupportFragmentManager(), titles);
        adapter.addFragment(VoiceReminderFragment.newInstance());
        adapter.addFragment(TextReminderFragment.newInstance());

        mReminderViewPager.setAdapter(adapter);
        mReminderTabLayout.setupWithViewPager(mReminderViewPager);
    }
}
