package com.hongy.adbclient.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hongy.adbclient.R;
import com.hongy.adbclient.utils.ActivityCollector;
import com.hongy.adbclient.utils.StatusBarUtil;
import com.hyb.library.PreventKeyboardBlockUtil;

public abstract class BaseActivity extends AppCompatActivity {

    private boolean isShowTitle = true;

    private boolean isShowStatusBar = false;

    private boolean isFullScreen = false;

    public Context context;

    private ActivityCollector activityCollector = ActivityCollector.getAppManager();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        //activity管理
        activityCollector.addActivity(this);
        if (isFullScreen){
            StatusBarUtil.setTransparent(this);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
        setContentView(initLayout());
        initView();
        initData();
    }

    protected abstract int initLayout();

    protected abstract void initView();

    protected abstract void initData();

    public void setFullScreen(boolean fullScreen) {
        isFullScreen = fullScreen;
    }

    /**
     * 保证同一按钮在1秒内只会响应一次点击事件
     */
    public abstract class OnSingleClickListener implements View.OnClickListener {
        //两次点击按钮之间的间隔，目前为1000ms
        private static final int MIN_CLICK_DELAY_TIME = 1000;
        private long lastClickTime;

        public abstract void onSingleClick(View view);

        @Override
        public void onClick(View view) {
            long curClickTime = System.currentTimeMillis();
            if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
                lastClickTime = curClickTime;
                onSingleClick(view);
            }
        }
    }

    /**
     * 同一按钮在短时间内可重复响应点击事件
     */
    public abstract class OnMultiClickListener implements View.OnClickListener {
        public abstract void onMultiClick(View view);

        @Override
        public void onClick(View v) {
            onMultiClick(v);
        }
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null && null != imm) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * 显示软键盘
     */
    public void showSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null && null != imm) {
            imm.showSoftInputFromInputMethod(getCurrentFocus().getWindowToken(), 0);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //activity管理
        activityCollector.finishActivity(this);
    }
}
