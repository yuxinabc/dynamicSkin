package com.synertone.skin_core;


import android.app.Activity;
import android.app.Application;
import android.graphics.Typeface;
import android.os.Bundle;

import android.support.v4.util.ArrayMap;
import android.support.v4.view.LayoutInflaterCompat;
import android.view.LayoutInflater;
import android.widget.TextView;


import com.synertone.skin_core.util.SkinThemeUtil;

import java.lang.reflect.Field;

public class AppActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    private ArrayMap<Activity, SkinLayoutInflaterFactory> mLayoutInflaterFactories = new
            ArrayMap<>();
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        //状态栏换肤
        SkinThemeUtil.updateStatusBar(activity);
        Typeface typeface = SkinThemeUtil.getTypeface(activity);
        initFactorySet(activity);
        //使用factory2 设置布局加载工程
        //不能将skinLayoutInflaterFactory 设置为全局变量,因为需要给每个Factory注册观察者
        SkinLayoutInflaterFactory skinLayoutInflaterFactory = new SkinLayoutInflaterFactory
                (activity,typeface);
        LayoutInflaterCompat.setFactory2(LayoutInflater.from(activity), skinLayoutInflaterFactory);
        mLayoutInflaterFactories.put(activity, skinLayoutInflaterFactory);
        //注册观察者
        SkinManager.getInstance().addObserver(skinLayoutInflaterFactory);
    }

    private void initFactorySet(Activity activity) {
        Class<LayoutInflater> layoutInflaterClass = LayoutInflater.class;
        try {
            Field mFactorySet = layoutInflaterClass.getDeclaredField("mFactorySet");
            mFactorySet.setAccessible(true);
            mFactorySet.set(LayoutInflater.from(activity),false);
        } catch (NoSuchFieldException e) {

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        SkinLayoutInflaterFactory observer = mLayoutInflaterFactories.remove(activity);
        SkinManager.getInstance().deleteObserver(observer);
    }

    public void updateSkin(Activity activity) {
        SkinLayoutInflaterFactory skinLayoutInflaterFactory = mLayoutInflaterFactories.get(activity);
        skinLayoutInflaterFactory.update(null,null);
    }
}
