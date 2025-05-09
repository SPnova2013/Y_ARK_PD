package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Stamina;
import com.shatteredpixel.shatteredpixeldungeon.items.ScholarNotebook;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GloompincerSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HaundSprite;
import com.watabou.utils.Random;

public class Gloompincer extends Hound{
    {
        spriteClass = GloompincerSprite.class;

        HP = HT = 15;
        damageMax = 5;
        damageMin = 1;
        drMax = 10;
        drMaxIncRate = 17;
        drMaxInc = drMaxIncRate*rounds;
        drMin = 2;
        attackSkill = 12;
        defenseSkill = 5;

        EXP = 4;
        maxLvl = 9;

        loot = new MysteryMeat();
        lootChance = 0.167f;

        properties.add(Property.INFECTED);
    }
    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);
        if (Random.Int(2) == 0) {
            Buff.affect(enemy, Cripple.class, rounds);
            enemy.sprite.burst(0x000000, 5);
        }
        return damage;
    }
}
