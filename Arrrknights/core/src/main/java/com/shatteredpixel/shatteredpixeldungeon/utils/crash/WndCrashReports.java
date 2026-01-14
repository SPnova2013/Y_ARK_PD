package com.shatteredpixel.shatteredpixeldungeon.utils.crash;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.crash.bundle.CrashReport;
import com.watabou.noosa.Game;
import com.watabou.noosa.ui.Button;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.DeviceCompat;

import java.util.ArrayList;

public class WndCrashReports extends Window {

    private static final int WIDTH_P  = 135;
    private static final int HEIGHT_P = 200;

    private static final int WIDTH_L  = 220;
    private static final int HEIGHT_L = 130;

    private static final int GAP   = 2;
    private static final int BTN_H = 18;

    private final int w;
    private final int h;

    private RenderedTextBlock title;
    private ScrollPane list;

    private RedButton btnClear;
    private RedButton btnClose;

    public WndCrashReports() {
        super();

        w = PixelScene.landscape() ? WIDTH_L : WIDTH_P;
        h = PixelScene.landscape() ? HEIGHT_L : HEIGHT_P;

        resize(w, h);

        title = PixelScene.renderTextBlock(9);
        title.hardlight(TITLE_COLOR);
        add(title);

        list = new ScrollPane(new Component());
        add(list);

        btnClear = new RedButton("CLEAR ALL") {
            @Override protected void onClick() {
                CrashStore.clearAllSaved();
                updateList();
            }
        };
        add(btnClear);

        btnClose = new RedButton("CLOSE") {
            @Override protected void onClick() {
                hide();
            }
        };
        add(btnClose);

        layoutChrome();
        updateList();
    }

    private void layoutChrome() {

        title.text("CRASH REPORTS", w);
        title.setPos(0, 0);
        PixelScene.align(title);

        btnClear.setRect(0, h - BTN_H, (w - GAP) / 2f, BTN_H);
        PixelScene.align(btnClear);

        btnClose.setRect(btnClear.right() + GAP, h - BTN_H, w - (btnClear.right() + GAP), BTN_H);
        PixelScene.align(btnClose);

        float top = title.bottom() + GAP * 2;
        float bottom = btnClear.top() - GAP * 2;
        float paneH = bottom - top;
        if (paneH < 30) paneH = 30;

        list.setRect(0, top, w, paneH);
    }

    private void updateList() {
        Component content = list.content();
        content.clear();

        ArrayList<CrashStore.SavedMeta> metas = CrashStore.listSavedBundles();

        float y = 0;

        if (metas.isEmpty()) {
            RenderedTextBlock empty = PixelScene.renderTextBlock(6);
            empty.text("(no saved crash reports)", w);
            empty.setPos(0, 0);
            PixelScene.align(empty);
            content.add(empty);
            y = empty.bottom();
        } else {
            for (CrashStore.SavedMeta meta : metas) {
                CrashRow row = new CrashRow(meta, w);
                content.add(row);
                row.setRect(0, y, w, row.preferredHeight());
                y = row.bottom() + GAP;
                DeviceCompat.log("Yvalue after adding row: " + y);
            }
        }

        content.setSize(w, y);
        list.setSize(list.width(), list.height());
        DeviceCompat.log("List size after update: " + list.content().height());
        list.scrollTo(0, 0);
    }

    private static class CrashRow extends StyledButton {

        private final CrashStore.SavedMeta meta;
        private final int w;

        private RenderedTextBlock t;
        private RenderedTextBlock sub;

        CrashRow(CrashStore.SavedMeta meta, int w) {
            super(Chrome.Type.GREY_BUTTON_TR, "");
            this.meta = meta;
            this.w = w;

            t = PixelScene.renderTextBlock(7);
            t.hardlight(Window.TITLE_COLOR);
            add(t);

            sub = PixelScene.renderTextBlock(6);
            add(sub);

            t.text(meta.title(), w);
            sub.text(meta.subtitle(), w);
        }

        float preferredHeight() {
            t.maxWidth(w);
            sub.maxWidth(w);
            float th = Math.max(8, t.height());
            float sh = Math.max(8, sub.height());
            return th + 2 + sh + 2;
        }

        @Override
        protected void layout() {
            super.layout();

            t.maxWidth((int) width);
            t.setPos(4, y + 4);
            PixelScene.align(t);

            sub.maxWidth((int) width);
            sub.setPos(4, t.bottom() + 2);
            PixelScene.align(sub);
        }

        @Override
        protected void onClick() {
            CrashReport report = CrashStore.loadFromBundleFile(meta.bundleFile);
            if (report != null) {
                Game.scene().addToFront(new WndCrashReport(report));
            }
        }
    }
}
