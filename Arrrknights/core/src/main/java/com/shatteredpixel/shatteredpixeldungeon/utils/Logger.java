package com.shatteredpixel.shatteredpixeldungeon.utils;

import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.watabou.noosa.Game;
import com.watabou.noosa.Gizmo;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Logger extends Gizmo {
    private List<Map<String, String>> entries;
    private int maxEntries;
    private File logFile;
    private float time;

    private static float logExportInterval = 10;
    private static final String TAG = "Logger";

    //entries used to be flushed one by one, i.e. a file open/write/close for every
    //actor that spent time. Buffering them cuts that to a couple of writes per second.
    private static final int FLUSH_BATCH = 64;
    private static final long FLUSH_INTERVAL_MS = 2000;
    private long lastFlush = 0;
    private final String[] entryHeader;
    private boolean headerWritten = false;
    private final Object lock = new Object();

    public Logger(int maxEntries, String filename, String[] entryHeader) {
        DeviceCompat.log(TAG, "Logger Created");
        this.entries = new CopyOnWriteArrayList<>();
        this.maxEntries = maxEntries;
        this.time = logExportInterval;
        this.entryHeader = entryHeader;

        try {
            this.logFile = FileUtils.getFileHandle(filename).file();
            if (!this.logFile.exists()) {
                this.logFile.createNewFile();
            }
        } catch (Exception e) {
            DeviceCompat.log(TAG, "Failed to create file handle: " + e.getMessage());
        }
    }

    public static String getHMSM() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int millisecond = calendar.get(Calendar.MILLISECOND);
        return String.format(Locale.CHINA, "%02d:%02d:%02d:%03d", hour, minute, second, millisecond);
    }

    public synchronized void addEntry(Map<String, String> entryData) {
        entryData.put("timestamp", getHMSM());
        entryData.put("turn", String.valueOf((int) Actor.now()));
        entryData.put("depth", String.valueOf(Dungeon.depth));
        if (entryData.size() != entryHeader.length) {
            throw new IllegalArgumentException("Entry data does not match template length.");
        }
        boolean flush;
        synchronized (lock) {
            entries.add(entryData);
            if (entries.size() > maxEntries) {
                entries.remove(0);
            }
            flush = entries.size() >= FLUSH_BATCH
                    || System.currentTimeMillis() - lastFlush >= FLUSH_INTERVAL_MS;
        }
        if (flush) {
            logToFile();
        }
    }

    public synchronized void clearEntries() {
        synchronized (lock) {
            entries.clear();
        }
    }

    public synchronized void logToFile() {
        lastFlush = System.currentTimeMillis();
        if (entries.isEmpty()) {
            return;
        }
        FileHandle fileHandle = FileUtils.getFileHandle(this.logFile.getName());
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fileHandle.write(true)))) {
            if (!headerWritten) {
                writer.write(formatHeader() + "\n");
                headerWritten = true;
            }
            while (!entries.isEmpty()) {
                Map<String, String> entry = entries.get(0);
                entries.remove(0);
                writer.write(formatEntry(entry) + "\n");
            }
        } catch (IOException e) {
            DeviceCompat.log(TAG, "Error writing to file: " + e.getMessage());
        }
    }

    private String formatHeader() {
        StringBuilder sb = new StringBuilder();
        for (String key : entryHeader) {
            sb.append(key).append(",");
        }
        sb.setLength(sb.length() - 1); // Remove trailing comma
        return sb.toString();
    }

    private String formatEntry(Map<String, String> entry) {
        StringBuilder sb = new StringBuilder();
        for (String key : entryHeader) {
            if (entry.containsKey(key)) sb.append(entry.get(key));
            sb.append(",");
        }
        sb.setLength(sb.length() - 1); // Remove trailing comma
        return sb.toString();
    }

    public void exportLog(String filename, String method) {
        if ("clear".equals(method)) {
            clearEntries();
            Game.platform.clearText(filename);
        } else if ("share".equals(method)) {
            Game.platform.shareText(filename);
        }
    }

    @Override
    public void update() {
        super.update();
        time -= Game.elapsed;
        if (time <= 0) {
            DeviceCompat.log(TAG, "Exporting log to file");
            logToFile();
            time = logExportInterval;
        }
    }
}
