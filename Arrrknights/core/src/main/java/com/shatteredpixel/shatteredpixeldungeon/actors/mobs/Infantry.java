package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.sprites.InfantrySprite;
import com.watabou.utils.Random;

// 화람지심 보병
public class Infantry extends Mob {
    {
        spriteClass = InfantrySprite.class;

        HP = HT = 135;
        defenseSkill = 25;

        EXP = 15;
        maxLvl = 28;

        loot = Gold.class;
        lootChance = 0.28f;

        immunities.add(Silence.class);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 32, 44 );
    }

    @Override
    public int attackSkill( Char target ) {
        return 40;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 18);
    }

    @Override
    public void damage(int dmg, Object src) {

        if (HP == HT) {
            dmg = Math.min(HT-1, dmg);
        }

        super.damage(dmg, src);
    }
}
