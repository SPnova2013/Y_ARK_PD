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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CustomeSet;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class RoundShield extends MeleeWeapon implements MeleeWeapon.BlockingWeapon {

	{
		image = ItemSpriteSheet.ROUND_SHIELD;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;

		tier = 3;
	}

	//everyone within this many tiles of the hero gets a dose
	public static final int ABILITY_RADIUS = 2;
	//heal roll is ABILITY_HEAL_MIN to ABILITY_HEAL_MAX_BASE + weaponLevel
	public static final int ABILITY_HEAL_MIN = 1;
	public static final int ABILITY_HEAL_MAX_BASE = 5;

	@Override
	public int max(int lvl) {
		return  Math.round(2.5f*(tier+1)) +     //10 base, down from 20
				lvl*(tier-1);                   //+2 per level, down from +4
	}


	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		hero.belongings.abilityWeapon = this;
		beforeAbilityUsed(hero, null);

		doseAllies(hero);

		hero.sprite.operate(hero.pos);
		Sample.INSTANCE.play(Assets.Sounds.DRINK);

		afterAbilityUsed(hero);
		//no spendAndNext here on purpose, dressing wounds costs no turn
	}

	@Override
	public String abilityInfo() {
		return Messages.get(this, "ability_desc", ABILITY_HEAL_MAX_BASE + buffedLvl());
	}

	//hands a dose to the hero and to every ally standing close by
	private void doseAllies(Hero hero) {
		PathFinder.buildDistanceMap(hero.pos, BArray.not(Dungeon.level.solid, null), ABILITY_RADIUS);
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] > ABILITY_RADIUS) continue;

			Char ch = Actor.findChar(i);
			if (ch == null || !ch.isAlive()) continue;
			//the hero, plus anything fighting on their side
			if (ch != hero && ch.alignment != Char.Alignment.ALLY) continue;

			dose(ch);
		}
	}

	private void dose(Char ch) {
		int amount = Random.IntRange(ABILITY_HEAL_MIN, ABILITY_HEAL_MAX_BASE + buffedLvl());
		amount = Math.min(amount, ch.HT - ch.HP);
		if (amount > 0) {
			ch.HP += amount;
			if (ch.sprite != null) {
				ch.sprite.emitter().burst(Speck.factory(Speck.HEALING), 3);
				ch.sprite.showStatus(CharSprite.POSITIVE, "+%dHP", amount);
			}
		}
	}

	@Override
	public int defenseFactor( Char owner ) {
		return DRMax();
	}

	public int DRMax(){
		return DRMax(buffedLvl());
	}

	@Override
	public int DRMax(int lvl) {
		return  4 + 2 * lvl;
	}
	@Override
	public String desc() {
		String info = Messages.get(this, "desc");
		if (Dungeon.hero.belongings.getItem(CustomeSet.class) != null) {
			if (Dungeon.hero.belongings.getItem(CustomeSet.class).isEquipped(Dungeon.hero))
				info += "\n\n" + Messages.get( RoundShield.class, "setbouns");}

		return info;
	}
	
	public String statsInfo(){
		if (isIdentified()){
			return Messages.get(this, "stats_desc", 4+2*buffedLvl());
		} else {
			return Messages.get(this, "typical_stats_desc", 4);
		}
	}

}