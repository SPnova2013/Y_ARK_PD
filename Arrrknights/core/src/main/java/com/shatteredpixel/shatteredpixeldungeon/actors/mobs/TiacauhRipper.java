package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.sprites.EnragedSprite;
import com.watabou.utils.Random;

public class TiacauhRipper extends Mob {
    {
        spriteClass = EnragedSprite.class;

        HP = HT = 85;
        defenseSkill = 45;

        EXP = 15;
        maxLvl = 34;

        loot = Gold.class;
        lootChance = 0.25f;

        immunities.add(Silence.class);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 24, 38 );
    }

    @Override
    protected float attackDelay() {
        return super.attackDelay() * 0.4f;
    }

    @Override
    public int attackSkill( Char target ) {
        return 38;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 16);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        int dmgbouns = enemy.drRoll() / 4;
        dmgbouns = Math.min(dmgbouns, 9);
        damage += dmgbouns;
        return super.attackProc(enemy, damage);
    }
}
