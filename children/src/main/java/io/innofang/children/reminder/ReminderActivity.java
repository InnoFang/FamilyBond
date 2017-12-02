
package io.innofang.children.reminder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import io.innofang.base.base.BaseActivity;
import io.innofang.children.R;
import io.innofang.children.reminder.text_reminder.TextReminderFragment;
import io.innofang.children.reminder.voice_reminder.VoiceReminderFragment;

/**
 * Author: Inno Fang
 * Time: 2017/9/23 11:06
 * Description:
 */


public class ReminderActivity extends BaseActivity {

    TabLayout mReminderTabLayout;
    ViewPager mReminderViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        mReminderTabLayout = (TabLayout) findViewById(R.id.reminder_tab_layout);
        mReminderViewPager = (ViewPager) findViewById(R.id.reminder_view_pager);

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
