package io.innofang.parents;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Author: Inno Fang
 * Time: 2017/3/23 21:08
 * Description:
 */


public class ContentFragment extends Fragment {

    private static final String ARGS_TITLE = "io.innofang.parents.title";

    public static ContentFragment newInstance(String title) {
        Bundle args = new Bundle();
        args.putString(ARGS_TITLE, title);
        ContentFragment fragment = new ContentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView textView = new TextView(getContext());
        textView.setText(getArguments().getString(ARGS_TITLE));
        return textView;
    }
}
