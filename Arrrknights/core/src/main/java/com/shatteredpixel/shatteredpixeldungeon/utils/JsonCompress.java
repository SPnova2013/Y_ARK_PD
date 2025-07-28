package com.shatteredpixel.shatteredpixeldungeon.utils;

public class JsonCompress {
    private static final String TOKEN_CLASS     = "~";
    private static final String TOKEN_PKG      = "^";
    private static final String TOKEN_TRUE     = "!t";
    private static final String TOKEN_FALSE    = "!f";

    // Adjust once if your project root package ever changes:
    private static final String PACKAGE_PREFIX = "com.shatteredpixel.shatteredpixeldungeon";

    public static String compressJson(String json) {
        return json
                .replace("__className", TOKEN_CLASS)
                .replace(PACKAGE_PREFIX, TOKEN_PKG)
                .replace("true", TOKEN_TRUE)
                .replace("false", TOKEN_FALSE);
    }

    public static String decompressJson(String json) {
        return json
                .replace(TOKEN_CLASS, "__className")
                .replace(TOKEN_PKG,   PACKAGE_PREFIX)
                .replace(TOKEN_TRUE,  "true")
                .replace(TOKEN_FALSE, "false");
    }

}
