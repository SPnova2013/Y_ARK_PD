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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class HandAxe extends MeleeWeapon {

	public static final float ABILITY_DAMAGE_MULT = 3f;

	{
		image = ItemSpriteSheet.HAND_AXE;
		hitSound = Assets.Sounds.HIT_BONK;
		hitSoundPitch = 1f;

		tier = 2;
		ACC = 1.2f; //32% boost to accuracy
	}

	@Override
	public int max(int lvl) {
		return  4*(tier+1) +    //12 base, down from 15
				lvl*(tier+1);   //scaling unchanged
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		if (hero.buff(MicroBlast.class) != null) {
			GLog.w(Messages.get(this, "ability_armed"));
			return;
		}

		hero.belongings.abilityWeapon = this;
		beforeAbilityUsed(hero, null);

		//stays armed until an attack actually connects
		Buff.affect(hero, MicroBlast.class);

		hero.sprite.operate(hero.pos);
		Sample.INSTANCE.play(Assets.Sounds.SKILL_BASIC);
		Invisibility.dispel();

		afterAbilityUsed(hero);
	}

	@Override
	public String abilityInfo() {
		return Messages.get(this, "ability_desc", (int) ABILITY_DAMAGE_MULT);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {

		if (attacker.buff(MicroBlast.class) != null && defender.isAlive()) {
			Buff.detach(attacker, MicroBlast.class);

			damage = Math.round(damage * ABILITY_DAMAGE_MULT);

			CellEmitter.get( defender.pos ).burst( ElmoParticle.FACTORY, 12 );
			Sample.INSTANCE.play(Assets.Sounds.BLAST);
			for (int i : PathFinder.NEIGHBOURS8) {
				int cell = defender.pos + i;
				if (cell < 0 || cell >= Dungeon.level.length()) continue;
				//guard against stepping around the edge of a row
				if (Dungeon.level.distance(defender.pos, cell) != 1) continue;

				Char ch = Actor.findChar(cell);
				if (ch == null || ch == attacker || ch == defender || !ch.isAlive()) continue;
				if (ch.alignment != Char.Alignment.ENEMY) continue;

				CellEmitter.get( cell ).burst( ElmoParticle.FACTORY, 12 );
				ch.damage(Math.max(1, damage - ch.drRoll()), attacker);
			}
		}

		return super.proc(attacker, defender, damage);
	}

	//微差爆破: the coming blow is tripled and sets off the ground around its target
	public static class MicroBlast extends Buff {

		{
			type = buffType.POSITIVE;
			announced = true;
		}

		@Override
		public int icon() {
			return BuffIndicator.WEAPON;
		}

		@Override
		public String toString() {
			return Messages.get(this, "name");
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc");
		}
	}
}
