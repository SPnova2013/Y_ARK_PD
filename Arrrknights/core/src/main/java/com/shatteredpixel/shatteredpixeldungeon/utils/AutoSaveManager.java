package com.shatteredpixel.shatteredpixeldungeon.utils;

import com.badlogic.gdx.files.FileHandle;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Base64;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.TomorrowRogueNight;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGameInProgress;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;

import java.nio.charset.StandardCharsets;

public final class AutoSaveManager {

    public enum Slot { ENTER, EXIT, TURN0, TURN1, TURN2 }

    private AutoSaveManager() {}

    private static final String GAME_FILE = "game.dat";
    private static final String DEPTH_PREFIX = "depth";
    private static final String DEPTH_SUFFIX = ".dat";
    private static final String META_FILE = "meta.bundle";

    public static void saveOnLevelEnter() { snapshotTo(GamesInProgress.curSlot, Slot.ENTER); }
    public static void saveOnLevelExit()  { snapshotTo(GamesInProgress.curSlot, Slot.EXIT);  }
    public static void saveTurnBased()    { snapshotTo(GamesInProgress.curSlot, pickTurnSlot(GamesInProgress.curSlot)); }

    public static boolean hasSlot(Slot s) { return hasSlot(GamesInProgress.curSlot, s); }
    public static boolean hasSlot(int runSlot, Slot s) {
        return FileUtils.fileExists(dirFor(runSlot, s) + GAME_FILE);
    }

    public static String exportSlotAsString(Slot s) { return exportSlotAsString(GamesInProgress.curSlot, s); }
    public static String exportSlotAsString(int runSlot, Slot s) {
        try {
            String dir = dirFor(runSlot, s);
            if (!hasSlot(runSlot, s)) return null;

            StringBuilder sb = new StringBuilder();

            Bundle main = FileUtils.bundleFromFile(dir + GAME_FILE);
            String mainJson = JsonCompress.compressJson(FileUtils.bundleToString(main, false));
            sb.append("G=").append(Base64.encodeBase64String(mainJson.getBytes(StandardCharsets.UTF_8)));

            int maxD = Math.max(1, Dungeon.depth);
            for (int d = 1; d <= maxD; d++) {
                String p = dir + DEPTH_PREFIX + d + DEPTH_SUFFIX;
                if (!FileUtils.fileExists(p)) continue;
                Bundle depth = FileUtils.bundleFromFile(p);
                String depthJson = JsonCompress.compressJson(FileUtils.bundleToString(depth, false));
                sb.append(WndGameInProgress.SEP)
                        .append("D").append(d).append("=")
                        .append(Base64.encodeBase64String(depthJson.getBytes(StandardCharsets.UTF_8)));
            }
            return sb.toString();
        } catch (Exception e) {
            TomorrowRogueNight.reportException(e);
            return null;
        }
    }

    public static void loadSlotIntoCurrentRun(Slot s) { loadSlotIntoCurrentRun(GamesInProgress.curSlot, s); }
    public static void loadSlotIntoCurrentRun(int runSlot, Slot s) {
        try {
            String dir = dirFor(runSlot, s);
            if (!hasSlot(runSlot, s)) return;

            // Copy autosave -> normal slot files
            copy(dir + GAME_FILE, GamesInProgress.gameFile(runSlot));
            for (int d = 1; d <= 40; d++) {
                String p = dir + DEPTH_PREFIX + d + DEPTH_SUFFIX;
                if (FileUtils.fileExists(p)) {
                    copy(p, GamesInProgress.depthFile(runSlot, d));
                }
            }

            Game.switchScene(GameScene.class);
            Dungeon.loadGame(runSlot, true);
            Dungeon.level = Dungeon.loadLevel(runSlot);
            Dungeon.switchLevel(Dungeon.level, Dungeon.hero.pos);
        } catch (Exception e) {
            TomorrowRogueNight.reportException(e);
            Game.scene().add(new WndMessage("Failed to load autosave:\n" + e.getMessage()));
        }
    }

    public static void loadSlotAtStart(Slot s) { loadSlotIntoCurrentRun(GamesInProgress.curSlot, s); }
    public static void loadSlotAtStart(int runSlot, Slot s) {
        try {
            String dir = dirFor(runSlot, s);
            if (!hasSlot(runSlot, s)) return;

            copy(dir + GAME_FILE, GamesInProgress.gameFile(runSlot));
            for (int d = 1; d <= 40; d++) {
                String p = dir + DEPTH_PREFIX + d + DEPTH_SUFFIX;
                if (FileUtils.fileExists(p)) {
                    copy(p, GamesInProgress.depthFile(runSlot, d));
                }
            }

            GamesInProgress.curSlot = runSlot;
            Dungeon.hero = null;
            ActionIndicator.action = null;
            InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
            TomorrowRogueNight.switchScene(InterlevelScene.class);
        } catch (Exception e) {
            TomorrowRogueNight.reportException(e);
            Game.scene().add(new WndMessage("Failed to load autosave:\n" + e.getMessage()));
        }
    }

