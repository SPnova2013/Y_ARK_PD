package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BlazingSunSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

/*
	A summoned sun. It never moves and never fights, it just stands there as a friendly
	obstacle and a beacon: the spear's blessing turns attacks made next to it into true damage.
 */
public class BlazingSun extends Mob {

    {
        spriteClass = BlazingSunSprite.class;

        alignment = Alignment.ALLY;
        state = PASSIVE;

        properties.add(Property.IMMOVABLE);

        EXP = 0;
        lootChance = 0f;
    }

    //the weapon level it was summoned with, used for health and defense
    private int power = 0;

    public void setting(int lvl) {
        power = lvl;
        HP = HT = 120 + lvl * 20;
        defenseSkill = 10 + lvl * 2;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(power / 2, 2 + power);
    }

    @Override
    protected boolean act() {
        spend(TICK);
        return true;
    }
    @Override
    public boolean canInteract(Char c) {
        return false;
    }
    @Override
    protected boolean getCloser(int target) {
        return true;
    }

    @Override
    protected boolean getFurther(int target) {
        return true;
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return false;
    }

    @Override
    public int attackSkill(Char target) {
        return 0;
    }

    @Override
    public int damageRoll() {
        return 0;
    }

    private static final String POWER = "power";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(POWER, power);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        power = bundle.getInt(POWER);
    }
}
