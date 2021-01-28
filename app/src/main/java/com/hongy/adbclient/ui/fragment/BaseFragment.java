package com.hongy.adbclient.ui.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.hongy.adbclient.utils.L;
import java.util.ArrayList;
import rx.Subscription;

public abstract class BaseFragment extends Fragment {

    public ArrayList<Subscription> rxBusList = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.i("basefragment onCreate");
    }

    @Override
    public void onPause() {
        super.onPause();
        L.i("basefragment onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        L.i("basefragment onDestroy");
        clearSubscription();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        L.i("basefragment onHiddenChanged:"+hidden);
    }

    /**
     * 取消该页面所有订阅
     */
    private void clearSubscription() {
        for (Subscription subscription : rxBusList) {
            if (subscription != null && subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
        }
    }

}
