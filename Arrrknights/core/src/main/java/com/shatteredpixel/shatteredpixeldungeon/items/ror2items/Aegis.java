package com.shatteredpixel.shatteredpixeldungeon.items.ror2items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Aegis extends ROR2item{
    {
        tier = 3;

        image = ItemSpriteSheet.AEGIS;
    }

    public static void addShield(float HealAmount){
        int targetShield = Math.max(0,Math.round((HealAmount-(Dungeon.hero.HT-Dungeon.hero.HP))));
        if(targetShield>0) Buff.affect(Dungeon.hero, Barrier.class).incShield(targetShield);
    }

    @Override
    protected ROR2itemBuff passiveBuff() {
        return new AegisBuff();
    }

    public class AegisBuff extends ROR2itemBuff{}

}