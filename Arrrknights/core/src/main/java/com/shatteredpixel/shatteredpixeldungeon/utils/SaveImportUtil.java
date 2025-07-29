package com.shatteredpixel.shatteredpixeldungeon.utils;

import com.badlogic.gdx.Gdx;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Base64;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.scenes.StartScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGameInProgress;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class SaveImportUtil {

    private SaveImportUtil() {}

    public static void tryImportFromClipboard(StartScene scene) {
        tryImport(Gdx.app.getClipboard().getContents(), scene);
    }

    public static void tryImport(String src, StartScene scene) {

        if (src == null || src.trim().isEmpty()) {
            Game.scene().add(new WndMessage("存档字串无效"));
            return;
        }

        try {
            String[] parts = src.trim()
                    .split(Pattern.quote(WndGameInProgress.SEP));

            if (parts.length == 0) throw new IllegalArgumentException();

            int slot = GamesInProgress.firstEmpty();
            if (slot == -1) {
                Game.scene().add(new WndMessage("没有空白存档"));
                return;
            }

            // make sure slot folder exists
            FileUtils.getFileHandle(GamesInProgress.gameFolder(slot)).mkdirs();

            for (String p : parts) {
                int cut = p.indexOf('=');
                if (cut <= 0) continue;

                String tag   = p.substring(0, cut);
                String b64   = p.substring(cut + 1);
                String json  = new String(Base64.decodeBase64(b64), StandardCharsets.UTF_8);
                Bundle bundle  = Bundle.readFromString(JsonCompress.decompressJson(json));

                if ("G".equals(tag)) {
                    FileUtils.bundleToFile(GamesInProgress.gameFile(slot), bundle);
                } else if (tag.startsWith("D")) {
                    int depth = Integer.parseInt(tag.substring(1));
                    FileUtils.bundleToFile(
                            GamesInProgress.depthFile(slot, depth), bundle);
                }
            }

            GamesInProgress.refreshAll();
            StartScene.EnsureRefresh(scene);

        } catch (Exception e) {
            Game.scene().add(new WndMessage("存档字串无效"));
        }
    }
}
