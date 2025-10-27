package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.text.DecimalFormat;

public class RingOfAssassin extends Ring {
    {
        icon = ItemSpriteSheet.Icons.RING_SURPRISE;
    }

    public String statsInfo() {
        if (isIdentified()){
            return Messages.get(this, "stats",
                    new DecimalFormat("#.##").format(100f * (Math.pow(1.06f, soloBuffedBonus()) - 1f)),2*soloBuffedBonus());
        } else {
            return Messages.get(this, "typical_stats",
                    new DecimalFormat("#.##").format(6f));
        }
    }
    @Override
    public String upgradeStat1(int level){
        if (cursed && cursedKnown) level = Math.min(-1, level-3);
        return new DecimalFormat("#.##").format(100f * (Math.pow(1.06f, level+1)-1f)) + "%";
    }
    @Override
    public String upgradeStat2(int level){
        if (cursed && cursedKnown) level = Math.min(-1, level-3);
        return new DecimalFormat("#.##").format(100f * (0.02f * level+1)) + "%";
    }
    @Override
    protected Ring.RingBuff buff( ) {
        return new Suprise();
    }

    public static float supriseattackbouns( Char target ){
        return (float)Math.pow(1.06f, getBuffedBonus(target, RingOfAssassin.Suprise.class));
    }

    public static boolean Assassin_Curse( Char target ){
        return getCursed(target,RingOfAssassin.Suprise.class);
    }

    public class Suprise extends Ring.RingBuff {
    }
}
