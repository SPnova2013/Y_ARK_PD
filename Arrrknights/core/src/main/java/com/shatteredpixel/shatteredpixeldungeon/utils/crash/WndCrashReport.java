package com.shatteredpixel.shatteredpixeldungeon.utils.crash;

import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.crash.bundle.CrashReport;
import com.watabou.noosa.Game;
import com.watabou.noosa.ui.Component;

public class WndCrashReport extends Window {

    private static final int WIDTH_P  = 135;
    private static final int HEIGHT_P = 200;

    private static final int WIDTH_L  = 220;
    private static final int HEIGHT_L = 140;

    private static final int GAP   = 2;
    private static final int BTN_H = 18;

    private static final int MAX_RENDER_CHARS = 12000;

    private final int w;
    private final int h;

    private RenderedTextBlock title;
    private RenderedTextBlock message;
    private RenderedTextBlock meta;
    private RenderedTextBlock legend;

    private ScrollPane tracePane;

    private RedButton btnCopy;
    private RedButton btnReports;
    private RedButton btnClose;

    private String fullText;

    public WndCrashReport(CrashReport report) {
        super();

        w = PixelScene.landscape() ? WIDTH_L : WIDTH_P;
        h = PixelScene.landscape() ? HEIGHT_L : HEIGHT_P;

        resize(w, h);

        fullText = CrashStore.pretty(report);

        title = PixelScene.renderTextBlock(9);
        title.hardlight(TITLE_COLOR);
        add(title);

        message = PixelScene.renderTextBlock(6);
        add(message);

        meta = PixelScene.renderTextBlock(6);
        add(meta);

        legend = PixelScene.renderTextBlock(6);
        legend.hardlight(0xAAAAAA);
        add(legend);

        tracePane = new ScrollPane(new Component());
        add(tracePane);

        btnCopy = new RedButton("COPY") {
            @Override protected void onClick() {
                try {
                    if (Gdx.app != null && Gdx.app.getClipboard() != null) {
                        Gdx.app.getClipboard().setContents(fullText != null ? fullText : "");
                    }
                } catch (Throwable ignored) {}
            }
        };
        add(btnCopy);

        btnReports = new RedButton("REPORTS") {
            @Override protected void onClick() {
                Game.scene().addToFront(new WndCrashReports());
            }
        };
        add(btnReports);

        btnClose = new RedButton("CLOSE") {
            @Override protected void onClick() {
                hide();
            }
        };
        add(btnClose);

        layoutChrome(report);
        updateTrace(report);
    }

    private void layoutChrome(CrashReport report) {

        String header = "CRASH: " + safe(CrashStore.shortenType(report.exceptionType));
        title.text(header, w);
        title.setPos(0, 0);
        PixelScene.align(title);

        String msg = safe(report.exceptionMessage);
        if ("(null)".equals(msg)) msg = "";
        message.text(msg.isEmpty() ? "(no message)" : msg, w);
        message.setPos(0, title.bottom() + GAP);
        PixelScene.align(message);

        String metaText =
                "Time: " + CrashStore.formatTime(report.timeMillis) + "\n" +
                        "Source: " + safe(report.source != null ? report.source.name() : null) + "\n" +
                        "Thread: " + safe(report.threadName) + " (" + report.threadId + ")\n" +
                        "Version: " + safe(report.gameVersion) + " (" + report.versionCode + ")\n" +
                        "Seed: " + report.dungeonSeed + "\n" +
                        "Challenges: " + report.dungeonChallenges + "\n" +
                        "Fatal: " + (report.fatalHint ? "YES" : "NO");

        meta.text(metaText, w);
        meta.setPos(0, message.bottom() + GAP);
        PixelScene.align(meta);

        String leg = CrashStore.traceLegend();
        legend.text(leg.isEmpty() ? "" : leg, w);
        legend.setPos(0, meta.bottom() + GAP);
        PixelScene.align(legend);

        // Buttons: COPY full width, then REPORTS/CLOSE row
        btnReports.setRect(0, h - BTN_H, (w - GAP) / 2f, BTN_H);
        PixelScene.align(btnReports);

        btnClose.setRect(btnReports.right() + GAP, h - BTN_H, w - (btnReports.right() + GAP), BTN_H);
        PixelScene.align(btnClose);

        btnCopy.setRect(0, btnReports.top() - GAP - BTN_H, w, BTN_H);
        PixelScene.align(btnCopy);

        float top = legend.bottom() + GAP * 2;
        float bottom = btnCopy.top() - GAP * 2;
        float paneH = bottom - top;
        if (paneH < 30) paneH = 30;

        tracePane.setRect(0, top, w, paneH);
    }

    private void updateTrace(CrashReport report) {
        Component content = tracePane.content();
        content.clear();

        String trace = CrashStore.shortenStackTrace(report.stackTrace);
        if (trace == null) trace = "(no stack trace)";

        if (trace.length() > MAX_RENDER_CHARS) {
            trace = trace.substring(0, MAX_RENDER_CHARS)
                    + "\n\n...(truncated for display; COPY copies full report)...\n";
        }

        RenderedTextBlock body = PixelScene.renderTextBlock(6);
        body.text(trace, w);
        body.breakLongTokens(true);
        body.setPos(0, 0);
        PixelScene.align(body);

        content.add(body);

        content.setSize(w, body.bottom());
        tracePane.setSize(tracePane.width(), tracePane.height());
        tracePane.scrollTo(0, 0);
    }

    private static String safe(String s) {
        return s == null ? "(null)" : s;
    }
}
