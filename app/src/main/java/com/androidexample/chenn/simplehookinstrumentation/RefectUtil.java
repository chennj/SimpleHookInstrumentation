package com.androidexample.chenn.simplehookinstrumentation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by chenn on 2017/9/23.
 */

public final class RefectUtil {

    private static Object invokeMethod(Class<?> targetObjCls, String methodName, Object targetObj, Class<?>[] ParamTypeArr, Object[] ParamArr) {

        Object obj = null;
        try{
            Method declaredMethod = targetObjCls.getDeclaredMethod(methodName, ParamTypeArr);
            declaredMethod.setAccessible(true);
            obj = declaredMethod.invoke(targetObj, ParamArr);
        } catch(Exception e){
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 获取对象的属性
     */
    public static Object getField(Class<?> targetObjCls, String fieldName, Object targetObj){

        try{
            Field declaredField = targetObjCls.getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            Object obj = declaredField.get(targetObj);
            return obj;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 执行对象的方法，并获取返回值
     */
    public static Object invokeMethod(String methodName, Object targetObj, Object[] params){

        Class[] paramsType = null;
        if (null != params){
            paramsType = new Class[params.length];
            for(int i=0; i<params.length; ++i){
                paramsType[i] = params[i].getClass();
            }
        }

        return invokeMethod(targetObj.getClass(), methodName, targetObj, paramsType, params);
    }

    /*
     * 执行类的静态方法，并获取返回值
     */
    public static Object invokeStaticMethod(String fullClassName, String methodName) throws ClassNotFoundException {

        return invokeMethod(Class.forName(fullClassName), methodName, null, null, null);
    }
}
