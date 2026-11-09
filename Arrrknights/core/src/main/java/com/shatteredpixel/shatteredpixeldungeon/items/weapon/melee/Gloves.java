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
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

public class Gloves extends MeleeWeapon {

	//连击: every already landed hit adds this much damage, up to the cap
	public static final int ABILITY_DAMAGE_PER_COMBO = 2;
	public static final int ABILITY_MAX_COMBO = 10;
	//the chain lapses after this many turns without a landed hit, longer after a kill
	public static final float COMBO_WINDOW = 5f;
	public static final float COMBO_KILL_WINDOW = 15f;

	{
		image = ItemSpriteSheet.GLOVES;
		hitSound = Assets.Sounds.HIT_PUNCH;
		hitSoundPitch = 1.3f;

		tier = 1;
		DLY = 0.4f; // 2.5회. +150%
		
		bones = false;
	}

	//how many hits in a row this pair of gloves has landed
	private int combo = 0;
	//false until the current swing lands, so a miss can break the chain
	private boolean lastSwingLanded = true;

	@Override
	public int max(int lvl) {
		return  Math.round(2f*(tier+1)) +     //5 base, down from 10
				lvl*Math.round(1.5f*(tier+1));  //+1 per level, down from +2
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		if (target == null) {
			return;
		}

		final Char enemy = Actor.findChar(target);
		if (enemy == null || enemy == hero || !Dungeon.level.heroFOV[target]
				|| hero.isCharmedBy(enemy) || enemy.alignment == Char.Alignment.ALLY) {
			GLog.w(Messages.get(this, "ability_no_target"));
			return;
		}
		if (!hero.canAttack(enemy)) {
			GLog.w(Messages.get(this, "ability_target_range"));
			return;
		}

		final int bonus = combo * ABILITY_DAMAGE_PER_COMBO;

		hero.belongings.abilityWeapon = this;
		beforeAbilityUsed(hero, enemy);
		hero.busy();

		hero.sprite.attack(enemy.pos, new Callback() {
			@Override
			public void call() {
				AttackIndicator.target(enemy);

				//a guaranteed hit
				if (hero.attack(enemy, 1f, 0f, Char.INFINITE_ACCURACY)) {
					Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
				}
				Invisibility.dispel();

				//the gathered momentum lands a moment later
				if (bonus > 0 && enemy.isAlive()) {
					CellEmitter.get(enemy.pos).burst(Speck.factory(Speck.LIGHT), 6);
					enemy.damage(bonus, hero);
				}

				combo = 0;
				lastSwingLanded = true;
				Buff.detach(hero, ComboWindow.class);

				hero.spendAndNext(hero.attackDelay());
				afterAbilityUsed(hero);
			}
		});
	}

	@Override
	public String abilityInfo() {
		return Messages.get(this, "ability_desc", ABILITY_DAMAGE_PER_COMBO, ABILITY_MAX_COMBO);
	}

	//a swing that misses breaks the chain, a landed one extends it
	@Override
	public void onSwing(Char owner, Char enemy) {
		if (!lastSwingLanded) {
			combo = 0;
		}
		lastSwingLanded = false;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		lastSwingLanded = true;
		if (combo < ABILITY_MAX_COMBO) {
			combo++;
		}

		//the chain lapses when nothing is hit for a while, a kill keeps it alive longer
		if (attacker instanceof Hero) {
			boolean lethal = defender.HP <= damage;
			Buff.prolong((Hero) attacker, ComboWindow.class,
					lethal ? COMBO_KILL_WINDOW : COMBO_WINDOW);
		}

		int df = damage;
		damage = Math.min(damage + defender.drRoll(), damage + defender.drRoll());

		if (damage >= df+2) damage = df+2;

		return super.proc(attacker, defender, damage);
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	//no hits for a few turns and the chain is gone
	public static class ComboWindow extends FlavourBuff {

		{
			type = buffType.POSITIVE;
			announced = false;
		}

		@Override
		public void detach() {
			super.detach();
			if (target instanceof Hero) {
				KindOfWeapon wep = ((Hero) target).belongings.weapon;
				if (wep instanceof Gloves) {
					((Gloves) wep).combo = 0;
				}
			}
		}
	}

	private static final String COMBO = "combo";
	private static final String LANDED = "last_swing_landed";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(COMBO, combo);
		bundle.put(LANDED, lastSwingLanded);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		combo = bundle.getInt(COMBO);
		lastSwingLanded = bundle.getBoolean(LANDED);
	}
}
