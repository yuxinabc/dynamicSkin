package com.synertone.skin_core.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;

import com.synertone.skin_core.R;

public class SkinThemeUtil {
    private static int[] TYPEFACE_ATTR = {
            R.attr.skinTypeface
    };
    private static int[] APPCOMPAT_COLOR_PRIMARY_DARK_ATTRS = {
            android.support.v7.appcompat.R.attr.colorPrimaryDark
    };
    private static int[] STATUSBAR_COLOR_ATTRS = {android.R.attr.statusBarColor, android.R.attr
            .navigationBarColor};

    public static  int[] getResId(Context context,int[] attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        int length=typedArray.length();
        int[] resIds=new int[attrs.length];
        for(int i=0;i<length;i++){
            int resourceId = typedArray.getResourceId(i, 0);
            resIds[i]=resourceId;
        }
        typedArray.recycle();
        return resIds;
    }
    public static void updateStatusBar(Activity activity) {
        //5.0以上才能修改
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        int[] resIds = getResId(activity, STATUSBAR_COLOR_ATTRS);
        /**
         * 修改状态栏的颜色
         */
        //如果没有配置 属性 则获得0
        if (resIds[0] == 0) {
            int statusBarColorId = getResId(activity, APPCOMPAT_COLOR_PRIMARY_DARK_ATTRS)[0];
            if (statusBarColorId != 0) {
                activity.getWindow().setStatusBarColor(SkinResources.getInstance().getColor(statusBarColorId));
            }
        } else {
            activity.getWindow().setStatusBarColor(SkinResources.getInstance().getColor(resIds[0]));
        }
        //修改底部虚拟按键的颜色
        if (resIds[1] != 0) {
            activity.getWindow().setNavigationBarColor(SkinResources.getInstance().getColor(resIds[1]));
        }

    }

    public static Typeface getTypeface(Activity activity) {
        int[] typeFaceAttrId = getResId(activity, TYPEFACE_ATTR);
        return SkinResources.getInstance().getTypeface(typeFaceAttrId[0]);
    }
}
