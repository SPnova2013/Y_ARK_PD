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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RotLasherSprite;
import com.watabou.utils.Random;

import java.util.HashSet;

public class RotLasher extends Mob {

	{
		spriteClass = RotLasherSprite.class;

		HP = HT = 40;
		damageMax = 15;
		damageMin = 8;
		drMax = 8;
		drMin = 0;
		attackSkill = 15;
		defenseSkill = 0;

		EXP = 1;

		loot = Generator.Category.SEED;
		lootChance = 1f;

		state = WANDERING = new Waiting();

		properties.add(Property.IMMOVABLE);
		properties.add(Property.MINIBOSS);
	}
	public RotLasher(){
		super();
		HP = HT = Dungeon.roundedDepth * 5;
		damageMax = Dungeon.roundedDepth * 2;
		damageMin = Dungeon.roundedDepth;
		drMax = Dungeon.roundedDepth;
		attackSkill = Dungeon.roundedDepth * 2;
		maxLvl = Dungeon.roundedDepth;
	}

	@Override
	protected boolean act() {
		if (enemy == null || !Dungeon.level.adjacent(pos, enemy.pos)) {
			HP = Math.min(HT, HP + 3);
		}
		return super.act();
	}

	@Override
	public void damage(int dmg, Object src) {
		if (src instanceof Burning) {
			destroy();
			sprite.die();
		} else {
			super.damage(dmg, src);
		}
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		damage = super.attackProc( enemy, damage );
		Buff.affect( enemy, Cripple.class, 2f );
		return super.attackProc(enemy, damage);
	}

	@Override
	public boolean reset() {
		return true;
	}

	@Override
	protected boolean getCloser(int target) {
		return true;
	}

	@Override
	protected boolean getFurther(int target) {
		return true;
	}
	{
		immunities.add( ToxicGas.class );
	}

	private class Waiting extends Mob.Wandering{}

	@Override
	protected Char chooseEnemy() {
		Terror terror = buff(Terror.class);
		if (terror != null) {
			Char source = (Char) Actor.findById(terror.object);
			if (source != null) {
				return source;
			}
		}

		HashSet<Char> enemies = new HashSet<>();

		if (Dungeon.hero.isAlive() && fieldOfView[Dungeon.hero.pos] && Dungeon.hero.invisible <= 0) {
			if (!Dungeon.hero.isInvulnerable(getClass())) {
				enemies.add(Dungeon.hero);
			}
		}

		for (Mob mob : Dungeon.level.mobs) {
			if (mob == this || mob instanceof RotLasher || mob instanceof RotHeart) {
				continue;
			}
			if (mob.isAlive() && fieldOfView[mob.pos] && mob.invisible <= 0) {
				if (!mob.isInvulnerable(getClass())) {
					enemies.add(mob);
				}
			}
		}

		Charm charm = buff(Charm.class);
		if (charm != null) {
			Char source = (Char) Actor.findById(charm.object);
			if (source != null && enemies.contains(source) && enemies.size() > 1) {
				enemies.remove(source);
			}
		}

		if (enemies.isEmpty()) {
			return null;
		}

		Char closest = null;
		for (Char curr : enemies) {
			if (closest == null
					|| Dungeon.level.distance(pos, curr.pos) < Dungeon.level.distance(pos, closest.pos)
					|| (Dungeon.level.distance(pos, curr.pos) == Dungeon.level.distance(pos, closest.pos) && curr == Dungeon.hero)) {
				closest = curr;
			}
		}
		return closest;
	}
}
