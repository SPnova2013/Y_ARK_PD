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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

import java.util.ArrayList;

public class Spear extends MeleeWeapon {
	public static final String AC_ZAP = "ZAP";
	{
		image = ItemSpriteSheet.SPEAR;
		hitSound = Assets.Sounds.HIT_SPEAR;
		hitSoundPitch = 0.9f;

		defaultAction = AC_ZAP;

		tier = 2;
		DLY = 1f; //0.67x speed
		RCH = 1;    //extra reach
	}

	private boolean swiching = false;

	@Override
	public int max(int lvl) {
		if (swiching == true)
		return  Math.round(6.67f*(tier+1)) +    //20 base, up from 15
				lvl*Math.round(1.33f*(tier+1)); //+4 per level, up from +
		else return  4*(tier+1) +                	//12 + 3
				lvl*(tier+1);
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		//+(9+2*lvl) damage, roughly +83% base damage, +80% scaling
		int dmgBoost = augment.damageFactor(9 + Math.round(2f*buffedLvl()));
		Spear.spikeAbility(hero, target, 1, dmgBoost, this);
	}

	@Override
	public String abilityInfo() {
		int dmgBoost = levelKnown ? 9 + Math.round(2f*buffedLvl()) : 9;
		if (levelKnown){
			return Messages.get(this, "ability_desc", augment.damageFactor(min()+dmgBoost), augment.damageFactor(max()+dmgBoost));
		} else {
			return Messages.get(this, "typical_ability_desc", min(0)+dmgBoost, max(0)+dmgBoost);
		}
	}

	public static void spikeAbility(Hero hero, Integer target, float dmgMulti, int dmgBoost, MeleeWeapon wep){
		if (target == null) {
			return;
		}

		Char enemy = Actor.findChar(target);
		if (enemy == null || enemy == hero || hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target]) {
			GLog.w(Messages.get(wep, "ability_no_target"));
			return;
		}

		hero.belongings.abilityWeapon = wep;
		if (!hero.canAttack(enemy) || Dungeon.level.adjacent(hero.pos, enemy.pos)){
			GLog.w(Messages.get(wep, "ability_target_range"));
			hero.belongings.abilityWeapon = null;
			return;
		}
		hero.belongings.abilityWeapon = null;

		hero.sprite.attack(enemy.pos, new Callback() {
			@Override
			public void call() {
				wep.beforeAbilityUsed(hero, enemy);
				AttackIndicator.target(enemy);
				int oldPos = enemy.pos;
				//only push if the enemy is still there after the hit
				if (hero.attack(enemy, dmgMulti, dmgBoost, Char.INFINITE_ACCURACY)) {
					if (enemy.isAlive() && enemy.pos == oldPos){
						//trace a ballistica to our target (which will also extend past them
						Ballistica trajectory = new Ballistica(hero.pos, enemy.pos, Ballistica.STOP_TARGET);
						//trim it to just be the part that goes past them
						trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
						//knock them back along that ballistica
						WandOfBlastWave.throwChar(enemy, trajectory, 1, true, false);
					} else if (!enemy.isAlive()) {
						wep.onAbilityKill(hero, enemy);
					}
					Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
				}
				Invisibility.dispel();
				hero.spendAndNext(hero.attackDelay());
				wep.afterAbilityUsed(hero);
			}
		});
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
			if (swiching == true) {
				swiching = false;DLY = 1f;RCH = 1;
			} else {
				swiching = true;DLY = 1.5f;RCH = 3;
			}

			updateQuickslot();
			curUser.spendAndNext(0.5f);
		}
	}

	@Override
	public String desc() {
		if (swiching) return Messages.get(this, "desc_mode");
		else return Messages.get(this, "desc");
	}

	@Override
	public String status() {
        if (Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.IRENE) {
            return super.status();
        }
		if (this.isIdentified()) {
			if (swiching) return "EX";
			else return "NM";
		}
		else return null;}

	private static final String SWICH = "swiching";
	private static final String RCHSAVE = "RCH";
	private static final String DLYSAVE = "DLY";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(SWICH, swiching);
		bundle.put(RCHSAVE, RCH);
		bundle.put(DLYSAVE, DLY);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		swiching = bundle.getBoolean(SWICH);
		RCH = bundle.getInt(RCHSAVE);
		DLY = bundle.getFloat(DLYSAVE);
	}
}
