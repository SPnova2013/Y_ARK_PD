package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.CatGun;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

/*
	The three commands of the cat gun. Two of them only make sense while Mon3tr is
	out, so their buttons are disabled otherwise.
 */
public class WndCatGunCommands extends Window {

    private static final int WIDTH = 130;
    private static final float GAP = 2;
    private static final int COUNT = 3;

    public interface Listener {
        //index 0..2, matching the command_N keys
        void onSelect(int index);
    }

    public WndCatGunCommands(final CatGun weapon, final Hero hero, final Listener listener) {

        super();

        IconTitle titlebar = new IconTitle();
        titlebar.icon(new ItemSprite(weapon.image(), null));
        titlebar.label(Messages.get(CatGun.class, "command_title"));
        titlebar.setRect(0, 0, WIDTH, 0);
        add(titlebar);

        RenderedTextBlock message = PixelScene.renderTextBlock(6);
        message.text(Messages.get(CatGun.class, "command_message"), WIDTH);
        message.setPos(titlebar.left(), titlebar.bottom() + GAP);
        add(message);

        float pos = message.bottom() + 3 * GAP;

        //only the reinforcement command works without Mon3tr
        boolean hasMon3tr = CatGun.findMon3tr() != null;

        for (int i = 0; i < COUNT; i++) {
            final int index = i;

            RedButton btnCommand = new RedButton(Messages.get(CatGun.class, "command_" + index + "_short"), 6) {
                @Override
                protected void onClick() {
                    hide();
                    listener.onSelect(index);
                }
            };
            btnCommand.leftJustify = true;
            btnCommand.multiline = true;
            btnCommand.setSize(WIDTH - 20, btnCommand.reqHeight() + 2);
            btnCommand.setRect(0, pos, WIDTH - 20, btnCommand.reqHeight() + 2);
            btnCommand.enable(index == 0 || hasMon3tr);
            add(btnCommand);

            IconButton info = new IconButton(Icons.get(Icons.INFO)) {
                @Override
                protected void onClick() {
                    GameScene.show(new WndTitledMessage(
                            new ItemSprite(weapon.image(), null),
                            Messages.get(CatGun.class, "command_" + index + "_name"),
                            Messages.get(CatGun.class, "command_" + index + "_desc")));
                }
            };
            info.setRect(WIDTH - 20, btnCommand.top() + (btnCommand.height() - 20) / 2, 20, 20);
            add(info);

            pos = btnCommand.bottom() + GAP;
        }

        RedButton btnCancel = new RedButton(Messages.get(CatGun.class, "command_cancel")) {
            @Override
            protected void onClick() {
                hide();
            }
        };
        btnCancel.setRect(0, pos, WIDTH, 18);
        add(btnCancel);

        resize(WIDTH, (int) btnCancel.bottom());
    }
}
