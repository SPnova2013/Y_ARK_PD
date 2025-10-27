package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.text.DecimalFormat;

public class RingOfCommand extends Ring {
    {
        icon = ItemSpriteSheet.Icons.RING_COMMAND;
    }

    public String statsInfo() {
        if (isIdentified()){
            return Messages.get(this, "stats", new DecimalFormat("#.##").format(100f * (Math.pow(1.15f, soloBuffedBonus()) - 1f)));
        } else {
            return Messages.get(this, "typical_stats", new DecimalFormat("#.##").format(15f));
        }
    }

    @Override
    public String upgradeStat1(int level){
        if (cursed && cursedKnown) level = Math.min(-1, level-3);
        return new DecimalFormat("#.##").format(100f * (Math.pow(1.15f, level+1)-1f)) + "%";
    }
    @Override
    protected RingBuff buff( ) {
        return new Command();
    }

    public static float damageMultiplier( Char target ){
        return (float)Math.pow(1.15f, getBuffedBonus(target, Command.class));
    }

    public class Command extends RingBuff {
    }
}
