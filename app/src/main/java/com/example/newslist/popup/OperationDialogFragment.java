package com.example.newslist.popup;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.newslist.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * @author 庞旺
 */
public class OperationDialogFragment extends BottomSheetDialogFragment {
    private Dialog dialog;
    View rootView;

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        dialog = super.onCreateDialog(savedInstanceState);
//        dialog.setCanceledOnTouchOutside(true);//设置点击外部可消失
//        Window win = dialog.getWindow();
//        WindowManager.LayoutParams params = win.getAttributes();
//        win.setSoftInputMode(params.SOFT_INPUT_ADJUST_NOTHING);//设置使软键盘弹出的时候dialog不会被顶起
//        win.setWindowAnimations(R.style.Anim_Dialog_Bottom);//这里设置dialog的进出动画
//        return dialog;
//    }

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
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getScreenHeight(getActivity()) * 2 / 3));
        return rootView;
    }


    /**
     * 得到屏幕的高
     * @param context
     */
    public static int getScreenHeight(FragmentActivity context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        return height;
    }
}