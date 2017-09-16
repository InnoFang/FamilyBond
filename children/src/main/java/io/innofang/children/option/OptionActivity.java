package io.innofang.children.option;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.innofang.base.base.BaseActivity;
import io.innofang.base.widget.card_view_pager.ShadowTransformer;
import io.innofang.children.R;

@Route(path = "/children/1")
public class OptionActivity extends BaseActivity {


    @BindView(R.id.card_view_pager)
    ViewPager mCardViewPager;

    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      /*  final View content = findViewById(android.R.id.content).getRootView();
        if (content.getWidth() > 0) {
            Bitmap image = BlurBuilder.blur(content);
            getWindow().setBackgroundDrawable(new BitmapDrawable(getResources(), image));
        } else {
            content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Bitmap image = BlurBuilder.blur(content);
                    getWindow().setBackgroundDrawable(new BitmapDrawable(getResources(), image));
                }
            });
        }*/

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
        mCardAdapter.setOnItemClickListener(new CardPagerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        toast(" 体检");
                        break;
                    case 1:
                        toast("给父母设置任务提醒");
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

}
