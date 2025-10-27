package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RingOfAmplified extends Ring {
    {
        icon = ItemSpriteSheet.Icons.RING_AMPLIFIED;
    }

    public String statsInfo() {

        if (isIdentified()){
            return Messages.get(this, "stats");
        } else {
            return Messages.get(this, "typical_stats");
        }
    }
    @Override
    public String upgradeStat1(int level){
        if (cursed && cursedKnown) level = Math.min(-1, level-3);
        return Integer.toString(level+1);
    }
    @Override
    protected RingBuff buff() {
        return new WandPowerup();
    }

    public static int DamageBonus( Char target ){
        return (int) getBonus(target, RingOfAmplified.WandPowerup.class);
    }


    public class WandPowerup extends RingBuff {
    }
}
