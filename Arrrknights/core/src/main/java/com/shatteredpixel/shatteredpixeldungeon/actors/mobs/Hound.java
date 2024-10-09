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

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Stamina;
import com.shatteredpixel.shatteredpixeldungeon.items.ScholarNotebook;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HaundSprite;
import com.watabou.utils.Random;

public class Hound extends Mob {

	{
		spriteClass = HaundSprite.class;
		
		HP = HT = 15;
		damageMax = 6;
		drMax = 4;
		drMin = 0;
		attackSkill = 12;
		defenseSkill = 5;
		baseSpeed = 2f;
		
		EXP = 4;
		maxLvl = 9;
		
		loot = new MysteryMeat();
		lootChance = 0.167f;

		immunities.add(Silence.class);
		properties.add(Property.INFECTED);
	}
	
	@Override
	public int damageRoll() {
		if (Dungeon.isChallenged(Challenges.TACTICAL_UPGRADE)) return Random.NormalIntRange(2+damageMinInc, 6+damageMaxInc);
		return Random.NormalIntRange( 1+damageMinInc, 6+damageMaxInc);
	}
	@Override
	public boolean hasNotebookSkill(){ return true;}
	@Override
	public void notebookSkill(ScholarNotebook notebook, int index){
		Buff.affect(Dungeon.hero, Stamina.class, 10f);
	}
}
