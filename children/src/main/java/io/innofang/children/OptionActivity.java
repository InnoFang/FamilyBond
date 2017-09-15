package io.innofang.children;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.innofang.base.base.BaseActivity;
import io.innofang.base.widget.card_view_pager.ShadowTransformer;

@Route(path = "/children/1")
public class OptionActivity extends BaseActivity {


    @BindView(R.id.card_view_pager)
    ViewPager mCardViewPager;

    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        ButterKnife.bind(this);

        mCardAdapter = new CardPagerAdapter();
        mCardAdapter.addCardItem(new CardItem(R.string.card_medically_exam, R.drawable.medically_exam));
        mCardAdapter.addCardItem(new CardItem(R.string.card_voice_reminder, R.drawable.voice_reminder));
        mCardAdapter.addCardItem(new CardItem(R.string.card_communication, R.drawable.communication));
        mCardAdapter.addCardItem(new CardItem(R.string.card_settings, R.drawable.settings));

        mCardShadowTransformer = new ShadowTransformer(mCardViewPager, mCardAdapter);
        mCardShadowTransformer.enableScaling(true);
        mCardViewPager.setAdapter(mCardAdapter);
        mCardViewPager.setPageTransformer(false, mCardShadowTransformer);
        mCardViewPager.setOffscreenPageLimit(3);
        mCardViewPager.setCurrentItem(1);
        mCardViewPager.setOnTouchListener(new View.OnTouchListener() {
            int clickable = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        clickable = 0;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        clickable = 1;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (clickable == 0) {
                            int item = mCardViewPager.getCurrentItem();
                            if (item == 0) {
                                toast("体检");
                            } else if (item == 1) {
                                toast("给父母设置任务提醒");
                            } else if (item == 2) {
                                toast("与父母交流");
                            } else if (item == 3) {
                                toast("设置");
                            }
                        }
                        break;
                }
                return false;
            }
        });
    }

}
