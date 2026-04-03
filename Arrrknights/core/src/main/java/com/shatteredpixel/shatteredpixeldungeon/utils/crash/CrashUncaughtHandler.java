package com.shatteredpixel.shatteredpixeldungeon.utils.crash;

public class CrashUncaughtHandler implements Thread.UncaughtExceptionHandler {

    private final Thread.UncaughtExceptionHandler previous;

    public CrashUncaughtHandler(Thread.UncaughtExceptionHandler previous) {
        this.previous = previous;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        try {
            CrashCenter.recordFromUncaught(t, e);
            CrashCenter.pumpUI();
        } catch (Throwable ignored) {}

        if (CrashUtil.isProbablyFatal(e)) {
            if (previous != null) {
                previous.uncaughtException(t, e);
            }
        }
    }
}
