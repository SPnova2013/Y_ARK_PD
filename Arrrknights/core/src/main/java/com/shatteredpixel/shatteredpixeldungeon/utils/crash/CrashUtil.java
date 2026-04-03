package com.shatteredpixel.shatteredpixeldungeon.utils.crash;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class CrashUtil {
    private CrashUtil() {}

    public static String stackTraceToString(Throwable t) {
        if (t == null) return "(null)";
        StringWriter sw = new StringWriter(4096);
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    public static boolean isProbablyFatal(Throwable t) {
        if (t == null) return false;
        if (t instanceof OutOfMemoryError) return true;
        if (t instanceof StackOverflowError) return true;
        if (t instanceof InternalError) return true;
        return isProbablyFatal(t.getCause());
    }
}
