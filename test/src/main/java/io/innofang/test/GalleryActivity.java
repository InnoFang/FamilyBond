package io.innofang.test;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import io.innofang.base.widget.gallery_recycler_view.CardScaleHelper;
import io.innofang.base.widget.gallery_recycler_view.GalleryRecyclerView;

public class GalleryActivity extends AppCompatActivity {

    private GalleryRecyclerView mGalleryRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        List<Integer> list = new ArrayList<>();
        list.add(Color.RED);
        list.add(Color.YELLOW);
        list.add(Color.BLUE);
        list.add(Color.GREEN);
        list.add(Color.GRAY);

        mGalleryRecyclerView = (GalleryRecyclerView) findViewById(R.id.gallery_recycler_view);
        mGalleryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mGalleryRecyclerView.setAdapter(new CardAdapter(list)/*new SimpleAdapter<Integer>(this, R.layout.layout_gallery_item, list) {

            @Override
            protected void bindViewHolder(SimpleViewHolder viewHolder, Integer integer, int position) {
                viewHolder.setImageBackgroundColor(R.id.image, integer);
            }
        }*/);

        CardScaleHelper cardScaleHelper = new CardScaleHelper();
        cardScaleHelper.setCurrentItemPos(2);
        cardScaleHelper.attachToRecyclerView(mGalleryRecyclerView);
    }
}
