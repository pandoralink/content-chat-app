package com.example.newslist.popup;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.newslist.NewsAdapter;
import com.example.newslist.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * @author 庞旺
 */
public class OperationDialogFragment extends BottomSheetDialogFragment {
    private Dialog dialog;
    View rootView;
    public String articleUrl;
    private String TAG = "PW";
    private OnNotLikeClickListener onNotLikeClickListener;
    public int itemIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.operationDialogFragment);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = super.onCreateDialog(savedInstanceState);
        //设置点击外部可消失
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 在这里将view的高度设置为精确高度，即可屏蔽向上滑动不占全屏的手势。如果不设置高度的话 会默认向上滑动时dialog覆盖全屏
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_operation_dialog, container, false);
        }
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 360));

        RadioButton rbNews = rootView.findViewById(R.id.rb_news);
        RadioButton rbNotLike = rootView.findViewById(R.id.rb_not_like);

        rbNews.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(null, articleUrl);
            clipboard.setPrimaryClip(clipData);
        });
        rbNotLike.setOnClickListener(v -> {
            if(onNotLikeClickListener != null) {
                this.onNotLikeClickListener.onClick(itemIndex);
            }
            this.dismiss();
        });
        return rootView;
    }

    public interface OnNotLikeClickListener {
        /**
         * 用户点击不感兴趣
         * @param position
         */
        void onClick(int position);
    }

    public void setOnNotLikeClickListener(OnNotLikeClickListener onNotLikeClickListener) {
        this.onNotLikeClickListener = onNotLikeClickListener;
    }
}