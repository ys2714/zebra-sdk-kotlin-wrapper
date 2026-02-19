package com.zebra.emdk_kotlin_wrapper.java_interface;

import android.content.Context;

import androidx.annotation.Keep;

import com.zebra.emdk_kotlin_wrapper.emdk.EMDKHelper;
import kotlin.Unit;

@Keep
public class EMDKHelperJava {

    public static void prepare(Context context, BooleanCompletion completion) {
        EMDKHelper.Companion.getShared().prepare(context, (success) -> {
            completion.onComplete(success);
            return Unit.INSTANCE;
        });
    }

    public static void teardown() {
        EMDKHelper.Companion.getShared().teardown();
    }

    public static String getEMDKVersion() {
        return EMDKHelper.Companion.getShared().getEmdkVersion();
    }

    public static String getMXVersion() {
        return EMDKHelper.Companion.getShared().getMxVersion();
    }

    public static String getDWVersion() {
        return EMDKHelper.Companion.getShared().getDwVersion();
    }
}
