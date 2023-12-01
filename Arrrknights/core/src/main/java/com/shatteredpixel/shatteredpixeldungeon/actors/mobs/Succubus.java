/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Camouflage;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfWarp;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.LancerSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SuccubusSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class Succubus extends Mob {

    {
        spriteClass = LancerSprite.class;

        HP = HT = 95;
        viewDistance = Light.DISTANCE;

        EXP = 12;
        maxLvl = 25;
        baseSpeed = 1f;
        defenseSkill = 25;

        loot = Generator.Category.SCROLL;
        lootChance = 0.22f;

        properties.add(Property.SARKAZ);
        immunities.add(Silence.class);
        resistances.add(Cripple.class);
    }

    private int ASPlus;

    @Override
    public int damageRoll() {

        if (ASPlus != 0) return Random.NormalIntRange(25, 25 + ASPlus * 2);
        else return Random.NormalIntRange(18, 28);
    }
    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);
        ASPlus = 0;
        Buff.detach(this, Acceleration.class);

        return damage;
    }

    @Override
    public void move(int step, boolean travelling) {
        super.move(step,travelling);
        if (state == HUNTING && buff(Acceleration.class) == null) {
            Buff.affect(this, Acceleration.class, 15f);
            Buff.affect(this, Camouflage.class, 15f);
        }
        if (buff(Acceleration.class) != null) ASPlus += 3;
        if (ASPlus > 30) ASPlus = 30;
    }

    @Override
    protected boolean act() {
        if (paralysed == 1) ASPlus = 0;
        return super.act();
    }

    @Override
    public float speed() {
        return super.speed() * 1 + (ASPlus * (ASPlus / 3) / 30);
    }

    @Override
    public int attackSkill(Char target) {
        return 40 + (ASPlus * 2);
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 10); }

    @Override
    public void die(Object cause) {
        super.die(cause);
        if (Random.Int(0, 100) <= 3) {
            Dungeon.level.drop(Generator.random(Generator.Category.SKL_T2), pos).sprite.drop(pos);
        }
    }

    @Override
    protected Item createLoot() {
        Class<? extends Scroll> loot;
        do {
            loot = (Class<? extends Scroll>) Random.oneOf(Generator.Category.SCROLL.classes);
        } while (loot == ScrollOfIdentify.class || loot == ScrollOfUpgrade.class || loot == ScrollOfWarp.class);

        return Reflection.newInstance(loot);
    }

    {
        immunities.add(Charm.class);
    }

    private static final String SPEEDSKILL = "asplus";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SPEEDSKILL, ASPlus);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        ASPlus = bundle.getInt(SPEEDSKILL);
    }

    public static class Acceleration extends FlavourBuff {

        {
            type = buffType.POSITIVE;
            announced = true;
        }

        @Override
        public int icon() {
            return BuffIndicator.MIND_VISION;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.25f, 1.5f, 1f);
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc");
        }
    }
}