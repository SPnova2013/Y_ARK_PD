package com.shatteredpixel.shatteredpixeldungeon.utils.crash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.utils.crash.bundle.CrashReport;
import com.watabou.utils.Bundle;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.FileUtils;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public final class CrashStore {

    private CrashStore() {}

    public static final String DIR = "crash_logs";
    public static final String LAST_BUNDLE = "last_crash.bundle";
    public static final String LAST_TEXT = "last_crash.txt";

    // --------- stacktrace shortening ---------
    private static final LinkedHashMap<String, String> TRACE_PREFIX = new LinkedHashMap<>();
    static {
        TRACE_PREFIX.put("com.shatteredpixel.shatteredpixeldungeon.", "[css].");
        TRACE_PREFIX.put("com.watabou.", "[wat].");
        TRACE_PREFIX.put("com.badlogic.gdx.", "[gdx].");
        TRACE_PREFIX.put("java.", "[j].");
        TRACE_PREFIX.put("javax.", "[jx].");
        TRACE_PREFIX.put("kotlin.", "[kt].");
        TRACE_PREFIX.put("android.", "[and].");
        TRACE_PREFIX.put("org.lwjgl.", "[lwjgl].");
    }

    public static String shortenStackTrace(String trace) {
        if (trace == null) return null;

        String[] lines = trace.split("\n");
        StringBuilder out = new StringBuilder(trace.length());

        for (String line : lines) {
            String s = line;
            for (Map.Entry<String, String> e : TRACE_PREFIX.entrySet()) {
                s = s.replace(e.getKey(), e.getValue());
            }
            out.append(s).append('\n');
        }

        return out.toString();
    }

    public static String shortenType(String exceptionType) {
        if (exceptionType == null) return null;
        String s = exceptionType;
        for (Map.Entry<String, String> e : TRACE_PREFIX.entrySet()) {
            s = s.replace(e.getKey(), e.getValue());
        }
        return s;
    }

    public static String traceLegend() {
        return "";
//        StringBuilder sb = new StringBuilder();
//        for (Map.Entry<String, String> e : TRACE_PREFIX.entrySet()) {
//            String full = e.getKey();
//            if (full.endsWith(".")) full = full.substring(0, full.length() - 1);
//
//            String tag = e.getValue();
//            if (tag.endsWith(".")) tag = tag.substring(0, tag.length() - 1);
//
//            sb.append(tag).append("=").append(full).append("  ");
//        }
//        return sb.toString().trim();
    }

    public static FileHandle dir() {
        return Gdx.files.local(DIR);
    }

    public static void ensureDir() {
        try {
            FileHandle d = dir();
            if (!d.exists()) d.mkdirs();
        } catch (Throwable ignored) {}
    }

    public static void saveLast(CrashReport report) {
        if (report == null) return;

        try {
            ensureDir();

            // Bundle
            Bundle b = new Bundle();
            report.storeInBundle(b);

            FileHandle outFile = dir().child(LAST_BUNDLE);
            try (OutputStream out = outFile.write(false)) {
                Bundle.write(b, out, true);
            }

            // Human readable (shortened for readability)
            dir().child(LAST_TEXT).writeString(prettyShort(report), false, "UTF-8");

        } catch (Throwable ignored) {
            // Avoid cascading crashes.
        }
    }

    public static void saveHistory(CrashReport report) {
        if (report == null) return;

        try {
            ensureDir();
            String ts = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
                    .format(new Date(report.timeMillis));
            String base = "crash_" + ts;

            Bundle b = new Bundle();
            report.storeInBundle(b);

            FileHandle outFile = dir().child(base + ".bundle");
            try (OutputStream out = outFile.write(false)) {
                Bundle.write(b, out, true);
            }
            dir().child(base + ".txt").writeString(prettyShort(report), false, "UTF-8");
        } catch (Throwable ignored) {}
    }

    public static CrashReport loadLast() {
        try {
            Bundle b;
            FileHandle in = dir().child(LAST_BUNDLE);
            try {
                b = FileUtils.bundleFromFile(in.path());
            } catch (Throwable t) {
                b = FileUtils.bundleFromFile(Gdx.files.local(in.path()).path());
            }

            CrashReport r = new CrashReport();
            r.restoreFromBundle(b);
            return r;

        } catch (Throwable ignored) {
            return null;
        }
    }

    public static CrashReport loadFromBundleFile(FileHandle bundleFile) {
        if (bundleFile == null) {
            DeviceCompat.log("CrashStore.loadFromBundleFile: bundleFile == null");
            return null;
        }
        try {
            if (!bundleFile.exists()) {
                DeviceCompat.log("CrashStore.loadFromBundleFile: file does NOT exist: " + bundleFile.path());
                return null;
            }
            Bundle b = null;
            try {
                b = FileUtils.bundleFromFile(bundleFile.path());
            } catch (Throwable t1) {
                DeviceCompat.log("CrashStore.loadFromBundleFile: FAILED via bundleFile.path(): " + t1.getClass().getSimpleName() + ": " + t1.getMessage());
                try {
                    String localPath = Gdx.files.local(bundleFile.path()).path();
                    b = FileUtils.bundleFromFile(localPath);
                } catch (Throwable t2) {
                    DeviceCompat.log("CrashStore.loadFromBundleFile: FAILED via fallback localPath: " + t2.getClass().getSimpleName() + ": " + t2.getMessage());
                    return null;
                }
            }
            if (b == null) {
                DeviceCompat.log("CrashStore.loadFromBundleFile: bundle is null after load?!");
                return null;
            }
            CrashReport r = new CrashReport();
            try {
                r.restoreFromBundle(b);
            } catch (Throwable t3) {
                DeviceCompat.log("CrashStore.loadFromBundleFile: restore FAILED: " + t3.getClass().getSimpleName() + ": " + t3.getMessage());
                return null;
            }
            return r;
        } catch (Throwable t) {
            DeviceCompat.log("CrashStore.loadFromBundleFile: OUTER FAILED: " + t.getClass().getSimpleName() + ": " + t.getMessage());
            return null;
        }
    }


    public static void clearLast() {
        try {
            FileHandle d = dir();
            d.child(LAST_BUNDLE).delete();
            d.child(LAST_TEXT).delete();
        } catch (Throwable ignored) {}
    }

    // --------- history listing / clearing ---------
    public static final class SavedMeta {
        public final FileHandle bundleFile;
        public final long timeMillis;
        public final String exceptionType;
        public final String exceptionMessage;
        public final boolean fatalHint;
        public final boolean isLast;

        public SavedMeta(FileHandle bundleFile, long timeMillis, String exceptionType, String exceptionMessage, boolean fatalHint, boolean isLast) {
            this.bundleFile = bundleFile;
            this.timeMillis = timeMillis;
            this.exceptionType = exceptionType;
            this.exceptionMessage = exceptionMessage;
            this.fatalHint = fatalHint;
            this.isLast = isLast;
        }

        public String title() {
            String t = shortenType(exceptionType);
            if (t == null) t = "(unknown)";
            return isLast ? ("[LAST] " + t) : t;
        }

        public String subtitle() {
            String when = formatTime(timeMillis);
            String msg = trimOneLine(exceptionMessage, 80);
            return when + (fatalHint ? "  •  FATAL" : "") + (msg.isEmpty() ? "" : ("\n" + msg));
        }
    }

    /** Lists saved crash bundles. Includes LAST at top if present. Sorted newest->oldest. */
    public static ArrayList<SavedMeta> listSavedBundles() {
        ArrayList<SavedMeta> out = new ArrayList<>();
        try {
            ensureDir();
            FileHandle d = dir();
            if (!d.exists()) return out;

            // Include LAST if present
            FileHandle last = d.child(LAST_BUNDLE);
            if (last.exists()) {
                CrashReport r = loadFromBundleFile(last);
                if (r != null) {
                    out.add(new SavedMeta(last, r.timeMillis, r.exceptionType, r.exceptionMessage, r.fatalHint, true));
                } else {
                    // even if unreadable, show it
                    out.add(new SavedMeta(last, last.lastModified(), "(unreadable last_crash.bundle)", "", false, true));
                }
            }

            // Include ALL other .bundle files (not just crash_*.bundle)
            for (FileHandle f : d.list()) {
                DeviceCompat.log("Checking file: " + f.name());
                if (f == null || f.isDirectory()) continue;

                String name = f.name();
                if (!name.endsWith(".bundle")) continue;
                DeviceCompat.log("Found bundle file: " + name);
                if (LAST_BUNDLE.equals(name)) continue;
                DeviceCompat.log("Loading bundle file: " + name);

                CrashReport r = loadFromBundleFile(f);
                if (r != null) {
                    out.add(new SavedMeta(f, r.timeMillis, r.exceptionType, r.exceptionMessage, r.fatalHint, false));
                } else {
                    // Show it anyway so you can see it's there
                    out.add(new SavedMeta(f, f.lastModified(), "(unreadable) " + name, "", false, false));
                }
            }

            DeviceCompat.log("Output size before sort: " + out.size());

            // Sort newest first (LAST will naturally float to top if it's newest)
            out.sort((a, b) -> Long.compare(b.timeMillis, a.timeMillis));

        } catch (Throwable ignored) {}

        return out;
    }

    /** Deletes all saved reports (history + last), including .txt companions. */
    public static void clearAllSaved() {
        try {
            ensureDir();
            FileHandle d = dir();
            if (!d.exists()) return;

            for (FileHandle f : d.list()) {
                if (f == null || f.isDirectory()) continue;
                String n = f.name();
                if (n.endsWith(".bundle") || n.endsWith(".txt")) {
                    try { f.delete(); } catch (Throwable ignored) {}
                }
            }
        } catch (Throwable ignored) {}
    }

    // --------- formatting helpers ---------
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public static String formatTime(long millis) {
        try {
            return DATE_FMT.format(new Date(millis));
        } catch (Throwable t) {
            return String.valueOf(millis);
        }
    }

    public static String trimOneLine(String s, int maxChars) {
        if (s == null) return "";
        s = s.replace('\n', ' ').replace('\r', ' ').trim();
        if (s.length() <= maxChars) return s;
        return s.substring(0, Math.max(0, maxChars - 1)) + "…";
    }

    // --------- report text ---------
    /** Full (raw) report text. Good for COPY / dev dumps. */
    public static String pretty(CrashReport r) {
        StringBuilder sb = new StringBuilder(2048);
        sb.append("=== CRASH REPORT ===\n");
        sb.append("timeMillis: ").append(r.timeMillis).append('\n');
        sb.append("source: ").append(r.source).append('\n');
        sb.append("thread: ").append(r.threadName).append(" (").append(r.threadId).append(")\n");
        sb.append("gameVersion: ").append(r.gameVersion).append(" (").append(r.versionCode).append(")\n");
        sb.append("seed: ").append(r.dungeonSeed).append('\n');
        sb.append("challenges: ").append(r.dungeonChallenges).append('\n');
        sb.append("exception: ").append(r.exceptionType).append('\n');
        sb.append("message: ").append(r.exceptionMessage).append('\n');
        sb.append("fatalHint: ").append(r.fatalHint).append('\n');
        sb.append("\n--- STACK TRACE ---\n");
        sb.append(r.stackTrace != null ? r.stackTrace : "(no trace)");
        sb.append("\n=== END ===\n");
        return sb.toString();
    }

    /** Shortened (readable) report text for UI files. */
    public static String prettyShort(CrashReport r) {
        StringBuilder sb = new StringBuilder(2048);
        sb.append("=== CRASH REPORT ===\n");
        sb.append("time: ").append(formatTime(r.timeMillis)).append('\n');
        sb.append("source: ").append(r.source).append('\n');
        sb.append("thread: ").append(r.threadName).append(" (").append(r.threadId).append(")\n");
        sb.append("gameVersion: ").append(r.gameVersion).append(" (").append(r.versionCode).append(")\n");
        sb.append("seed: ").append(r.dungeonSeed).append('\n');
        sb.append("challenges: ").append(r.dungeonChallenges).append('\n');
        sb.append("exception: ").append(shortenType(r.exceptionType)).append('\n');
        sb.append("message: ").append(r.exceptionMessage).append('\n');
        sb.append("fatalHint: ").append(r.fatalHint).append('\n');

        String legend = traceLegend();
        if (!legend.isEmpty()) sb.append("legend: ").append(legend).append('\n');

        sb.append("\n--- STACK TRACE (shortened) ---\n");
        String st = shortenStackTrace(r.stackTrace);
        sb.append(st != null ? st : "(no trace)");
        sb.append("\n=== END ===\n");
        return sb.toString();
    }
}
