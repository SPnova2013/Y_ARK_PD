package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FrostfangSprite;

public class Frostfang extends Mob{
    {
        spriteClass = FrostfangSprite.class;

        HP = HT = 30;
        damageMax = 15;
        drMax = 5;
        drMin = 0;
        attackSkill = 15;
        defenseSkill = 15;
        baseSpeed = 2f;

        EXP = 7;
        maxLvl = 15;

        loot = new MysteryMeat();
        lootChance = 0.167f;

        immunities.add(Chill.class);
        immunities.add(Frost.class);
        immunities.add(ChampionEnemy.R2Blazing.class);
        immunities.add(ChampionEnemy.Blazing.class);
        properties.add(Property.INFECTED);
    }
    @Override
    public int attackProc(Char enemy, int damage) {
        if(enemy.buff(Frost.class) != null){
            damage *= 1.3;
        }
        return super.attackProc(enemy, damage);
    }
}
