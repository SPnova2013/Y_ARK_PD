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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BatSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BreakerSprite;
import com.watabou.utils.Random;

public class Bat extends Mob {

	{
		spriteClass = BreakerSprite.class;
		
		HP = HT = 32;
		damageMax = 17;
		damageMin = 5;
		drMax = 4;
		drMin = 0;
		attackSkill = 16;
		defenseSkill = 15;
		baseSpeed = 2f;
		
		EXP = 7;
		maxLvl = 15;
		
		loot = new PotionOfHealing();
		lootChance = 0.2f; //by default, see rollToDropLoot()
	}
	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );
		int reg = Math.min( damage - 4, HT - HP );

		if(this.buff(Silence.class) == null) {
			if (reg > 0) {
				HP += reg;
				sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
			}
		}
		
		return damage;
	}
	
	@Override
	public void rollToDropLoot() {
		lootChance *= ((7f - Dungeon.LimitedDrops.BAT_HP.count) / 7f);
		super.rollToDropLoot();
	}
	
	@Override
	protected Item createLoot(){
		Dungeon.LimitedDrops.BAT_HP.count++;
		return super.createLoot();
	}
	@Override
	public boolean hasNotebookSkill(){ return true;}
}
