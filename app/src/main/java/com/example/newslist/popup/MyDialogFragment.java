package com.example.newslist.popup;

import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class MyDialogFragment extends DialogFragment {
    private final String msg;
    private int index;

    public interface NoticeDialogListener {
        /**
         * 确认删除对话
         *
         * @param dialog
         * @param index
         */
        public void onDialogPositiveClick(DialogFragment dialog, int index);

        /**
         * 取消删除对话
         *
         * @param dialog
         */
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    NoticeDialogListener listener;

    /**
     * @param index 删除数据索引
     * @param msg   dialog 提示框
     */
    public MyDialogFragment(int index, String msg) {
        this.index = index;
        this.msg = msg;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(msg)
                .setPositiveButton("确定", (dialog, id) -> {
                    if (listener != null) {
                        this.listener.onDialogPositiveClick(this, index);
                    }
                })
                .setNegativeButton("取消", (dialog, id) -> listener.onDialogNegativeClick(this));
        return builder.create();
    }

    public void setOnNoticeDialogListener(NoticeDialogListener noticeDialogListener) {
        this.listener = noticeDialogListener;
    }
}
