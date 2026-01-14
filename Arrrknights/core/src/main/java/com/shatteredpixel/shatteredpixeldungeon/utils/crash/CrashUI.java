package com.shatteredpixel.shatteredpixeldungeon.utils.crash;

import com.shatteredpixel.shatteredpixeldungeon.utils.crash.bundle.CrashReport;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.Scene;
import com.watabou.utils.DeviceCompat;

public final class CrashUI {

    private CrashUI() {}

    public static void showOverlay(CrashReport report) {
        if (report == null) return;

        Scene scene = Game.scene();
        if (scene != null) {
            try {
                DeviceCompat.log("Showing crash report overlay on " + scene.getClass().getName());
                scene.addToFront(new WndCrashReport(report));
                return;
            } catch (Throwable ignored) {
            }
        }
    }
}
