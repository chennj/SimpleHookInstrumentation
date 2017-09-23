package com.androidexample.chenn.simplehookinstrumentation;

import android.app.Application;
import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by chenn on 2017/9/23.
 */

public class MyApplication extends Application {

    private static MyApplication instance;

    public static MyApplication getInstance(){return instance;}
    @Override
    public void onCreate(){

        super.onCreate();

        instance = this;
        MyHooker myHooker = new MyHooker();
        if (myHooker.initHookInstrumentation()){
            myHooker.callbacks = new HashSet<MyHooker.InstruCallback>();
            myHooker.callbacks.add(new NewActivityAction());
        }
    }
}
