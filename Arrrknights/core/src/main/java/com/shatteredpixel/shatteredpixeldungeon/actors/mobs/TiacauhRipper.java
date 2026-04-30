package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.sprites.EnragedSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.Tiacauh_RipperSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.Tiacauh_fanaticSprite;
import com.watabou.utils.Random;

public class TiacauhRipper extends Mob {
    {
        spriteClass = Tiacauh_RipperSprite.class;

        HP = HT = 85;
        damageMax = 38;
        damageMaxIncRate = 16;
        damageMin = 24;
        damageMinIncRate = 13;
        drMax = 16;
        drMin = 0;
        attackSkill = 38;
        defenseSkill = 45;

        EXP = 16;
        maxLvl = 34;

        immunities.add(Silence.class);
    }
    @Override
    protected float attackDelay() {
        return super.attackDelay() * 0.4f;
    }
    @Override
    public int attackProc(Char enemy, int damage) {
        int dmgbouns = enemy.drRoll() / 4;
        dmgbouns = Math.min(dmgbouns, 9+9*rounds);
        damage += dmgbouns;
        return super.attackProc(enemy, damage);

    }

    @Override
    public void damage(int dmg, Object src) {
        if (src == Burning.class) dmg *= 2;
        super.damage(dmg, src);
    }
}
