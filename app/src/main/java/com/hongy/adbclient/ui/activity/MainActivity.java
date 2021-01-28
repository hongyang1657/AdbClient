package com.hongy.adbclient.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.hongy.adbclient.R;
import com.hongy.adbclient.adb.AdbDevice;
import com.hongy.adbclient.bean.EventAdbDevice;
import com.hongy.adbclient.ui.fragment.FunctionFragment;
import com.hongy.adbclient.ui.fragment.TerminalFragment;
import com.hongy.adbclient.utils.L;
import com.hongy.adbclient.utils.RxBus;

public class MainActivity extends AdbBaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private NavigationView navigationView;
    private FloatingActionButton floatingActionButton;
    private Toolbar tbToolbar;

    private static final int INDEX_FUNCTION = 0;
    private static final int INDEX_CONTROLLER = 1;
    private static final int INDEX_TERMINAL = 2;
    private SparseArray<Fragment> mFragmentMap = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }


    protected void initView() {
        drawerLayout = findViewById(R.id.draw);
        bottomNavigationView = findViewById(R.id.bnv_bottom_nav);
        navigationView = findViewById(R.id.nv_drawer_nav);
        tbToolbar = findViewById(R.id.tb_toolbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        initToolbar();
        switchFragment(INDEX_FUNCTION);
    }

    private void initToolbar() {
        setSupportActionBar(tbToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(R.string.function);
    }

    @SuppressLint("WrongConstant")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Open the navigation drawer when the home icon is selected from the toolbar.
                drawerLayout.openDrawer(Gravity.START);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.tab_function:
                tbToolbar.setTitle(R.string.function);
                switchFragment(INDEX_FUNCTION);
                Log.i("debug_message", "tab_function: ");
                break;
            case R.id.tab_control:
                tbToolbar.setTitle(R.string.control);
                switchFragment(INDEX_CONTROLLER);
                Log.i("debug_message", "tab_control: ");
                break;
            case R.id.tab_terminal:
                tbToolbar.setTitle(R.string.terminal);
                switchFragment(INDEX_TERMINAL);
                Log.i("debug_message", "tab_terminal: ");
                break;
        }
        return false;
    }

    private Fragment getFragment(int index) {
        Fragment fragment = mFragmentMap.get(index);
        if (fragment == null) {
            switch (index) {
                case INDEX_FUNCTION:
                    fragment = FunctionFragment.newInstance();
                    break;

                case INDEX_CONTROLLER:
                    fragment = TerminalFragment.newInstance();
                    break;

                case INDEX_TERMINAL:
                    fragment = TerminalFragment.newInstance();
                    break;

                default:
                    break;
            }
            mFragmentMap.put(index, fragment);
        }

        return fragment;
    }

    private int mLastIndex = -1;
    private void switchFragment(int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (mLastIndex != -1) {
            transaction.hide(getFragment(mLastIndex));
        }
        mLastIndex = index;

        Fragment fragment = getFragment(index);
        if (!fragment.isAdded()) {
            transaction.add(R.id.fl_fragment_container, fragment);
        }

        transaction.show(fragment).commitAllowingStateLoss();
    }

    @Override
    public void onAttached() {
        super.onAttached();
    }

    @Override
    public void deviceOnline(AdbDevice device) {
        super.deviceOnline(device);
        L.i("deviceOnline:"+device.getSerial());
        RxBus.getDefault().post(new EventAdbDevice(device));
    }

    @Override
    public void onDetached(String deviceName) {
        super.onDetached(deviceName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //PreventKeyboardBlockUtil.getInstance(this).setBtnView(findViewById(R.id.bnv_bottom_nav)).register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //PreventKeyboardBlockUtil.getInstance(this).unRegister();
    }
}
