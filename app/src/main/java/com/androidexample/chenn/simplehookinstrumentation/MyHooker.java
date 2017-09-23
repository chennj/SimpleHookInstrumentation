package com.androidexample.chenn.simplehookinstrumentation;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * Created by chenn on 2017/9/23.
 */

public final class MyHooker {

    public static String TYPE = "ActivityMonitorController";

    private boolean isHook = false;
    public  Set<InstruCallback> callbacks = null;
    private Object currentActivityThread = null;
    private Instrumentation myInstrumentation = null;

    public static abstract class InstruCallback {

        public InstruCallback() {

        }

        public void beforeOnNewActivity(ClassLoader classLoader, String className, Intent intent){}

        public void beforeOnCreate(Activity activity, Bundle bundle) {}

        public void afterOnCreate(Activity activity, Bundle bundle) {}

        public void beforeOnResume(Activity activity) {}

        public void afterOnResume(Activity activity) {}

        public void beforeOnPause(Activity activity) {}

        public void afterOnPause(Activity activity) {}

        public void beforeOnStart(Activity activity) {}

        public void afterOnStart(Activity activity) {}

        public void beforeOnNewIntent(Activity activity, Intent intent) {}

        public void afterOnNewIntent(Activity activity, Intent intent) {}

        public void beforeOnStop(Activity activity) {}

        public void afterOnStop(Activity activity) {}

        public void beforeOnDestroy(Activity activity) {}

        public void afterOnDestroy(Activity activity) {}
    }

    private class CustomInstrumentation extends Instrumentation{

        @Override
        public Activity newActivity(ClassLoader classLoader, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {

            MyHooker.this.onNewActivity(classLoader, className, intent);
            return myInstrumentation.newActivity(classLoader, className, intent);
        }

        @Override
        public void callActivityOnCreate(Activity activity, Bundle bundle) {

            MyHooker.this.onCreate(activity, bundle, true);
            myInstrumentation.callActivityOnCreate(activity, bundle);
            MyHooker.this.onCreate(activity, bundle, false);
        }

        @Override
        public void callActivityOnResume(Activity activity){

            MyHooker.this.onResume(activity, true);
            myInstrumentation.callActivityOnResume(activity);
            MyHooker.this.onResume(activity, false);
        }

        @Override
        public void callActivityOnPause(Activity activity) {

            MyHooker.this.onPause(activity, true);
            myInstrumentation.callActivityOnPause(activity);
            MyHooker.this.onPause(activity, false);
        }

        @Override
        public void callActivityOnStart(Activity activity) {

            MyHooker.this.onStart(activity, true);
            myInstrumentation.callActivityOnStart(activity);
            MyHooker.this.onStart(activity, false);
        }

        @Override
        public void callActivityOnRestart(Activity activity) {

            myInstrumentation.callActivityOnRestart(activity);
        }

        @Override
        public void callActivityOnNewIntent(Activity activity, Intent intent) {

            MyHooker.this.onNewIntent(activity, intent, true);
            myInstrumentation.callActivityOnNewIntent(activity, intent);
            MyHooker.this.onNewIntent(activity, intent, false);
        }

        @Override
        public void callActivityOnSaveInstanceState(Activity activity, Bundle bundle) {

            myInstrumentation.callActivityOnSaveInstanceState(activity, bundle);
        }

        @Override
        public void callActivityOnRestoreInstanceState(Activity activity, Bundle bundle) {

            myInstrumentation.callActivityOnRestoreInstanceState(activity, bundle);
        }

        @Override
        public void callActivityOnStop(Activity activity) {

            MyHooker.this.onStop(activity, true);
            myInstrumentation.callActivityOnStop(activity);
            MyHooker.this.onStop(activity, false);
        }

        @Override
        public void callActivityOnDestroy(Activity activity) {

            MyHooker.this.onDestroy(activity, true);
            myInstrumentation.callActivityOnDestroy(activity);
            MyHooker.this.onDestroy(activity, false);
        }

    }

    public final boolean initHookInstrumentation(){

        boolean hadInit = false;

        try{
            currentActivityThread = RefectUtil.invokeStaticMethod("android.app.ActivityThread", "currentActivityThread");
            if (null == currentActivityThread)
                throw new IllegalStateException("Failed to get CurrentActivityThread.");
            myInstrumentation = (Instrumentation)RefectUtil.getField(currentActivityThread.getClass(), "mInstrumentation", currentActivityThread);
            if (null == myInstrumentation){
                throw new IllegalStateException("Failed to get Instrumentation instance.");
            } else if (myInstrumentation.getClass().equals(CustomInstrumentation.class)){
                //当前的currentActivityThread的mInstrumentation已经被替换
                hadInit = true;
                isHook = true;
            } else if (myInstrumentation.getClass().equals(Instrumentation.class)){
                //将当前的currentActivityThread的mInstrumentation替换为自己的CustomInstrumentation
                Class cls = currentActivityThread.getClass();
                String fieldName = "mInstrumentation";
                CustomInstrumentation customIntru = new CustomInstrumentation();
                Object obj = currentActivityThread;
                try{
                    Field declaredField = cls.getDeclaredField(fieldName);
                    declaredField.setAccessible(true);
                    declaredField.set(obj, customIntru);
                    hadInit = true;
                    isHook = true;
                } catch(Exception e){
                    e.printStackTrace();
                }
            } else {
                throw new IllegalStateException("Not original Instrumentation instance, give up monitoring.");
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return hadInit;
    }

    public final synchronized void onNewActivity(ClassLoader classLoader, String className, Intent intent){

        for (InstruCallback callback : callbacks){

            callback.beforeOnNewActivity(classLoader, className, intent);
        }
    }

    public final synchronized void onCreate(Activity activity, Bundle bundle, boolean beforeorafter){

        for (InstruCallback callback : callbacks){

            if (beforeorafter){
                callback.beforeOnCreate(activity, bundle);
            }else{
                callback.afterOnCreate(activity, bundle);
            }
        }
    }

    public final synchronized void onResume(Activity activity, boolean beforeorafter){

        for (InstruCallback callback : callbacks){

            if (beforeorafter){
                callback.beforeOnResume(activity);
            }else{
                callback.afterOnResume(activity);
            }
        }
    }

    public final synchronized void onPause(Activity activity, boolean beforeorafter){

        for (InstruCallback callback : callbacks){

            if (beforeorafter){
                callback.beforeOnPause(activity);
            }else{
                callback.afterOnPause(activity);
            }
        }
    }

    public final synchronized void onStart(Activity activity, boolean beforeorafter){

        for (InstruCallback callback : callbacks){

            if (beforeorafter){
                callback.beforeOnStart(activity);
            }else{
                callback.afterOnStart(activity);
            }
        }
    }

    public final synchronized void onNewIntent(Activity activity, Intent intent, boolean beforeorafter){

        for (InstruCallback callback : callbacks){

            if (beforeorafter){
                callback.beforeOnNewIntent(activity, intent);
            }else{
                callback.afterOnNewIntent(activity, intent);
            }
        }
    }

    public final synchronized void onStop(Activity activity, boolean beforeorafter){

        for (InstruCallback callback : callbacks){

            if (beforeorafter){
                callback.beforeOnStop(activity);
            }else{
                callback.afterOnStop(activity);
            }
        }
    }

    public final synchronized void onDestroy(Activity activity, boolean beforeorafter){

        for (InstruCallback callback : callbacks){

            if (beforeorafter){
                callback.beforeOnDestroy(activity);
            }else{
                callback.afterOnDestroy(activity);
            }
        }
    }

}
