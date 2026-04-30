package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.sprites.StrikerSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.Tiacauh_lancerSprite;
import com.watabou.utils.Random;

public class TiacauhLancer extends Mob {
    {
        spriteClass =  Tiacauh_lancerSprite.class;

        HP = HT = 110;
        damageMax = 38;
        damageMaxIncRate = 20;
        damageMin = 30;
        damageMinIncRate = 16;
        drMax = 16;
        drMin = 0;
        attackSkill = 36;
        defenseSkill = 15;

        EXP = 14;
        maxLvl = 30;

        immunities.add(Silence.class);
    }

    @Override
    protected boolean canAttack(Char enemy) {
        if (super.canAttack(enemy)) return true;//change from budding
        return this.fieldOfView[enemy.pos] && Dungeon.level.distance(this.pos, enemy.pos) <= 3;
    }
    @Override
    protected float attackDelay() {
        return super.attackDelay() * 0.5f;
    }
    @Override
    public void damage(int dmg, Object src) {
        if (src == Burning.class) dmg *= 2;
        super.damage(dmg, src);
    }

}
