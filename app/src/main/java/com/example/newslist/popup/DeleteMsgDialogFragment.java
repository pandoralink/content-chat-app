package com.example.newslist.popup;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

public class DeleteMsgDialogFragment extends DialogFragment {
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

    public DeleteMsgDialogFragment(int index) {
        this.index = index;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("是否删除此对话")
                .setPositiveButton("确定", (dialog, id) -> {
                    if (listener != null) {
                        this.listener.onDialogPositiveClick(DeleteMsgDialogFragment.this, index);
                    }
                })
                .setNegativeButton("取消", (dialog, id) -> listener.onDialogNegativeClick(DeleteMsgDialogFragment.this));
        return builder.create();
    }

    public void setOnNoticeDialogListener(NoticeDialogListener noticeDialogListener) {
        this.listener = noticeDialogListener;
    }
}