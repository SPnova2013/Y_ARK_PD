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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class Greatsword extends MeleeWeapon {

	//沸腾爆裂: how long the rage lasts, and how hard it hits when it bursts
	public static final int ABILITY_DURATION = 10;
	public static final float ABILITY_STAT_BONUS = 0.8f;
	public static final float ABILITY_BURST_MULT = 4f;
	public static final int ABILITY_RADIUS = 2;
	public static final float ABILITY_LIFE_COST = 0.25f;

	{
		image = ItemSpriteSheet.GREATSWORD;
		hitSound = Assets.Sounds.HIT_CHAINSAW2;
		hitSoundPitch = 1f;


		tier=5;
		DLY = 0.5f;
		ACC = 0.85f;
		RCH = 2;

	}
	@Override
	public int max(int lvl) {
		return  3*(tier+1) +    // 18 + 3
				lvl*(tier-1);   //scaling unchanged
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		hero.belongings.abilityWeapon = this;
		beforeAbilityUsed(hero, null);

		//refreshing it does not stack, the burst simply happens later
		Buff.prolong(hero, BoilingBurst.class, ABILITY_DURATION);

		hero.sprite.operate(hero.pos);
		Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
		Invisibility.dispel();

		afterAbilityUsed(hero);
		hero.spendAndNext(Actor.TICK);
	}

	@Override
	public String abilityInfo() {
		return Messages.get(this, "ability_desc",
				ABILITY_DURATION,
				(int) (ABILITY_STAT_BONUS * 100),
				(int) (ABILITY_BURST_MULT * 100),
				(int) (ABILITY_LIFE_COST * 100));
	}

	//while boiling the blade hits harder
	@Override
	public int damageRoll(Char owner) {
		int dmg = super.damageRoll(owner);
		if (owner.buff(BoilingBurst.class) != null) {
			dmg = Math.round(dmg * (1f + ABILITY_STAT_BONUS));
		}
		return dmg;
	}

	//沸腾爆裂: the rage hardens the body, then blows up around the hero
	public static class BoilingBurst extends FlavourBuff {

		{
			type = buffType.POSITIVE;
			announced = true;
		}

		@Override
		public int icon() {
			return BuffIndicator.WEAPON;
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (ABILITY_DURATION - visualcooldown()) / (float) ABILITY_DURATION);
		}

		@Override
		public void detach() {
			super.detach();

			Hero hero = target instanceof Hero ? (Hero) target : null;
			if (hero == null || !hero.isAlive()) {
				return;
			}

			//the whole rage goes off at once
			int dmg = Math.round(hero.damageRoll() * ABILITY_BURST_MULT);
			PathFinder.buildDistanceMap(hero.pos, BArray.not(Dungeon.level.solid, null), ABILITY_RADIUS);
			for (int cell = 0; cell < PathFinder.distance.length; cell++) {
				if (PathFinder.distance[cell] > ABILITY_RADIUS) {
					continue;
				}
				Char ch = Actor.findChar(cell);
				if (ch == null || ch == hero || !ch.isAlive() || ch.alignment != Char.Alignment.ENEMY) {
					continue;
				}
				CellEmitter.get( cell ).burst( ElmoParticle.FACTORY, 12 );
				ch.damage(dmg, hero);
			}

			CellEmitter.get( hero.pos ).burst( ElmoParticle.FACTORY, 18 );
			Sample.INSTANCE.play(Assets.Sounds.BLAST);

			//and the hero pays a quarter of what is left
			int lost = hero.HP - Math.max(1, Math.round(hero.HP * (1f - ABILITY_LIFE_COST)));
			hero.HP -= lost;
			if (lost > 0) {
				hero.sprite.showStatus(CharSprite.NEGATIVE, "-%d", lost);
			}
		}
	}
}