    public static void deleteAllForSlot(int runSlot) {
        FileUtils.deleteDir(slotRoot(runSlot)); // nukes /autosave/slotX/ completely
    }

    // ===== Internal snapshotting =====
    private static void snapshotTo(int runSlot, Slot type) {
        if (Dungeon.hero == null || !Dungeon.hero.isAlive()) return;

        try {
            Actor.fixTime();
            Dungeon.saveGame(GamesInProgress.curSlot);
            Dungeon.saveLevel(GamesInProgress.curSlot);
        } catch (Exception e) {
            TomorrowRogueNight.reportException(e);
        }
        finally {
            String dir = dirFor(runSlot, type);
            FileUtils.getFileHandle(dir).mkdirs();

            copy(GamesInProgress.gameFile(runSlot), dir + GAME_FILE);

            int maxD = Math.max(1, Dungeon.depth);
            for (int d = 1; d <= maxD; d++) {
                String src = GamesInProgress.depthFile(runSlot, d);
                if (FileUtils.fileExists(src)) copy(src, dir + DEPTH_PREFIX + d + DEPTH_SUFFIX);
            }

            try {
                Bundle meta = new Bundle();
                meta.put("slot", runSlot);
                meta.put("depth", Dungeon.depth);
                meta.put("turn", (int) Math.floor(Actor.totalTurns()));
                meta.put("ts", System.currentTimeMillis());
                FileUtils.bundleToFile(dir + META_FILE, meta);
            } catch (Exception e) {
                TomorrowRogueNight.reportException(e);
            }
        }
    }

    private static Slot pickTurnSlot(int runSlot) {
        long t0 = lastMod(dirFor(runSlot, Slot.TURN0) + GAME_FILE);
        long t1 = lastMod(dirFor(runSlot, Slot.TURN1) + GAME_FILE);
        long t2 = lastMod(dirFor(runSlot, Slot.TURN2) + GAME_FILE);
        if (t0 == 0 || (t0 <= t1 && t0 <= t2)) return Slot.TURN0;
        if (t1 == 0 || (t1 <= t0 && t1 <= t2)) return Slot.TURN1;
        return Slot.TURN2;
    }

    public static class SlotInfo {
        public boolean present;
        public int slotIndex;
        public int depth;
        public int turn;
        public long tsMillis;
    }

    public static SlotInfo infoOf(Slot s) { return infoOf(GamesInProgress.curSlot, s); }
    public static SlotInfo infoOf(int runSlot, Slot s) {
        SlotInfo info = new SlotInfo();
        info.slotIndex = runSlot;
        info.present = hasSlot(runSlot, s);
        if (!info.present) return info;

        try {
            Bundle meta = FileUtils.bundleFromFile(dirFor(runSlot, s) + META_FILE);
            info.depth = meta.getInt("depth");
            info.turn = meta.getInt("turn");
            info.tsMillis = meta.getLong("ts");
        } catch (Exception e) {
            info.depth = Math.max(1, Dungeon.depth);
            info.turn = (int) Math.floor(Actor.totalTurns());
            info.tsMillis = lastMod(dirFor(runSlot, s) + GAME_FILE);
        }
        return info;
    }

    private static String slotRoot(int runSlot) { return "autosave/slot" + runSlot + "/"; }

    private static String dirFor(int runSlot, Slot s) {
        switch (s) {
            case ENTER: return slotRoot(runSlot) + "enter/";
            case EXIT:  return slotRoot(runSlot) + "exit/";
            case TURN0: return slotRoot(runSlot) + "turn0/";
            case TURN1: return slotRoot(runSlot) + "turn1/";
            case TURN2: return slotRoot(runSlot) + "turn2/";
            default:    return slotRoot(runSlot);
        }
    }

    private static long lastMod(String path) {
        try {
            FileHandle f = FileUtils.getFileHandle(path);
            return f.exists() ? f.lastModified() : 0L;
        } catch (Throwable t) {
            return 0L;
        }
    }

    private static void copy(String srcPath, String dstPath) {
        FileHandle src = FileUtils.getFileHandle(srcPath);
        FileHandle dst = FileUtils.getFileHandle(dstPath);
        dst.writeBytes(src.readBytes(), false);
    }
}
