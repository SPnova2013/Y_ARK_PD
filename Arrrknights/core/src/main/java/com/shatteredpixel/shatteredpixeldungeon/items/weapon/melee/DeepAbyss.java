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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArcaneArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.SandalsOfNature;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;

public class DeepAbyss extends MeleeWeapon {

	{
		image = ItemSpriteSheet.GREATAXE;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1f;

		tier = 5;
	}

	@Override
	public int max(int lvl) {
		return  5*(tier+4) +    //45 base, up from 30
				lvl*(tier+1);   //scaling unchanged
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (Dungeon.hero.belongings.getItem(SandalsOfNature.class) != null) {
			if (attacker instanceof Hero && Dungeon.hero.belongings.getItem(SandalsOfNature.class).isEquipped(Dungeon.hero)) {
				Buff.affect(attacker, ArcaneArmor.class).set(buffedLvl() + 3, 1);
			}
		}
		return super.proc(attacker, defender, damage);
	}

	@Override
	public String desc() {
		String info = Messages.get(this, "desc");
		if (Dungeon.hero.belongings.getItem(SandalsOfNature.class) != null) {
		if (Dungeon.hero.belongings.getItem(SandalsOfNature.class).isEquipped(Dungeon.hero))
			info += "\n\n" + Messages.get( DeepAbyss.class, "setbouns");}

		return info;
	}

	@Override
	public int STRReq(int lvl) {
		return STRReq(tier+1, lvl); //20 base strength req, up from 18
	}

	//潮涌悲歌: a slow, heavy tide that swells everything the hero is
	public static final int ABILITY_CHARGE = 90;
	public static final int ABILITY_DURATION = 50;
	public static final float ABILITY_BONUS = 1.3f;

	@Override
	public int abilityChargeCap() {
		return ABILITY_CHARGE;
	}

	@Override
	protected int baseChargeUse(Hero hero, Char target) {
		//one whole pool per use, and the pool is the cooldown
		return ABILITY_CHARGE;
	}

	@Override
	public boolean abilityEffectActive(Char owner) {
		//the tide holds its charge back for as long as it lasts
		return owner.buff(TidalSong.class) != null;
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		hero.belongings.abilityWeapon = this;
		beforeAbilityUsed(hero, null);

		Buff.affect(hero, TidalSong.class, ABILITY_DURATION);

		hero.sprite.operate(hero.pos);
		Sample.INSTANCE.play(Assets.Sounds.SKILL_BASIC);
		Invisibility.dispel();

		afterAbilityUsed(hero);
		hero.spendAndNext(Actor.TICK);
	}

	@Override
	public String abilityInfo() {
		return Messages.get(this, "ability_desc", ABILITY_DURATION,
				(int) (ABILITY_BONUS * 100), ABILITY_CHARGE);
	}

	//the tide itself: attack, guard and life all swell together
	public static class TidalSong extends FlavourBuff {

		{
			type = buffType.POSITIVE;
			announced = true;
		}

		@Override
		public int icon() {
			return BuffIndicator.COMBO;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0.4f, 0.6f, 1f);
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (ABILITY_DURATION - visualcooldown()) / (float) ABILITY_DURATION);
		}

		@Override
		public boolean attachTo(Char target) {
			if (!super.attachTo(target)) {
				return false;
			}
			//swell the body, keeping the same proportion of life
			target.HT = Math.max(1, Math.round(target.HT * (1f + ABILITY_BONUS)));
			target.HP = Math.min(target.HT, Math.max(1, Math.round(target.HP * (1f + ABILITY_BONUS))));
			return true;
		}

		@Override
		public void detach() {
			//and fold it back down
			if (target != null) {
				float scale = 1f / (1f + ABILITY_BONUS);
				target.HP = Math.max(1, Math.round(target.HP * scale));
				target.HT = Math.max(1, Math.round(target.HT * scale));
				target.HP = Math.min(target.HP, target.HT);
			}
			super.detach();
		}
	}
}
