package com.shatteredpixel.shatteredpixeldungeon.items.bags;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.testtool.Generators;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;

import java.util.ArrayList;

public class EquipmentsBag extends Bag{
    {
        image = ItemSpriteSheet.UNDONE_MARK;
    }
    @Override
    public boolean canHold( Item item ) {
        if (item instanceof Generators){
            return super.canHold(item);
        } else {
            return false;
        }
    }

    public int capacity(){
        return 24;
    }

    @Override
    public int value() {
        return 40;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        return super.actions(hero);
    }
    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
    }
}
