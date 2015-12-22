package com.cyou.cma.clockscreen;

import android.content.Context;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;

import java.lang.reflect.Constructor;

public class LayoutInflaterFactory implements LayoutInflater.Factory {
    private final ClassLoader mClassLoader;
    private final Object[] mConstructorArgs = new Object[2];

    private static final Class<?>[] mConstructorSignature = new Class[] {
            Context.class, AttributeSet.class
    };

    public LayoutInflaterFactory(ClassLoader classLoader) {
        mClassLoader = classLoader;
    }

    /**
     * 这个方法调用只针对自定义的View起作�?
     * 
     * @param context
     * @param name
     * @param attrs
     * @return
     */
    public View createView(Context context, String name, AttributeSet attrs) {
        Constructor<? extends View> constructor;
        Class<? extends View> clazz = null;
        mConstructorArgs[0] = context;
        mConstructorArgs[1] = attrs;
        try {
            clazz = mClassLoader.loadClass(name).asSubclass(View.class);
            constructor = clazz.getConstructor(mConstructorSignature);
            View v = constructor.newInstance(mConstructorArgs);
            return v;
        } catch (NoSuchMethodException e) {
            InflateException ie = new InflateException(attrs.getPositionDescription()
                    + ": Error inflating class " + name);
            ie.initCause(e);
            throw ie;

        } catch (ClassCastException e) {
            InflateException ie = new InflateException(attrs.getPositionDescription()
                    + ": Class is not a View " + name);
            ie.initCause(e);
            throw ie;
        } catch (ClassNotFoundException e) {

            return null;
        } catch (Exception e) {
            InflateException ie = new InflateException(attrs.getPositionDescription()
                    + ": Error inflating class " + (clazz == null ? "<unknown>" : clazz.getName()));
            ie.initCause(e);
            throw ie;
        }
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        if (-1 != name.indexOf('.')) {
            return createView(context, name, attrs);
        }

        return null;
    }
}
