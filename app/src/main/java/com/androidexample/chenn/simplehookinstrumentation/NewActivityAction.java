package com.androidexample.chenn.simplehookinstrumentation;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * Created by chenn on 2017/9/23.
 */

public class NewActivityAction extends MyHooker.InstruCallback {

    @Override
    public void beforeOnNewActivity(ClassLoader classLoader, String className, Intent intent) {

        Toast.makeText(MyApplication.getInstance(), "此路是我开\n此树是我栽\n要想从此过\n留下买路财", Toast.LENGTH_SHORT).show();

        try {
            //下面的代码没什么卵用，来自于网络，演示一下而已
            MyApplication myApplication = MyApplication.getInstance();
            Field mLoadedApk = myApplication.getClass().getSuperclass().getDeclaredField("mLoadedApk");
            mLoadedApk.setAccessible(true);
            Object mLoadedApkObject = mLoadedApk.get(myApplication);
            Log.d("[app]", "获取的mLoadedApkObject=" + mLoadedApkObject);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }
}
