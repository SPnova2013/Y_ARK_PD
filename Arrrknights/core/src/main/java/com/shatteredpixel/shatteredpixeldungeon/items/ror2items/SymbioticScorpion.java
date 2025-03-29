package com.shatteredpixel.shatteredpixeldungeon.items.ror2items;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PermanentArmorReduction;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;


public class SymbioticScorpion extends ROR2item{
    {
        tier = 3;
        image = ItemSpriteSheet.SYMBIOTIC_SCORPION;
    }
    @Override
    public int attackProc(Char attacker, Char defender, int damage ) {
        Buff.affect(defender, PermanentArmorReduction.class).add(1);
        return super.attackProc(attacker,defender,damage);
    }
    @Override
    protected ROR2itemBuff passiveBuff() {
        return new SymbioticScorpionBuff();
    }

    public class SymbioticScorpionBuff extends ROR2itemBuff{}
}
