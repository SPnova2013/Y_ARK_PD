package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.sprites.Striker_EliteSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.Tiacauh_ImpalerSprite;
import com.watabou.utils.Random;

public class TiacauhSniper extends TiacauhLancer {
    {
        spriteClass = Tiacauh_ImpalerSprite.class;

        HP = HT = 125;
        defenseSkill = 16;

        EXP = 17;
        maxLvl = 30;

        immunities.add(Silence.class);


        loot = new PotionOfHealing();
        lootChance = 1f;
    }

    @Override
    protected boolean canAttack(Char enemy) {
        if (super.canAttack(enemy)) return true;//change from budding
        return this.fieldOfView[enemy.pos] && Dungeon.level.distance(this.pos, enemy.pos) <= 4;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(30,42);
    }

    @Override
    public void damage(int dmg, Object src) {
        if (src == Burning.class) dmg *= 2;
        super.damage(dmg, src);
    }
}
