package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RazorfrostSprite;

public class Razorfrost extends Frostfang{
    {
        spriteClass = RazorfrostSprite.class;
    }
    @Override
    public int attackProc(Char enemy, int damage) {
        if(enemy.buff(Frost.class) != null){
            damage *= 1.5;
        }
        return super.attackProc(enemy, damage);
    }
}
