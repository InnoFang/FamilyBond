package io.innofang.children.sms_intercept;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.greendao.query.Query;

import java.text.DecimalFormat;
import java.util.List;

import io.innofang.base.bean.greendao.DaoSession;
import io.innofang.base.bean.greendao.SMS;
import io.innofang.base.bean.greendao.SMSDao;
import io.innofang.base.configure.GreenDaoConfig;
import io.innofang.base.utils.common.L;
import io.innofang.base.utils.event.OnRecyclerItemClickListener;
import io.innofang.base.widget.base_simple_adapter.BaseSimpleAdapter;
import io.innofang.base.widget.base_simple_adapter.BaseSimpleViewHolder;
import io.innofang.children.R;

/**
 * Author: Inno Fang
 * Time: 2017/10/13 16:09
 * Description:
 */


public class SMSInterceptionFragment extends Fragment {

    private List<SMS> mSMSList;
    private DaoSession mDaoSession;
    private SMSDao mSMSDao;
    private DecimalFormat mDecimalFormat = new DecimalFormat("#.##%");
    private BaseSimpleAdapter<SMS> mAdapter;

    public static SMSInterceptionFragment newInstance() {
        return new SMSInterceptionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDaoSession = GreenDaoConfig.getInstance().getDaoSession();
        mSMSDao = mDaoSession.getSMSDao();
        Query<SMS> query = mSMSDao.queryBuilder().orderAsc(SMSDao.Properties.Id).build();
        mSMSList = query.list();
        L.i("SMS Fragment list size: " + mSMSList.size());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sms_interception, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.sms_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        mAdapter = new BaseSimpleAdapter<SMS>(getActivity(), R.layout.item_sms_interception, mSMSList) {

            @Override
            protected void bindViewHolder(BaseSimpleViewHolder viewHolder, SMS sms, int position) {
                viewHolder.setText(R.id.sms_time_text_view, getString(R.string.sms_time, sms.getTime()));
                viewHolder.setText(R.id.sms_address_text_view, getString(R.string.sms_address, sms.getAddress()));
                viewHolder.setText(R.id.sms_content_text_view, getString(R.string.sms_content, sms.getContent()));
                viewHolder.setText(R.id.sms_probability_text_view, getString(R.string.sms_probability, mDecimalFormat.format(sms.getProbability())));
            }
        };

        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(recyclerView){

            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {

            }

            @Override
            public void onItemLongClick(RecyclerView.ViewHolder vh) {
                if (vh instanceof BaseSimpleViewHolder) {
                    BaseSimpleViewHolder viewHolder = (BaseSimpleViewHolder) vh;
                    final SMS sms = (SMS) viewHolder.getModel();
                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setMessage(R.string.sure_to_delete_suspcious_sms)
                            .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mSMSDao.delete(sms);
                                    mAdapter.delItem(sms);
                                    mAdapter.notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }

            }
        });
    }
}
