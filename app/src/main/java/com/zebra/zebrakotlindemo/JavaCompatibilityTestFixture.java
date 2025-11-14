package com.zebra.zebrakotlindemo;

import android.content.Context;

import com.zebra.emdk_kotlin_wrapper.dw.DataWedgeHelper;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class JavaCompatibilityTestFixture {

    void callKotlinObject(Context context) {
        DataWedgeHelper.INSTANCE.prepare(context, new Function1<Boolean, Unit>() {
            @Override
            public Unit invoke(Boolean aBoolean) {
                return null;
            }
        });
    }

    void callKotlinObjectWithNullableCallback(Context context) {
        DataWedgeHelper.INSTANCE.enableDW(context, null);

        DataWedgeHelper.INSTANCE.enableDW(context, new Function1<Boolean, Unit>() {
            @Override
            public Unit invoke(Boolean aBoolean) {
                return null;
            }
        });
    }
}
