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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Twilight;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfElements;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMight;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

import java.util.ArrayList;

public class Laevateinn extends MeleeWeapon {
	public static final String AC_ZAP = "ZAP";

	{
		image = ItemSpriteSheet.WAR_HAMMER;
		hitSound = Assets.Sounds.HIT_CRUSH;
		hitSoundPitch = 1f;

		tier = 5;
		ACC = 1.3f; //20% boost to accuracy
	}

	private boolean skill = true; // fasle == skill non-active

	//flame blade: the strike itself deals this many times its damage
	public static final float ABILITY_DAMAGE_MULT = 3f;

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
		if (enemy == null || enemy == hero || hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target]) {
			GLog.w(Messages.get(this, "ability_no_target"));
			return;
		}
		if (!hero.canAttack(enemy)) {
			GLog.w(Messages.get(this, "ability_target_range"));
			return;
		}

		//same shape as the duelist's cleave: swing ourselves, then read the outcome
		hero.sprite.attack(enemy.pos, new Callback() {
			@Override
			public void call() {
				beforeAbilityUsed(hero, enemy);
				AttackIndicator.target(enemy);

				if (hero.attack(enemy, ABILITY_DAMAGE_MULT, 0, Char.INFINITE_ACCURACY)) {
					Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
				}

				Invisibility.dispel();

				if (!enemy.isAlive()) {
					//the killing blow pays the charge back
					Buff.affect(hero, Charger.class).gainCharge(abilityChargeUse(hero, null));
					hero.next();
				} else {
					hero.spendAndNext(hero.attackDelay());
				}
				afterAbilityUsed(hero);
			}
		});
	}

	@Override
	public String abilityInfo() {
		return Messages.get(this, "ability_desc", (int) ABILITY_DAMAGE_MULT);
	}

	@Override
	public int max(int lvl) {
		return  5*(tier) + 2 +    //27 + 6
				lvl*(tier+1);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (Dungeon.hero.buff(Twilight.class) != null) {
			damage *= 2;
		}

		if (Dungeon.hero.belongings.getItem(RingOfElements.class) != null && Dungeon.hero.belongings.getItem(RingOfMight.class) != null) {
			if (Dungeon.hero.belongings.getItem(RingOfElements.class).isEquipped(Dungeon.hero) && Dungeon.hero.belongings.getItem(RingOfMight.class).isEquipped(Dungeon.hero)) {
				if (defender.buff(Burning.class) != null) {
					damage *= 1.4f;
				}
			}
		}
		return super.proc(attacker, defender, damage);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_ZAP);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {

		super.execute(hero, action);

		if (action.equals(AC_ZAP)) {
			if (Dungeon.hero.belongings.weapon == this) {
				if (skill == true) {
					Dungeon.hero.HP = 1;
					GameScene.flash(0x00FF0000);
					Buff.affect(Dungeon.hero, Twilight.class, 30f);
					Sample.INSTANCE.play(Assets.Sounds.SKILL_SURTR);
					skill = false;
					Dungeon.hero.spendAndNext(1f);

				} else {
					GLog.w(Messages.get(this, "skillfail"));
				}
			}
			else GLog.w(Messages.get(this, "dont_equ"));
		}
	}

	@Override
	public String desc() {
		String info = Messages.get(this, "desc");
		if (Dungeon.hero.belongings.getItem(RingOfElements.class) != null && Dungeon.hero.belongings.getItem(RingOfMight.class) != null) {
			if (Dungeon.hero.belongings.getItem(RingOfElements.class).isEquipped(Dungeon.hero) && Dungeon.hero.belongings.getItem(RingOfMight.class).isEquipped(Dungeon.hero))
				info += "\n\n" + Messages.get( Laevateinn.class, "setbouns");}

		return info;
	}

	private static final String SKILL = "skill";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(SKILL, skill);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		skill = bundle.getBoolean(SKILL);
	}

}
