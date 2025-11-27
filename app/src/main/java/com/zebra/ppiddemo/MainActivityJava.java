package com.zebra.ppiddemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.zebra.emdk_kotlin_wrapper.java_interface.BooleanCompletion;
import com.zebra.emdk_kotlin_wrapper.java_interface.EMDKHelperJava;
import com.zebra.emdk_kotlin_wrapper.java_interface.StringCompletion;
import com.zebra.emdk_kotlin_wrapper.java_interface.MXHelperJava;

/**
 * please switch the activity between MainActivityJava and MainActivityKotlin in AndroidManifest.xml
 * opening MainActivityJava by default
 * */
public class MainActivityJava extends Activity {

    private TextView textView1;
    private TextView textView2;
    private Button button1;
    private Button button2;

    private Boolean emdkPrepared = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);

        textView1.setText("PPID:");
        textView2.setText("Serial:");
        button1.setText("fetch PPID");
        button2.setText("fetch Serial");

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emdkPrepared) {
                    fetchPPID(MainActivityJava.this);
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emdkPrepared) {
                    fetchSerialNumber(MainActivityJava.this);
                }
            }
        });

        prepareEMDK(this);
    }

    void updatePPID(String result) {
        textView1.setText("PPID: " + result);
    }

    void updateSerial(String result) {
        textView2.setText("Serial: " + result);
    }

    public void prepareEMDK(Context context) {
        if (emdkPrepared) return;
        EMDKHelperJava.prepare(context, new BooleanCompletion() {
            @Override
            public void onComplete(Boolean success) {
                emdkPrepared = true;
            }
        });
    }

    public void fetchPPID(Context context) {
        MXHelperJava.fetchPPID(context, true, new StringCompletion() {
            @Override
            public void onComplete(String result) {
                if (!result.isEmpty()) {
                    updatePPID(result);
                } else {
                    showDebugToast(context, "PPID", "get ppid error");
                }
            }
        });
    }

    public void fetchSerialNumber(Context context) {
        MXHelperJava.fetchSerialNumber(context, new StringCompletion() {
            @Override
            public void onComplete(String result) {
                if (!result.isEmpty()) {
                    updateSerial(result);
                } else {
                    showDebugToast(context, "Serial", "get serial error");
                }
            }
        });
    }

    public void showDebugToast(Context context, String type, String data) {
        runOnUiThread(() -> Toast.makeText(context, type + "\n" + data, Toast.LENGTH_LONG).show());
    }
}
