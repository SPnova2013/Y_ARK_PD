package com.shatteredpixel.shatteredpixeldungeon.utils.crash.bundle;

import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public class CrashReport implements Bundlable {

    public enum Source {
        GDX_RENDER_THREAD,
        UNCAUGHT_OTHER_THREAD
    }

    public long timeMillis;

    public Source source;
    public String threadName;
    public long threadId;

    public String gameVersion;
    public int versionCode;

    public long dungeonSeed;
    public int dungeonChallenges;

    public String exceptionType;
    public String exceptionMessage;
    public String stackTrace;

    public boolean fatalHint;

    public CrashReport() {}

    @Override
    public void storeInBundle(Bundle b) {
        b.put("timeMillis", timeMillis);

        b.put("source", source != null ? source.name() : Source.UNCAUGHT_OTHER_THREAD.name());
        b.put("threadName", threadName);
        b.put("threadId", threadId);

        b.put("gameVersion", gameVersion);
        b.put("versionCode", versionCode);

        b.put("dungeonSeed", dungeonSeed);
        b.put("dungeonChallenges", dungeonChallenges);

        b.put("exceptionType", exceptionType);
        b.put("exceptionMessage", exceptionMessage);
        b.put("stackTrace", stackTrace);

        b.put("fatalHint", fatalHint);
    }

    @Override
    public void restoreFromBundle(Bundle b) {
        timeMillis = b.getLong("timeMillis");

        try {
            source = Source.valueOf(b.getString("source"));
        } catch (Throwable ignored) {
            source = Source.UNCAUGHT_OTHER_THREAD;
        }

        threadName = b.getString("threadName");
        threadId = b.getLong("threadId");

        gameVersion = b.getString("gameVersion");
        versionCode = b.getInt("versionCode");

        dungeonSeed = b.getLong("dungeonSeed");
        dungeonChallenges = b.getInt("dungeonChallenges");

        exceptionType = b.getString("exceptionType");
        exceptionMessage = b.getString("exceptionMessage");
        stackTrace = b.getString("stackTrace");

        fatalHint = b.getBoolean("fatalHint");
    }
}