package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CockTailThrowerSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.InfantrySprite;
import com.watabou.utils.Random;

public class CocktailThrower extends Mob{
    {
        spriteClass = CockTailThrowerSprite.class;

        HP = HT = 12;
        damageMax = 1;
        damageMaxIncRate = 35;
        damageMin = 1;
        drMax = 2;
        drMin = 0;
        attackSkill = 1;
        defenseSkill = 4;

        EXP = 2;
        maxLvl = 8;

        loot = PotionOfLiquidFlame.class;
        lootChance = 0.2f;

        immunities.add(Silence.class);
    }
    @Override
    protected boolean canAttack(Char enemy) {
        if (super.canAttack(enemy)) return true;
        return this.fieldOfView[enemy.pos] && Dungeon.level.distance(this.pos, enemy.pos) <= 3;
    }
    @Override
    public int attackProc(Char enemy, int damage) {
        Buff.affect(enemy, Burning.class).reignite(enemy, 1f);
        return super.attackProc(enemy, damage);
    }
}
