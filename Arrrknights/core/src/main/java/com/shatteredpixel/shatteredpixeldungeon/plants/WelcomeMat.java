package com.shatteredpixel.shatteredpixeldungeon.plants;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

/*
	迎宾踏垫: laid down by the trap deployer.

	It lives in the level's plant table, which gives it everything a trap could not:
	a real tile sprite, several mats at once, and no interaction with terrain,
	pathing or save/load across floors. Friends simply walk over it.
 */
public class WelcomeMat extends Plant {

    //terrain_features.png, row 18 col 0. plants are drawn at image + 7*16
    public static final int IMAGE = 32;

    public static final float PARALYSIS = 2f;

    //the blow is rolled when the mat is laid, and kept with it
    private int damage = 1;

    {
        image = IMAGE;
    }

    public WelcomeMat setDamage(int damage) {
        this.damage = Math.max(1, damage);
        return this;
    }

    @Override
    public void trigger() {
        Char ch = Actor.findChar(pos);

        //the hero and her allies only walk over it
        if (ch == null || ch instanceof Hero || ch.alignment == Char.Alignment.ALLY) {
            return;
        }

        //a mat is spent the moment it is stepped on
        wither();
        activate(ch);
    }

    @Override
    public void activate(Char ch) {
        if (ch == null) {
            return;
        }

        CellEmitter.get(pos).burst(BlastParticle.FACTORY, 8);
        Sample.INSTANCE.play(Assets.Sounds.BLAST);
        GameScene.flash(0x60FFCC66);

        ch.damage(damage, Dungeon.hero);

        if (ch.isAlive()) {
            Buff.affect(ch, Paralysis.class, PARALYSIS);
        }
    }

    private static final String DAMAGE = "damage";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(DAMAGE, damage);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        damage = bundle.getInt(DAMAGE);
    }
}
