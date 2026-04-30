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
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.ScholarNotebook;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BabyBugSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class Snake extends Mob {
	
	{
		spriteClass = BabyBugSprite.class;
		
		HP = HT = 1;
		hthpIncRate = 10;
		defenseSkill = 35;
		
		EXP = 2;
		damageMax = 2;
		damageMin = 1;
		attackSkill = 10;
		maxLvl = 7;
		
		loot = Generator.Category.SEED;
		lootChance = 0.25f;

		immunities.add(Silence.class);
		immunities.add(ChampionEnemy.Giant.class);
		immunities.add(ChampionEnemy.R2Overloading.class);
		properties.add(Property.INFECTED);
	}

	private static int dodges = 0;

	@Override
	public String defenseVerb() {
		dodges++;
		if (dodges >= 3 && !Badges.isUnlocked(Badges.Badge.BOSS_SLAIN_1)){
			GLog.h(Messages.get(this, "hint"));
			dodges = 0;
		}
		return super.defenseVerb();
	}
	@Override
	public int defenseSkill(Char src){//change from budding
		if (Dungeon.isChallenged(Challenges.TACTICAL_UPGRADE)){
			if ((enemySeen && state != SLEEPING && paralysed == 0)
					&& (src != null && enemy == src)&& enemy.invisible == 0
					&& !(Dungeon.level.feeling == Level.Feeling.TYPHOON)) {
				return INFINITE_EVASION;
			}
		}
		return super.defenseSkill( src );
	}
	@Override
	public void damage( int dmg, Object src ) {
		if (Dungeon.isChallenged(Challenges.TACTICAL_UPGRADE)) {
			if ((enemySeen && state != SLEEPING && paralysed == 0)
					&& ((src instanceof Wand && enemy == Dungeon.hero)
					|| ((src instanceof Char && enemy == src)&& enemy.invisible == 0)
					|| (src instanceof Weapon)
			)) {
				GLog.n(Messages.get(this, "noticed"));
			} else {
				super.damage(dmg, src);
			}
		}
		else super.damage(dmg, src);
	}

	@Override
	public boolean hasNotebookSkill(){ return true;}
}
