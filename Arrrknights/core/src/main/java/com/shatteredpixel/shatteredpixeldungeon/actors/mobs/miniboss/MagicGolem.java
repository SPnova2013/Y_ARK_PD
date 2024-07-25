package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.miniboss;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM200;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.NewGameItem.Certificate;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfSacrifice;
import com.shatteredpixel.shatteredpixeldungeon.sprites.Red_golemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.S_GolemSprite;
import com.watabou.utils.Random;

public class MagicGolem extends Mob {
    {
        spriteClass = Red_golemSprite.class;

        HP = HT = 999;
        defenseSkill = 12;

        EXP = 25;
        maxLvl = 25;

        state = PASSIVE;

        properties.add(Property.INORGANIC);
        properties.add(Property.MINIBOSS);
        immunities.add(Vertigo.class);
        immunities.add(Silence.class);
        immunities.add(Amok.class);
    }

    @Override
    public int attackSkill(Char target) {
        return 20;
    }

    @Override
    public int damageRoll() {
        return 90;
    }

    @Override
    public int drRoll() {
        return Random.IntRange(0,20);
    }

    @Override
    public float speed() {
        return super.speed() * 0.5f;
    }

    @Override
    public void damage(int dmg, Object src) {
        dmg /= 10;
        if (state == PASSIVE) state = HUNTING;
        super.damage(dmg, src);
    }

    @Override
    public void die(Object cause) {
        super.die(cause);

        Dungeon.level.drop(new ScrollOfSacrifice(), pos).sprite.drop(pos);
        Dungeon.level.drop(new Certificate(1), pos).sprite.drop(pos);
    }
    @Override
    public boolean hasNotebookSkill(){ return true;}
}
