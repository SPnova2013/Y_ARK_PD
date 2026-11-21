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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEvasion;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class Decapitator extends MeleeWeapon {

	{
		image = ItemSpriteSheet.FLAIL;
		hitSound = Assets.Sounds.HIT_KNIFE;
		hitSoundPitch = 0.8f;

		tier = 5;
		ACC = 1.3f; //0.9x accuracy
		//also cannot surprise attack, see Hero.canSurpriseAttack
	}

	//弦月: a sweeping arc that guards and cuts twice
	public static final float ABILITY_EVASION_BONUS = 2f;
	public static final int ABILITY_DURATION_BASE = 5;

	public int abilityDuration() {
		return ABILITY_DURATION_BASE + buffedLvl() / 2;
	}

	//a free action: the arc is drawn, not swung
	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		hero.belongings.abilityWeapon = this;
		beforeAbilityUsed(hero, null);

		Buff.prolong(hero, CrescentMoon.class, abilityDuration());

		hero.sprite.operate(hero.pos);
		Sample.INSTANCE.play(Assets.Sounds.SKILL_BASIC);
		Invisibility.dispel();

		afterAbilityUsed(hero);
		//no spendAndNext here on purpose, drawing the arc costs no turn
	}

	@Override
	public String abilityInfo() {
		return Messages.get(this, "ability_desc", abilityDuration(),
				(int) (ABILITY_EVASION_BONUS * 100));
	}

	//the arc itself
	public static class CrescentMoon extends FlavourBuff {

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
			return Math.max(0, (ABILITY_DURATION_BASE - visualcooldown()) / (float) ABILITY_DURATION_BASE);
		}
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		float healPer = 0.12f;
		int thisHP = attacker.HP;
		int thisHT = attacker.HT;

		if (Dungeon.hero.belongings.getItem(RingOfEvasion.class) != null) {
			if (Dungeon.hero.belongings.getItem(RingOfEvasion.class).isEquipped(Dungeon.hero)) {
				healPer = 0.18f;
			}}

		if (thisHP <= thisHT/4) {
			damage *= 1.5f;

			if (Random.Int(2) < 1) {
				int healAmt = Math.round(damage * healPer);
				healAmt = Math.min(healAmt, attacker.HT - attacker.HP);

				if (healAmt > 0 && attacker.isAlive()) {

					attacker.HP += healAmt;
					attacker.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 1);
					attacker.sprite.showStatus(CharSprite.POSITIVE, Integer.toString(healAmt));

				}
			}
		}
		else if (thisHP <= thisHT / 2){
			damage *= 1.3f;
			if (Random.Int(4) < 1) {
				int healAmt = Math.round(damage * healPer);
				healAmt = Math.min(healAmt, attacker.HT - attacker.HP);

				if (healAmt > 0 && attacker.isAlive()) {

					attacker.HP += healAmt;
					attacker.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 1);
					attacker.sprite.showStatus(CharSprite.POSITIVE, Integer.toString(healAmt));

				}
			}
		}
		else if (thisHP <= thisHT - thisHT/4) {
			damage *= 1.1f;
		}

		//the arc comes around a second time
		if (attacker.buff(CrescentMoon.class) != null && defender.isAlive()) {
			defender.sprite.flash();
			defender.damage(damage, attacker);
		}

		return super.proc(attacker, defender, damage);
	}


	@Override
	public String desc() {
		String info = Messages.get(this, "desc");
		if (Dungeon.hero.belongings.getItem(RingOfEvasion.class) != null) {
			if (Dungeon.hero.belongings.getItem(RingOfEvasion.class).isEquipped(Dungeon.hero))
				info += "\n\n" + Messages.get( Decapitator.class, "setbouns");}

		return info;
	}
}
