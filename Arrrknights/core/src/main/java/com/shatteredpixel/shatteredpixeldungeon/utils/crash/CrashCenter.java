package com.shatteredpixel.shatteredpixeldungeon.utils.crash;

import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.utils.crash.bundle.CrashReport;
import com.watabou.noosa.Game;
import com.watabou.utils.DeviceCompat;

import java.util.concurrent.atomic.AtomicReference;

public final class CrashCenter {

    private CrashCenter() {}

    private static final AtomicReference<CrashReport> pending = new AtomicReference<>(null);
    private static volatile boolean gdxReady;

    public static void installUncaughtHandler() {
        Thread.UncaughtExceptionHandler prev = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new CrashUncaughtHandler(prev));
    }

    public static void onGdxReady() {
        gdxReady = true;
        try {
            CrashReport last = CrashStore.loadLast();
            if (last != null) {
                CrashStore.clearLast();
                postShow(last);
            }
        } catch (Throwable ignored) {}
    }

    public static void recordFromRenderThread(Throwable t) {
        record(CrashReport.Source.GDX_RENDER_THREAD, Thread.currentThread(), t);
    }

    public static void recordFromUncaught(Thread thread, Throwable t) {
        record(CrashReport.Source.UNCAUGHT_OTHER_THREAD, thread, t);
    }

    private static void record(CrashReport.Source source, Thread thread, Throwable t) {
        CrashReport r = new CrashReport();
        r.timeMillis = System.currentTimeMillis();
        r.source = source;

        if (thread != null) {
            r.threadName = thread.getName();
            r.threadId = thread.getId();
        }

        // Best-effort game info; must not throw
        try { r.gameVersion = Game.version; } catch (Throwable ignored) { r.gameVersion = "???"; }
        try { r.versionCode = Game.versionCode; } catch (Throwable ignored) { r.versionCode = 0; }

        try { r.dungeonSeed = Dungeon.seed; } catch (Throwable ignored) { r.dungeonSeed = 0; }
        try { r.dungeonChallenges = Dungeon.challenges; } catch (Throwable ignored) { r.dungeonChallenges = 0; }

        r.exceptionType = (t != null) ? t.getClass().getName() : "(null)";
        r.exceptionMessage = (t != null) ? String.valueOf(t.getMessage()) : "(null)";
        r.stackTrace = CrashUtil.stackTraceToString(t);
        r.fatalHint = CrashUtil.isProbablyFatal(t);

        try {
            if (Gdx.files != null) {
                CrashStore.saveLast(r);
                CrashStore.saveHistory(r);
            }
        } catch (Throwable ignored) {}

        pending.set(r);
    }

    public static void pumpUI() {
        CrashReport r = pending.get();
        if (r == null) return;
        postShow(r);
    }

    private static void postShow(CrashReport r) {
        if (r == null) return;
        if (!gdxReady || Gdx.app == null) return;

        if (!pending.compareAndSet(r, null)) return;

        Gdx.app.postRunnable(() -> {
            try {
                CrashUI.showOverlay(r);
            } catch (Throwable ignored) {
            }
        });
    }
}
