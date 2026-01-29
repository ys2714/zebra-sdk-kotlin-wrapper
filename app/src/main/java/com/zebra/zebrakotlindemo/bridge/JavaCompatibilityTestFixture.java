package com.zebra.zebrakotlindemo.bridge;

import android.content.Context;

import com.zebra.emdk_kotlin_wrapper.java_interface.BooleanCompletion;
import com.zebra.emdk_kotlin_wrapper.java_interface.DWHelperJava;
import com.zebra.emdk_kotlin_wrapper.java_interface.EMDKHelperJava;
import com.zebra.emdk_kotlin_wrapper.java_interface.MXHelperJava;
import com.zebra.emdk_kotlin_wrapper.java_interface.StringCompletion;

public class JavaCompatibilityTestFixture {

    void callKotlinObject(Context context) {
        EMDKHelperJava.prepare(context, new BooleanCompletion() {
            @Override
            public void onComplete(Boolean success) {

            }
        });

        DWHelperJava.prepare(context, new BooleanCompletion() {
            @Override
            public void onComplete(Boolean success) {
                
            }
        });

        MXHelperJava.fetchPPID(context, true, new StringCompletion() {
            @Override
            public void onComplete(String result) {

            }
        });
    }

    void callKotlinObjectWithNullableCallback(Context context) {
        DWHelperJava.enableDW(context, null);

        DWHelperJava.enableDW(context, new BooleanCompletion() {
            @Override
            public void onComplete(Boolean success) {

            }
        });
    }
}
