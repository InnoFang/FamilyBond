package io.innofang.medically.data_display;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import io.innofang.base.bean.greendao.Bpm;
import io.innofang.base.widget.base_adapter.BaseViewHolder;
import io.innofang.medically.R;

/**
 * Author: Inno Fang
 * Time: 2017/10/4 17:52
 * Description:
 */


public class DataDisplayViewHolder extends BaseViewHolder<Bpm> {

    private ImageView mStateImageView;
    private TextView mBpmTextView;
    private TextView mDescriptionTextView;
    private TextView mTimeTextView;

    public DataDisplayViewHolder(View itemView) {
        super(itemView);
        mStateImageView = (ImageView) find(R.id.state_image_view);
        mBpmTextView = (TextView) find(R.id.bpm_text_view);
        mDescriptionTextView = (TextView) find(R.id.description_text_view);
        mTimeTextView = (TextView) find(R.id.time_text_view);
    }

    @Override
    public void bindHolder(Bpm model) {
        int value = Integer.parseInt(model.getBpm());
        if (value >= 60 && value <= 90) {
            mStateImageView.setImageResource(R.drawable.state_normal);
            mDescriptionTextView.setText("继续保持");
        } else if (value < 60) {
            mStateImageView.setImageResource(R.drawable.state_low);
            mDescriptionTextView.setText("勤加锻炼");
        } else if (value > 90) {
            mStateImageView.setImageResource(R.drawable.state_high);
            mDescriptionTextView.setText("注意休息");
        }

        mBpmTextView.setText(String.format("%sbpm ", model.getBpm()));
        mTimeTextView.setText(model.getTime());
    }
}
