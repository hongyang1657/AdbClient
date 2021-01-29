package com.hongy.adbclient.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import com.hongy.adbclient.R;
import com.hongy.adbclient.app.MainApplication;
import com.hongy.adbclient.utils.L;
import com.hongy.adbclient.utils.PermissionsUtils;


/**
 * 启动首页
 */
public class SplashActivity extends BaseActivity {

    //保存用户信息到app
    private MainApplication app;
    //权限
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE,Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen(true);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
        app = (MainApplication) getApplication();
        initWindow();
    }

    @Override
    protected void initData() {
        boolean isPermissionPassed = PermissionsUtils.getInstance().chekPermissions(this,permissions , new PermissionsUtils.IPermissionsResult() {
            @Override
            public void passPermissons() {
                L.i("---------------权限通过----------------");
                toMain();
            }

            @Override
            public void forbitPermissons() {
                L.i("---------------权限禁止----------------");
                finish();
            }
        });
        L.i("isPermissionPassed:"+isPermissionPassed);
        if (isPermissionPassed){
            toMain();
        }
    }

    private void toMain() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    //初始化屏幕宽高数据
    private void initWindow(){
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        app.setWidth(width);
        app.setHeight(height);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsUtils.getInstance().onRequestPermissionsResult(this,requestCode,permissions,grantResults);
    }
}
