package com.shatteredpixel.shatteredpixeldungeon.android;

import static com.google.common.io.ByteStreams.readFully;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.shatteredpixel.shatteredpixeldungeon.scenes.StartScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.SaveImportUtil;
import com.watabou.utils.DeviceCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class SaveImportActivity extends Activity {

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);

        Intent i = getIntent();
        Uri data = (Intent.ACTION_VIEW.equals(i.getAction())) ? i.getData()
                : (Intent.ACTION_SEND.equals(i.getAction()) ? i.getParcelableExtra(Intent.EXTRA_STREAM) : null);

        if (data == null) { finish(); return; }

        try (InputStream in = getContentResolver().openInputStream(data)) {
            assert in != null;
            byte[] raw = readFully(in);
            String txt = new String(raw, StandardCharsets.UTF_8);
            SaveImportUtil.tryImport(txt, StartScene.Instance);
        } catch (Exception e) {
            DeviceCompat.log("SAVEIMPORT", e.getMessage());
        }
        finish();
    }

    private static byte[] readFully(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(Math.max(32, in.available()));
        byte[] buffer = new byte[8192];
        int n;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }
}
