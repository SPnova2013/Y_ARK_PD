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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.audio.Sample;

public class Greatshield extends MeleeWeapon {

	//护卫 lasts ABILITY_DURATION_BASE + weaponLevel turns
	public static final int ABILITY_DURATION_BASE = 5;

	{
		image = ItemSpriteSheet.GREATSHIELD;
		hitSound = Assets.Sounds.HIT_STRIKE;
		tier = 5;
	}

	@Override
	public int max(int lvl) {
		return  Math.round(2.5f*(tier+1)) +     //15 base, down from 30
				lvl*(tier-2);                   //+3 per level, down from +6
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		hero.belongings.abilityWeapon = this;
		beforeAbilityUsed(hero, null);

		Buff.prolong(hero, Guard.class, ABILITY_DURATION_BASE + buffedLvl());

		hero.sprite.operate(hero.pos);
		Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
		Invisibility.dispel();

		afterAbilityUsed(hero);
		hero.spendAndNext(Actor.TICK);
	}

	@Override
	public String abilityInfo() {
		return Messages.get(this, "ability_desc", ABILITY_DURATION_BASE + buffedLvl());
	}

	//the guard is not broken by swinging, it simply drops for that round
	@Override
	public void onSwing(Char owner, Char enemy) {
		if (owner.buff(Guard.class) != null && owner.buff(GuardLapse.class) == null) {
			Buff.affect(owner, GuardLapse.class, 0f);
		}
	}

	//护卫: nothing that comes from another character can get through
	public static class Guard extends FlavourBuff {

		{
			type = buffType.POSITIVE;
			announced = true;
		}

		@Override
		public int icon() {
			return BuffIndicator.ARMOR;
		}
	}

	//the round in which the hero swings: the guard is treated as inactive
	public static class GuardLapse extends FlavourBuff {

		{
			type = buffType.NEGATIVE;
			announced = false;
		}
	}

	@Override
	public int defenseFactor( Char owner ) {
		return DRMax();
	}

	public int DRMax(){
		return DRMax(buffedLvl());
	}

	//6 extra defence, plus 2 per level
	public int DRMax(int lvl){
		return 6 + 3*lvl;
	}
	
	public String statsInfo(){
		if (isIdentified()){
			return Messages.get(this, "stats_desc", 6+3*buffedLvl());
		} else {
			return Messages.get(this, "typical_stats_desc", 6);
		}
	}
}
