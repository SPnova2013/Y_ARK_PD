package com.shatteredpixel.shatteredpixeldungeon.utils.crash.bundle;

import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public class CrashStrings implements Bundlable {

    public String fileName;
    public String stackTrace;
    public String message;

    public CrashStrings() {}

    public CrashStrings(String fileName, String stackTrace, String message) {
        this.fileName = fileName;
        this.stackTrace = stackTrace;
        this.message = message;
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        fileName = bundle.getString("fileName");
        stackTrace = bundle.getString("stackTrace");
        message = bundle.getString("message");
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put("fileName", fileName);
        bundle.put("stackTrace", stackTrace);
        bundle.put("message", message);
    }
}