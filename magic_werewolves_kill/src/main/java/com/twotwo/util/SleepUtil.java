package com.twotwo.util;

import javax.swing.Timer;

public class SleepUtil {
    public static void SLEEP(long milliseconds) {
        Timer timer = new Timer((int) milliseconds, e -> {
            // Do nothing, just wait
        });
        timer.setRepeats(false); // 只执行一次
        timer.start();
    }
}
