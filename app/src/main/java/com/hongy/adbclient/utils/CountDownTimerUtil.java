package com.hongy.adbclient.utils;

import android.os.CountDownTimer;
import android.widget.TextView;

/**
 * 倒计时工具类
 * Created by blw on 2016/8/30.
 */
public class CountDownTimerUtil extends CountDownTimer{
    //请求码
    private int requestCode;
    private TextView mTextView;
    public CountDownTimerUtil(int requestCode, TextView textView, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.mTextView = textView;
        this.requestCode=requestCode;
    }
    //开始计时过程
    @Override
    public void onTick(long millisUntilFinished) {
        mTextView.setText(millisUntilFinished / 1000 + "S");  //设置倒计时时间
        mTextView.setEnabled(false);
    }
    //计时结束
    @Override
    public void onFinish() {
        switch (requestCode){
            case 0:
                mTextView.setText("重新发送");
                break;
            case 1:
                mTextView.setText("发送");
                break;
        }
        mTextView.setEnabled(true);
    }
}
