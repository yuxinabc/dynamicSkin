package com.synertone.skin_core;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;

import com.synertone.skin_core.util.SkinThemeUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class SkinLayoutInflaterFactory implements LayoutInflater.Factory2, Observer {
    private static final HashMap<String, Constructor<? extends View>> sConstructorMap =
            new HashMap<>();
    private static final ClassLoader BOOT_CLASS_LOADER = LayoutInflater.class.getClassLoader();
    static final Class<?>[] mConstructorSignature = new Class[]{
            Context.class, AttributeSet.class};
    private static final String[] mClassPrefixList = {
            "android.widget.",
            "android.view.",
            "android.webkit."
    };
    private SkinAttribute skinAttribute;
    private  Activity mActivity;
    public SkinLayoutInflaterFactory(Activity activity, Typeface typeface) {
        mActivity=activity;
        skinAttribute = new SkinAttribute(typeface);
    }





    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        try {
            return createViewFromName(name, attrs, context);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private View createViewFromName(String name, AttributeSet attrs, Context mContext) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        View view = null;
        if (-1 != name.indexOf('.')) {
            //自定义控件
            view = createView(name, null, attrs, mContext);
        } else {
            //系统控件
            for (int i = 0; i < mClassPrefixList.length; i++) {
                view = createView(name, mClassPrefixList[i], attrs, mContext);
                if (view != null) {
                    break;
                }
            }

        }
        skinAttribute.filterView(view, attrs);
        return view;
    }

    private View createView(String name, String prefix, AttributeSet attrs, Context mContext) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<? extends View> constructor = sConstructorMap.get(name);
        if (constructor != null && !verifyClassLoader(constructor, mContext)) {
            constructor = null;
            sConstructorMap.remove(name);
        }
        Class<? extends View> clazz;
        if (constructor == null) {
            // Class not found in the cache, see if it's real, and try to add it
            clazz = mContext.getClassLoader().loadClass(
                    prefix != null ? (prefix + name) : name).asSubclass(View.class);
            constructor = clazz.getConstructor(mConstructorSignature);
            constructor.setAccessible(true);
            sConstructorMap.put(name, constructor);
        }
        return constructor.newInstance(mContext, attrs);
    }


    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return null;
    }

    private final boolean verifyClassLoader(Constructor<? extends View> constructor, Context mContext) {
        final ClassLoader constructorLoader = constructor.getDeclaringClass().getClassLoader();
        if (constructorLoader == BOOT_CLASS_LOADER) {
            // fast path for boot class loader (most common case?) - always ok
            return true;
        }
        // in all normal cases (no dynamic code loading), we will exit the following loop on the
        // first iteration (i.e. when the declaring classloader is the contexts class loader).
        ClassLoader cl = mContext.getClassLoader();
        do {
            if (constructorLoader == cl) {
                return true;
            }
            cl = cl.getParent();
        } while (cl != null);
        return false;
    }

    @Override
    public void update(Observable o, Object arg) {
        //更换皮肤
        SkinThemeUtil.updateStatusBar(mActivity);
        Typeface typeface = SkinThemeUtil.getTypeface(mActivity);
        skinAttribute.applySkin(typeface);
    }
}
