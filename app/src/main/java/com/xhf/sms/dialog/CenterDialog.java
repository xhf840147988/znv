package com.xhf.sms.dialog;

import android.view.Gravity;
import android.view.View;

import com.xhf.sms.base.BaseDialog;

import androidx.annotation.LayoutRes;
import androidx.fragment.app.FragmentManager;


public class CenterDialog extends BaseDialog {

    private FragmentManager mFragmentManager;

    private float mDimAmount = super.getDimAmount();

    @LayoutRes
    private int mLayoutRes;

    private int mGravity = Gravity.CENTER;

    private ViewListener mViewListener;


    public static CenterDialog create(FragmentManager manager) {
        CenterDialog dialog = new CenterDialog();
        dialog.setFragmentManager(manager);
        return dialog;
    }

    @Override
    public int getLayoutRes() {
        return mLayoutRes;
    }

    @Override
    public int getGravity() {
        return mGravity;
    }

    @Override
    public void bindView(View v) {
        if (mViewListener != null) {
            mViewListener.bindView(v);
        }
    }

    public CenterDialog setFragmentManager(FragmentManager manager) {
        mFragmentManager = manager;
        return this;
    }

    public CenterDialog setViewListener(ViewListener listener) {
        mViewListener = listener;
        return this;
    }

    public CenterDialog setLayoutRes(@LayoutRes int layoutRes) {
        mLayoutRes = layoutRes;
        return this;
    }


    public CenterDialog setDimAmount(float dim) {
        mDimAmount = dim;
        return this;
    }

    public CenterDialog setViewGravity(int gravity) {
        mGravity = gravity;
        return this;
    }


    @Override
    public float getDimAmount() {
        return mDimAmount;
    }


    public interface ViewListener {
        void bindView(View v);
    }

    public BaseDialog show() {
        show(mFragmentManager);
        return this;
    }
}
