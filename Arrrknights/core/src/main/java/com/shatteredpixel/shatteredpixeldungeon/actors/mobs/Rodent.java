package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HaundSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RodentSprite;
import com.watabou.utils.Random;

public class Rodent extends Hound{
    {
        spriteClass = RodentSprite.class;

        HP = HT = 15;
        damageMax = 6;
        drMax = 4;
        drMin = 0;
        attackSkill = 12;
        defenseSkill = 5;
        baseSpeed = 1.5f;

        EXP = 4;
        maxLvl = 9;

        loot = new MysteryMeat();
        lootChance = 0.167f;
        properties.add(Property.INFECTED);
    }
    @Override
    public int attackProc( Char enemy, int damage ) {
        damage = super.attackProc( enemy, damage );
        if (Random.Int( 2 ) == 0) {
            Buff.affect( enemy, Bleeding.class ).set( damage/2 );
        }

        return damage;
    }
}
