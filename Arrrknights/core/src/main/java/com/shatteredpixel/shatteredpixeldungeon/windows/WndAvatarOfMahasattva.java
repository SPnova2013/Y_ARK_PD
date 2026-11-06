package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.KollamSword;
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
	The fourfold avatar of the mahasattva. Each button is one of the four vows:
	the button itself carries the summary, its info button carries the details.
 */
public class WndAvatarOfMahasattva extends Window {

    private static final int WIDTH = 130;
    private static final float GAP = 2;
    private static final int COUNT = 4;

    public interface Listener {
        //index 0..3, matching the skill_N keys
        void onSelect(int index);
    }

    public WndAvatarOfMahasattva(final KollamSword weapon, final Hero hero, final Listener listener) {

        super();

        IconTitle titlebar = new IconTitle();
        titlebar.icon(new ItemSprite(weapon.image(), null));
        titlebar.label(Messages.get(KollamSword.class, "avatar_title"));
        titlebar.setRect(0, 0, WIDTH, 0);
        add(titlebar);

        RenderedTextBlock message = PixelScene.renderTextBlock(6);
        message.text(Messages.get(KollamSword.class, "avatar_message"), WIDTH);
        message.setPos(titlebar.left(), titlebar.bottom() + GAP);
        add(message);

        float pos = message.bottom() + 3 * GAP;

        for (int i = 0; i < COUNT; i++) {
            final int index = i;

            RedButton btnSkill = new RedButton(Messages.get(KollamSword.class, "skill_" + index + "_short"), 6) {
                @Override
                protected void onClick() {
                    hide();
                    listener.onSelect(index);
                }
            };
            btnSkill.leftJustify = true;
            btnSkill.multiline = true;
            btnSkill.setSize(WIDTH - 20, btnSkill.reqHeight() + 2);
            btnSkill.setRect(0, pos, WIDTH - 20, btnSkill.reqHeight() + 2);
            add(btnSkill);

            IconButton info = new IconButton(Icons.get(Icons.INFO)) {
                @Override
                protected void onClick() {
                    GameScene.show(new WndTitledMessage(
                            new ItemSprite(weapon.image(), null),
                            Messages.get(KollamSword.class, "skill_" + index + "_name"),
                            Messages.get(KollamSword.class, "skill_" + index + "_desc")));
                }
            };
            info.setRect(WIDTH - 20, btnSkill.top() + (btnSkill.height() - 20) / 2, 20, 20);
            add(info);

            pos = btnSkill.bottom() + GAP;
        }

        RedButton btnCancel = new RedButton(Messages.get(KollamSword.class, "avatar_cancel")) {
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
