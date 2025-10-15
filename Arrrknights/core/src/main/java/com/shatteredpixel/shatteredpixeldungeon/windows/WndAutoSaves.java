package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.AutoSaveManager;
import com.shatteredpixel.shatteredpixeldungeon.utils.AutoSaveManager.Slot;
import com.watabou.noosa.Game;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WndAutoSaves extends Window {

    private static final int WIDTH = 140;
    private static final int ROW_H = 20;
    private static final int GAP   = 2;
    private boolean initialized = false;

    private int y;

    public WndAutoSaves(){
        this(GamesInProgress.curSlot);
        initialized = true;
    }

    public WndAutoSaves(int slot) {
        super();
        initialized = false;
        final int runSlot = slot;

        addRow("进入存档", AutoSaveManager.Slot.ENTER, runSlot);
        addRow("退出存档",  AutoSaveManager.Slot.EXIT,  runSlot);

        AutoSaveManager.Slot[] turnSlots = new AutoSaveManager.Slot[]{
                AutoSaveManager.Slot.TURN0, AutoSaveManager.Slot.TURN1, AutoSaveManager.Slot.TURN2
        };
        java.util.Arrays.sort(turnSlots, (a,b) ->
                Long.compare(AutoSaveManager.infoOf(runSlot, b).tsMillis, AutoSaveManager.infoOf(runSlot, a).tsMillis)
        );
        for (AutoSaveManager.Slot s : turnSlots) addRow("回合存档", s, runSlot);

        resize(WIDTH, y);
    }

    private void addRow(String title, AutoSaveManager.Slot slot, int runSlot) {
        AutoSaveManager.SlotInfo info = AutoSaveManager.infoOf(runSlot, slot);

        String ts = info.tsMillis > 0 ? new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(new Date(info.tsMillis)) : "time-unknown";

        String leftText = String.format("%s(T%dD%d)--%s", title, info.turn, info.depth, ts);

        RedButton left = new RedButton(leftText, 4) {
            @Override protected void onClick() {
                if (!info.present) return;
                hide();
                if (initialized) AutoSaveManager.loadSlotIntoCurrentRun(runSlot, slot);
                else AutoSaveManager.loadSlotAtStart(runSlot, slot);
            }
        };
        if (!info.present) left.enable(false);
        add(left);
        left.setRect(0, y > 0 ? y + 2 : 0, WIDTH - 44 - 2, ROW_H);

        RedButton export = new RedButton(Messages.get(this, "export")) {
            @Override protected void onClick() {
                if (!info.present) return;
                String payload = AutoSaveManager.exportSlotAsString(runSlot, slot);
                if (payload == null || payload.isEmpty()) {
                    Game.scene().add(new WndMessage(Messages.get(WndAutoSaves.class, "export_fail")));
                    return;
                }
                Game.platform.shareTextContent(payload, "autosave_slot" + runSlot + ".txt");
                Game.scene().add(new WndMessage(Messages.get(WndAutoSaves.class, "export_success")));
            }
        };
        export.enable(info.present);
        add(export);
        export.setRect(left.right() + 2, left.top(), 44, ROW_H);

        y = (int) left.bottom();
    }
}
