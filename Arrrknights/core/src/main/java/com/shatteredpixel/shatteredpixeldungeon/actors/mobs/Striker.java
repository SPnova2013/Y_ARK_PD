package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.sprites.StrikerSprite;
import com.watabou.utils.Random;

public class Striker extends Mob{
    {
        spriteClass = StrikerSprite.class;

        HP = HT = 100;
        drMax = 24;
        drMin = 8;
        attackSkill = 30;
        defenseSkill = 20;

        EXP = 14;
        maxLvl = 26;

        loot = Gold.class;
        lootChance = 0.25f;
        immunities.add(Silence.class);
    }

    @Override
    protected boolean canAttack(Char enemy) {
        if (super.canAttack(enemy)) return true;//change from budding
        return this.fieldOfView[enemy.pos] && Dungeon.level.distance(this.pos, enemy.pos) <= 2;
    }

    @Override
    public int damageRoll() {
        if (HP <= HT/2) return Random.NormalIntRange(33+damageMinIncRate*rounds,45+damageMaxIncRate*rounds);
        return Random.NormalIntRange(22+damageMinIncRate*rounds,33+damageMaxIncRate*rounds);
    }
    @Override
    public void damage(int dmg, Object src) {
        if (Dungeon.isChallenged(Challenges.TACTICAL_UPGRADE)) dmg *= 0.8f;
        super.damage(dmg, src);
    }
}
