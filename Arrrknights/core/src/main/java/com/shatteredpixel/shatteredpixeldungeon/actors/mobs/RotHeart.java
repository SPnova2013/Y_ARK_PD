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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ExplosiveSpear;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RotHeartSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class RotHeart extends Mob {

	{
		spriteClass = RotHeartSprite.class;

		HP = HT = 80;
		defenseSkill = 0;

		EXP = 4;

		state = PASSIVE;

		properties.add(Property.IMMOVABLE);
		properties.add(Property.MINIBOSS);
	}

	@Override
	public void damage(int dmg, Object src) {
		//TODO: when effect properties are done, change this to FIRE
		if (src instanceof Burning) {
			destroy();
			sprite.die();
		} else {
			super.damage(dmg, src);
		}
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
		GameScene.add(Blob.seed(pos, 20, ToxicGas.class));

		return super.defenseProc(enemy, damage);
	}

	@Override
	public void beckon(int cell) {
		//do nothing
	}

	@Override
	protected boolean getCloser(int target) {
		return false;
	}

	@Override
	public void destroy() {
		super.destroy();
		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[Dungeon.level.mobs.size()])){
			if (mob instanceof RotLasher){
				mob.die(null);
			}
		}
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
		Dungeon.level.drop( new Rotberry.Seed(), pos ).sprite.drop();
	}

	@Override
	public boolean reset() {
		return true;
	}

	@Override
	public int damageRoll() {
		return 0;
	}

	@Override
	public int attackSkill( Char target ) {
		return 0;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 5);
	}
	
	{
		immunities.add( Paralysis.class );
		immunities.add( Amok.class );
		immunities.add( Sleep.class );
		immunities.add( ToxicGas.class );
		immunities.add( Terror.class );
		immunities.add( Vertigo.class );
	}

	public static class RotHeartSpawn extends Buff {
		private int cooldown;
		private int turns;

		public void set(int t) {
			this.turns = t;
			this.cooldown = t;
			spend(TICK);
		}

		@Override
		public boolean act() {
			cooldown--;
			if (cooldown <= 0) {
				spawnLasherIfPossible();
				cooldown = turns;
			}
			spend(TICK);
			return true;
		}

		private void spawnLasherIfPossible() {
			Char heart = target;
			int center = heart.pos;
			int bestPos = findSpawnPosition(center);
			if (bestPos != -1) {
				RotLasher lasher = new RotLasher();
				placePlant(Dungeon.level, bestPos, lasher);
			}
		}
		private int findSpawnPosition(int center) {
			boolean[] passable = new boolean[Dungeon.level.length()];
			for (int i = 0; i < Dungeon.level.length(); i++) {
				int t = Dungeon.level.map[i];
				passable[i] = Terrain.isPlantable(t) && !isAdjacentToLasher(i);
			}
			passable[center] = false;

			boolean[] visited = new boolean[Dungeon.level.length()];
			Queue<Integer> queue = new LinkedList<>();
			queue.offer(center);
			visited[center] = true;

			ArrayList<Integer> candidates = new ArrayList<>();
			boolean found = false;

			while (!queue.isEmpty() && !found) {
				int size = queue.size();
				for (int i = 0; i < size; i++) {
					int c = queue.poll();
					for (int d : PathFinder.NEIGHBOURS8) {
						int n = c + d;
						if (Dungeon.level.insideMap(n) && !visited[n]) {
							visited[n] = true;
							if (passable[n]) {
								candidates.add(n);
								found = true;
							} else {
								queue.offer(n);
							}
						}
					}
				}
			}
			if (!candidates.isEmpty()) {
				return candidates.get(Random.Int(candidates.size()));
			}
			return -1;
		}

		private boolean isAdjacentToLasher(int cell) {
			for (Mob mob : Dungeon.level.mobs) {
				if (mob instanceof RotLasher) {
					if (Dungeon.level.distance(cell, mob.pos) <= 1) {
						return true;
					}
				}
			}
			return false;
		}

		// 存储
		private static final String CD = "cooldown";
		private static final String TURNS = "turns";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(CD, cooldown);
			bundle.put(TURNS, turns);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			cooldown = bundle.getInt(CD);
			turns = bundle.getInt(TURNS);
		}
	}

	private static void placePlant(Level level, int pos, Mob plant){
		plant.pos = pos;
		GameScene.add(plant);
		Actor.add(plant);

		for(int i : PathFinder.NEIGHBOURS8) {
			int terr = level.map[pos + i];
			if (Terrain.isPlantable(terr)){
				Painter.set(level, pos + i, Terrain.HIGH_GRASS);
				GameScene.updateMap( pos+i );
			}
		}
	}
}
