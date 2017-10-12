package io.innofang.children;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.innofang.base.base.BaseActivity;
import io.innofang.base.util.common.RequestPermissions;
import io.innofang.base.widget.card_view_pager.ShadowTransformer;
import io.innofang.children.reminder.ReminderActivity;
import io.innofang.children.settings.SettingsActivity;
import io.innofang.medically.heat_beat.HeartBeatActivity;


public class ChildrenActivity extends BaseActivity {


    @BindView(R2.id.card_view_pager)
    ViewPager mCardViewPager;

    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        mCardAdapter = new CardPagerAdapter();
        mCardAdapter.addCardItem(new CardItem(R.string.card_medically_exam, R.drawable.medically_exam));
        mCardAdapter.addCardItem(new CardItem(R.string.message_reminder, R.drawable.voice_reminder));
        mCardAdapter.addCardItem(new CardItem(R.string.card_communication, R.drawable.communication));
        mCardAdapter.addCardItem(new CardItem(R.string.common_settings, R.drawable.settings));

        mCardShadowTransformer = new ShadowTransformer(mCardViewPager, mCardAdapter);
        mCardShadowTransformer.enableScaling(true);
        mCardViewPager.setAdapter(mCardAdapter);
        mCardViewPager.setPageTransformer(false, mCardShadowTransformer);
        mCardViewPager.setOffscreenPageLimit(3);
        mCardViewPager.setCurrentItem(1);
        mCardAdapter.setOnItemClickListener(new CardPagerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        RequestPermissions.requestRuntimePermission(
                                new String[]{Manifest.permission.CAMERA}, new RequestPermissions.OnRequestPermissionsListener() {
                                    @Override
                                    public void onGranted() {
//                                        ARouter.getInstance().build("/heart_beat/1").navigation();
                                        startActivity(new Intent(ChildrenActivity.this, HeartBeatActivity.class));
                                    }

                                    @Override
                                    public void onDenied(List<String> deniedPermission) {

                                    }
                                }
                        );
                        break;
                    case 1:
                        startActivity(new Intent(ChildrenActivity.this, ReminderActivity.class));
                        break;
                    case 2:
                        toast("与父母交流");
                        break;
                    case 3:
                        startActivity(new Intent(ChildrenActivity.this, SettingsActivity.class));
                        break;
                }
            }
        });
    }
}
