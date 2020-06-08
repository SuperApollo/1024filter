package com.example.clfilter;

import android.content.Context;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class ToastUtil {

    public static void s(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    public static void l(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_LONG).show();
    }

    public void showStableToast(final Toast toast, final int count) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0, 3500);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, count);
    }

}
