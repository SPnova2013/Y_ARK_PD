package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.AgniOfNilaSprite;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

/*
	尼卢火: a flame of nila that the avatar of the mahasattva calls down.
	It cannot move, cannot fight and cannot be hurt, and every one of them
	softens the blows the hero takes. It burns out on its own after a while.
 */
public class AgniOfNila extends Mob {

    //how many turns it burns for
    public static final int LIFETIME = 50;

    {
        spriteClass = AgniOfNilaSprite.class;

        alignment = Alignment.ALLY;
        state = PASSIVE;

        properties.add(Property.IMMOVABLE);

        HP = HT = 1;
        EXP = 0;
        lootChance = 0f;
    }

    private int life = LIFETIME;

    public int remaining() {
        return life;
    }

    @Override
    protected boolean act() {
        if (--life <= 0) {
            //fade out quietly: no death message and no on-kill triggers fire for the hero
            CellEmitter.get(pos).burst(ElmoParticle.FACTORY, 8);
            if (sprite != null) {
                sprite.killAndErase();
            }
            if (Dungeon.level != null) {
                Dungeon.level.mobs.remove(this);
            }
            Actor.remove(this);
            return true;
        }
        spend(TICK);
        return true;
    }

    //invulnerable: nothing can hurt it, it simply burns out
    @Override
    public int defenseSkill(Char enemy) {
        return INFINITE_EVASION;
    }

    @Override
    public void damage(int dmg, Object src) {
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

    public static void summon(int cell) {
        AgniOfNila fire = new AgniOfNila();
        fire.pos = cell;
        com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene.add(fire);
    }

    //every fire currently burning on this floor
    public static ArrayList<AgniOfNila> allFires() {
        ArrayList<AgniOfNila> fires = new ArrayList<>();
        if (Dungeon.level != null) {
            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                if (mob instanceof AgniOfNila && mob.isAlive()) {
                    fires.add((AgniOfNila) mob);
                }
            }
        }
        return fires;
    }

    public static int count() {
        return allFires().size();
    }

    private static final String LIFE = "life";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LIFE, life);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        life = bundle.getInt(LIFE);
    }
}
