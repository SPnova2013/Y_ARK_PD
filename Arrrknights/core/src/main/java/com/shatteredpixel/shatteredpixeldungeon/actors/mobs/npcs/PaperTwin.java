package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.sprites.KazemaruSprite;
import com.watabou.utils.Bundle;

/*
	A paper twin folded in the shape of the hero. It fights on its own,
	and everything it can do is copied from the hero: life, armor, damage,
	accuracy and evasion.
 */
public class PaperTwin extends Mob {

    {
        spriteClass = KazemaruSprite.class;

        alignment = Alignment.ALLY;
        state = HUNTING;

        flying = true;

        EXP = 0;
        lootChance = 0f;
    }

    //life is copied once when it is folded, everything else follows the hero live
    private int maxLife = 1;

    public void setup(Hero hero) {
        maxLife = Math.max(1, hero.HT);
        HP = HT = maxLife;
    }

    private Hero hero() {
        return Dungeon.hero != null && Dungeon.hero.isAlive() ? Dungeon.hero : null;
    }

    @Override
    public int damageRoll() {
        Hero hero = hero();
        return hero == null ? 1 : hero.damageRoll();
    }

    @Override
    public int attackSkill(Char target) {
        Hero hero = hero();
        return hero == null ? 10 : hero.attackSkill(target);
    }

    @Override
    public int defenseSkill(Char enemy) {
        Hero hero = hero();
        return hero == null ? 10 : hero.defenseSkill(enemy);
    }

    @Override
    public int drRoll() {
        Hero hero = hero();
        return hero == null ? 0 : hero.drRoll();
    }

    private static final String MAX_LIFE = "max_life";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(MAX_LIFE, maxLife);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        maxLife = bundle.getInt(MAX_LIFE);
    }
}
