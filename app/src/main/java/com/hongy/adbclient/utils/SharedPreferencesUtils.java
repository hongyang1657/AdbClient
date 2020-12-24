package com.hongy.adbclient.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferencesUtils {

  private static SharedPreferencesUtils instance;
  private static Context context;

  private SharedPreferencesUtils(Context context) {
    this.context = context;
  }

  public synchronized static SharedPreferencesUtils getInstance(Context context) {
    if (instance == null)
      instance = new SharedPreferencesUtils(context.getApplicationContext());
    return instance;
  }

  private SharedPreferences.Editor getEditor() {
    return getSharedPreferences().edit();
  }

  private static SharedPreferences getSharedPreferences() {
    return context.getSharedPreferences("ayahUpgrade", Activity.MODE_PRIVATE);
  }

  public String getStringValueByKey(String key) {
    return getSharedPreferences().getString(key, "");
  }

  public String getStringValueByKey(String key,String def) {
    return getSharedPreferences().getString(key, def);
  }

  public synchronized void setStringKeyValue(String key, String value) {
    SharedPreferences.Editor editor = getEditor();
    editor.putString(key, value);
    editor.commit();
  }
  public long getLongValueByKey(String key, long defaultNum) {
    return getSharedPreferences().getLong(key,defaultNum);
  }

  public synchronized void setLongKeyValue(String key, long value) {
    SharedPreferences.Editor editor = getEditor();
    editor.putLong(key, value);
    editor.commit();
  }

  public float getFloatValueByKey(String key, float defaultNum) {
    return getSharedPreferences().getFloat(key,defaultNum);
  }

  public synchronized void setFloatKeyValue(String key, float value) {
    SharedPreferences.Editor editor = getEditor();
    editor.putFloat(key, value);
    editor.commit();
  }

  public boolean getBooleanValueByKey(String key, boolean defaultValue) {
    return getSharedPreferences().getBoolean(key, defaultValue);
  }

  public synchronized void setBooleanKeyValue(String key, boolean flagValue) {
    SharedPreferences.Editor editor = getEditor();
    editor.putBoolean(key, flagValue);
    editor.commit();
  }

  public int getIntValueByKey(String key) {
    return getSharedPreferences().getInt(key, 0);
  }

  public synchronized void setIntKeyValue(String key, int value) {
    SharedPreferences.Editor editor = getEditor();
    editor.putInt(key, value);
    editor.commit();
  }

  //清空数据
  public synchronized void clearSharedPreference(){
    SharedPreferences.Editor editor = getEditor();
    editor.clear();
    editor.commit();
  }


  //注册的倒计时
  public long getLongCountMin(String key,long defaultCount){
    return getSharedPreferences().getLong(key,defaultCount);
  }

  public synchronized void setLongCountMin(String key,long minCount){
    SharedPreferences.Editor editor = getEditor();
    editor.putLong(key,minCount);
    editor.commit();
  }
}
