package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.sprites.YetiOperativeSprite;

public class YetiOperative extends Mob{
    {
        spriteClass = YetiOperativeSprite.class;

        HP = HT = 40;
        damageMax = 10;
        damageMin = 5;
        drMax = 5;
        drMin = 0;
        attackSkill = 20;
        defenseSkill = 15;

        EXP = 8;
        maxLvl = 16;

        immunities.add(Chill.class);
        immunities.add(Frost.class);
        immunities.add(ChampionEnemy.R2Blazing.class);
        immunities.add(ChampionEnemy.Blazing.class);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        if(enemy.buff(Frost.class) != null){
            damage *= 1.5;
        }
        return super.attackProc(enemy, damage);
    }
}
