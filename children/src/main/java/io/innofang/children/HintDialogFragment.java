package io.innofang.children;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

/**
 * Author: Inno Fang
 * Time: 2017/9/15 18:35
 * Description:
 */

public class HintDialogFragment extends DialogFragment {

    public static final String TITLE = "title";
    public static final String MESSAGE = "message";
    public static final String REQUEST_CODE = "request_code";

    public static HintDialogFragment newInstance(int title, int message, int requestCode) {
        HintDialogFragment frag = new HintDialogFragment();
        Bundle args = new Bundle();
        args.putInt(TITLE, title);
        args.putInt(MESSAGE, message);
        args.putInt(REQUEST_CODE, requestCode);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt(TITLE);
        int message = getArguments().getInt(MESSAGE);
        final int requestCode = getArguments().getInt(REQUEST_CODE);

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((DialogFragmentCallback) getActivity()).doPositiveClick(requestCode);
                            }
                        }
                )
                .setNegativeButton(R.string.alert_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((DialogFragmentCallback) getActivity()).doNegativeClick(requestCode);
                            }
                        }
                )
                .create();
    }

    public interface DialogFragmentCallback {

        void doPositiveClick(int requestCode);

        void doNegativeClick(int requestCode);
    }

}