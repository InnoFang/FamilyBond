package io.innofang.children.option;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.innofang.base.base.BaseActivity;
import io.innofang.base.widget.card_view_pager.ShadowTransformer;
import io.innofang.children.R;
import io.innofang.children.voice_reminder.VoiceReminderActivity;


public class OptionActivity extends BaseActivity {


    @BindView(R.id.card_view_pager)
    ViewPager mCardViewPager;
    @BindView(R.id.option_fab)
    FloatingActionButton mOptionFab;

    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        mCardAdapter = new CardPagerAdapter();
        mCardAdapter.addCardItem(new CardItem(R.string.card_medically_exam, R.drawable.medically_exam));
        mCardAdapter.addCardItem(new CardItem(R.string.voice_reminder, R.drawable.voice_reminder));
        mCardAdapter.addCardItem(new CardItem(R.string.card_communication, R.drawable.communication));
        mCardAdapter.addCardItem(new CardItem(R.string.card_settings, R.drawable.settings));

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
                        toast(" 体检");
                        break;
                    case 1:
                        startActivity(new Intent(OptionActivity.this, VoiceReminderActivity.class));
                        break;
                    case 2:
                        toast("与父母交流");
                        break;
                    case 3:
                        toast("设置");
                        break;
                }
            }
        });
    }

    @OnClick(R.id.option_fab)
    public void onViewClicked() {
        finish();
    }
}