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

package com.shatteredpixel.shatteredpixeldungeon.plants;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AdrenalineSurge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PlantBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RotHeart;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ExplosiveSpear;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Reflection;

public class Rotberry extends Plant {

	{
		image = 0;
		seedClass = Seed.class;
	}

	@Override
	public void activate( Char ch ) {
		if (ch instanceof Hero && ((Hero) ch).subClassSet.contains(HeroSubClass.WARDEN)){
			Buff.affect(ch, AdrenalineSurge.class).reset(1, 200f);
		}
		
		Dungeon.level.drop( new Seed(), pos ).sprite.drop();
	}
	
	@Override
	public void wither() {
		Dungeon.level.uproot( pos );
		
		if (Dungeon.level.heroFOV[pos]) {
			CellEmitter.get( pos ).burst( LeafParticle.GENERAL, 6 );
		}
		
		//no warden benefit
	}

	public static class Seed extends Plant.Seed {
		{
			image = ItemSpriteSheet.SEED_ROTBERRY;

			plantClass = Rotberry.class;

			unique = true;
		}
		
		@Override
		public int value() {
			return 30 * quantity;
		}

		@Override
		public Plant couch( int pos, Level level ) {
			Plant plant = super.couch(pos, level);
			PlantBuff.append(plant, Growth.class).set( 50, plant );
			return plant;
		}
	}

	public static class Growth extends PlantBuff {
		private int left;
		Plant plant;
		public void set(int turns, Plant plant) {
			this.left = turns;
			this.plant = plant;
			spend(TICK);
		}

		@Override
		public boolean act() {
			left--;
			if (left <= 0) {
				int pos = plant.pos;
				plant.removePlantBuff(this);
				Dungeon.level.uproot(pos);
				placePlant(Dungeon.level, pos, new RotHeart());
				return true;
			}
			spend(TICK);
			return true;
		}

		private static final String LEFT = "left";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(LEFT, left);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			left = bundle.getInt(LEFT);
		}
	}

	private static void placePlant(Level level, int pos, Mob plant){
		plant.pos = pos;
		GameScene.add(plant);
		Actor.add(plant);
		Buff.affect(plant, RotHeart.RotHeartSpawn.class).set(20);

		for(int i : PathFinder.NEIGHBOURS8) {
			int terr = level.map[pos + i];
			if (Terrain.isPlantable(terr)){
				Painter.set(level, pos + i, Terrain.HIGH_GRASS);
				GameScene.updateMap( pos+i );
			}
		}
	}
}
