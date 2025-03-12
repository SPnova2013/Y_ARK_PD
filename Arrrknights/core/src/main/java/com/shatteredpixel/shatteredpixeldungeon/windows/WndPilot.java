package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroAction;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Obsidian;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Game;

public class WndPilot extends Window {
    private static final int WIDTH      = 120;
    private static final int BTN_HEIGHT = 20;
    private static final int GAP        = 2;

    public WndPilot() {

        super();

        IconTitle titlebar = new IconTitle();
        if (Dungeon.DLC == Dungeon.SIESTA){
            titlebar.icon( new ItemSprite(ItemSpriteSheet.OBSIDIAN, null ));
            titlebar.label( Messages.get(this, "siesta_name"));
        }
        if (Dungeon.DLC == Dungeon.SARGON) {
            titlebar.icon( new ItemSprite(ItemSpriteSheet.AMULET, null ));
            titlebar.label( Messages.get(this, "gavial_name"));
        }
        else if(Dungeon.DLC == Dungeon.ROR){
            titlebar.icon( new ItemSprite(ItemSpriteSheet.TOUGHER_TIMES, null ));
            titlebar.label( Messages.get(this, "ror_name"));
        }
        else {titlebar.icon( new ItemSprite(ItemSpriteSheet.UNDONE_MARK, null ));
            titlebar.label( Messages.get(this, "unknown_name"));}
        titlebar.setRect( 0, 0, WIDTH, 0 );
        add( titlebar );

        RenderedTextBlock message;
        message = PixelScene.renderTextBlock( Messages.get(this, "unknown"), 6 );
        if (Dungeon.DLC == Dungeon.SARGON)  message = PixelScene.renderTextBlock( Messages.get(this, "gavial"), 6 );
        if (Dungeon.DLC == Dungeon.SIESTA) message = PixelScene.renderTextBlock( Messages.get(this, "siesta"), 6 );
        if (Dungeon.DLC == Dungeon.ROR) message = PixelScene.renderTextBlock( Messages.get(this, "ror"), 6 );
        message.maxWidth(WIDTH);
        message.setPos(0, titlebar.bottom() + GAP);
        add( message );

        RedButton btnReward = new RedButton( Messages.get(this, "move") ) {
            @Override
            protected void onClick() {
                if(Dungeon.DLC == Dungeon.ROR){ GameScene.show(new WndMessage("不许去!"));}
                else move();
            }
            @Override
            protected boolean onLongClick() {
                move();
                return true;
            }
        };
        btnReward.setRect( 0, message.top() + message.height() + GAP, WIDTH, BTN_HEIGHT );
        add( btnReward );

        resize( WIDTH, (int)btnReward.bottom() );
    }

    private void move() {
        hide();
        InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
        Game.switchScene( InterlevelScene.class );
    }
}
