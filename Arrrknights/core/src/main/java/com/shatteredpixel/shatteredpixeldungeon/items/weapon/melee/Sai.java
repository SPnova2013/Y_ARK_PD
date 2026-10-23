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
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class Sai extends MeleeWeapon {

	{
		image = ItemSpriteSheet.SAI;
		hitSound = Assets.Sounds.HIT_PUNCH;
		hitSoundPitch = 1.3f;

		tier = 3;
		DLY = 0.4f; //2x speed
	}

	//the ability lasts ABILITY_DURATION_BASE + weaponLevel/2 turns
	public static final int ABILITY_DURATION_BASE = 3;

	@Override
	public int max(int lvl) {
		return  2*(tier+1) +   //8+3
				lvl*(tier);
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		hero.belongings.abilityWeapon = this;
		beforeAbilityUsed(hero, null);

		Buff.affect(hero, SoulRend.class, ABILITY_DURATION_BASE + buffedLvl()/2);

		hero.sprite.operate(hero.pos);
		Sample.INSTANCE.play(Assets.Sounds.SKILL_BASIC);
		Invisibility.dispel();

		afterAbilityUsed(hero);
		hero.spendAndNext(Actor.TICK);
	}

	@Override
	public String abilityInfo() {
		return Messages.get(this, "ability_desc",
				ABILITY_DURATION_BASE + buffedLvl()/2,
				Math.max(1, buffedLvl()));
	}

	//while SoulRend is up the weapon's damage is treated as spell damage, see Char#damageTypeOf
	@Override
	public boolean dealsSpellDamage(Char owner) {
		return owner.buff(SoulRend.class) != null;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (attacker.buff(SoulRend.class) != null) {
			//drink a little life with every hit
			int heal = Random.IntRange(1, Math.max(1, buffedLvl()/2));
			heal = Math.min(heal, attacker.HT - attacker.HP);
			if (heal > 0 && attacker.isAlive()) {
				attacker.HP += heal;
				attacker.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
				attacker.sprite.showStatus(CharSprite.POSITIVE, "+%dHP", heal);
			}
		}
		return super.proc(attacker, defender, damage);
	}

	//rends the soul: attacks count as spell damage and drain some health back to the wielder
	public static class SoulRend extends FlavourBuff {

		{
			type = buffType.POSITIVE;
			announced = true;
		}

		@Override
		public int icon() {
			return BuffIndicator.HEALING;
		}
	}
}
