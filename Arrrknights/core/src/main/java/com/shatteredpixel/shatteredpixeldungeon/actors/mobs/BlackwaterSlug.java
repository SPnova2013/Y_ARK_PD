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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BlackWaterSlugSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.Bug_ASprite;
import com.watabou.utils.Random;

public class BlackwaterSlug extends Slug {

    {
        spriteClass = BlackWaterSlugSprite.class;

        HP = HT = 15;
        damageMaxIncRate = 45;
        damageMinIncRate = 35;
        drMaxIncRate = 10;
        EXP = 2;

        properties.add(Property.INFECTED);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);
        if (Random.Int(2) == 0) {
            Buff.affect(enemy, Ooze.class).set(Ooze.DURATION);
            enemy.sprite.burst(0x000000, 5);
        }

        return damage;
    }
}