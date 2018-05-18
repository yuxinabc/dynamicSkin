package com.synertone.skin_core;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;

import android.text.TextUtils;

import com.synertone.skin_core.util.SkinPreference;
import com.synertone.skin_core.util.SkinResources;

import java.lang.reflect.Method;
import java.util.Observable;

public class SkinManager extends Observable {
    private static SkinManager mInstance;
    private AppActivityLifecycleCallbacks appActivityLifecycleCallbacks;
    private Application application;

    private SkinManager(Application application) {
        this.application = application;
        SkinPreference.init(application);
        SkinResources.init(application);
        //加载资源
        loadSkinNoNotify(SkinPreference.getInstance().getSkin());
        //筛选可换肤控件，并且换皮肤
        appActivityLifecycleCallbacks = new AppActivityLifecycleCallbacks();
        application.registerActivityLifecycleCallbacks(appActivityLifecycleCallbacks);

    }

    private void loadSkinNoNotify(String skinPath) {
        if (TextUtils.isEmpty(skinPath)) {
            SkinPreference.getInstance().setSkin("");
            SkinResources.getInstance().reset();
        } else {
            try {
                //不能用AssetManager manager=application.getAssets();
                AssetManager assetManager = AssetManager.class.newInstance();
                // 添加资源进入资源管理器
                Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String
                        .class);
                addAssetPath.setAccessible(true);
                addAssetPath.invoke(assetManager, skinPath);
                //app的resources
                Resources resources = application.getResources();
                // 横竖、语言 skinlib的resources
                Resources skinResource = new Resources(assetManager, resources.getDisplayMetrics(),
                        resources.getConfiguration());
                //获取外部Apk(皮肤包) 包名
                PackageManager mPm = application.getPackageManager();
                PackageInfo info = mPm.getPackageArchiveInfo(skinPath, PackageManager
                        .GET_ACTIVITIES);
                String packageName = info.packageName;
                SkinResources.getInstance().applySkin(skinResource, packageName);
                //保存当前使用的皮肤包
                SkinPreference.getInstance().setSkin(skinPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void init(Application application) {
        synchronized (SkinManager.class) {
            if (mInstance == null) {
                mInstance = new SkinManager(application);
            }
        }
    }

    public static SkinManager getInstance() {
        return mInstance;
    }

    /**
     * 加载资源，并且换肤
     *
     * @param skinPath
     */
    public void loadSkin(String skinPath) {
        if (TextUtils.isEmpty(skinPath)) {
            SkinPreference.getInstance().setSkin("");
            SkinResources.getInstance().reset();
        } else {
            try {
                //不能用AssetManager manager=application.getAssets();
                AssetManager assetManager = AssetManager.class.newInstance();
                // 添加资源进入资源管理器
                Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String
                        .class);
                addAssetPath.setAccessible(true);
                addAssetPath.invoke(assetManager, skinPath);
                //app的resources
                Resources resources = application.getResources();
                // 横竖、语言 skinlib的resources
                Resources skinResource = new Resources(assetManager, resources.getDisplayMetrics(),
                        resources.getConfiguration());
                //获取外部Apk(皮肤包) 包名
                PackageManager mPm = application.getPackageManager();
                PackageInfo info = mPm.getPackageArchiveInfo(skinPath, PackageManager
                        .GET_ACTIVITIES);
                String packageName = info.packageName;
                SkinResources.getInstance().applySkin(skinResource, packageName);
                //保存当前使用的皮肤包
                SkinPreference.getInstance().setSkin(skinPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setChanged();
        notifyObservers();
    }

    public void updateSkin(Activity activity) {
        appActivityLifecycleCallbacks.updateSkin(activity);
    }
}
