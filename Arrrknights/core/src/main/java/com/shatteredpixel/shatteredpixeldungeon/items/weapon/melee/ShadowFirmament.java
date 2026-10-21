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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChenCombo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Combo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.effects.coversprite.EX42Cover;
import com.shatteredpixel.shatteredpixeldungeon.effects.coversprite.FrostBlastCover;
import com.shatteredpixel.shatteredpixeldungeon.effects.coversprite.ShadowlessCover;
import com.shatteredpixel.shatteredpixeldungeon.effects.coversprite.ShadowlessFinishCover;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ChaliceOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.chimera.Shadow;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.TrueDamage;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ShadowFirmament extends MeleeWeapon {

	//the ability needs a completely full charge, and consumes all of it
	public static final int ABILITY_HITS = 10;
	public static final int ABILITY_RADIUS = 2;
	public static final float ABILITY_HIT_DAMAGE_FACTOR = 1f;
	//the last hit deals +100% damage and ignores armor/resistances, see TrueDamage
	public static final float ABILITY_FINISHER_BONUS = 1f;

	{
		image = ItemSpriteSheet.GLAIVE;
		hitSound = Assets.Sounds.HIT_DUALSTRIKE;
		hitSoundPitch = 0.8f;

		tier = 5;
	}
	private int baseComboCount = 1;
	private int currentComboCount = 1;

	public int getBaseComboCount() {
		if (hasChimera(Shadow.class)) return baseComboCount + 2;
		else return baseComboCount;
	}
	public int getCurrentComboCount() {
		return currentComboCount;
	}
	public void setCurrentComboCount(int value) {
		currentComboCount = value;
	}

	@Override
	public int max(int lvl) {
		return  3*(tier+1) - 1 +    //17 + 4. 공식상 2회 타격
				lvl*(tier-1);   //scaling unchanged
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected int baseChargeUse(Hero hero, Char target) {
		//paid for with the weapon's own charge, not with duelist charges
		return 0;
	}

	@Override
	public String abilityInfo() {
		if (levelKnown) {
			return Messages.get(this, "ability_desc",
					augment.damageFactor(Math.round(min()*ABILITY_HIT_DAMAGE_FACTOR)),
					augment.damageFactor(Math.round(max()*ABILITY_HIT_DAMAGE_FACTOR)));
		} else {
			return Messages.get(this, "typical_ability_desc",
					Math.round(min(0)*ABILITY_HIT_DAMAGE_FACTOR),
					Math.round(max(0)*ABILITY_HIT_DAMAGE_FACTOR));
		}
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {

		if (target == null) {
			return;
		}

		if (charge < chargeCap) {
			GLog.w(Messages.get(this, "no_charge"));
			return;
		}

		if (!Dungeon.level.heroFOV[target]
				|| !Dungeon.level.passable[target]
				|| Actor.findChar(target) != null) {
			GLog.w(Messages.get(this, "bad_target"));
			return;
		}

		hero.belongings.abilityWeapon = this;
		beforeAbilityUsed(hero, null);

		//the whole charge is spent, even if nothing ends up in reach
		charge = 0;
		updateQuickslot();

		//blink to the chosen cell
		hero.sprite.interruptMotion();
		hero.move(target, false);
		hero.sprite.place(target);
		Dungeon.observe();
		GameScene.updateFog();
		Sample.INSTANCE.play(Assets.Sounds.TELEPORT);

		//every enemy within 2 tiles of the landing spot shares the hits
		ArrayList<Char> victims = new ArrayList<>();
		PathFinder.buildDistanceMap(target, BArray.not(Dungeon.level.solid, null), ABILITY_RADIUS);
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] <= ABILITY_RADIUS) {
				Char ch = Actor.findChar(i);
				if (ch != null && ch != hero && ch.alignment == Char.Alignment.ENEMY && ch.isAlive()) {
					victims.add(ch);
				}
			}
		}

		//the hero is not drawn while the blows land, and cannot act until the storm is over
		hero.busy();
		hero.sprite.alpha(0f);

		if (victims.isEmpty()) {
			GLog.i(Messages.get(this, "no_targets"));
			finishAbility(hero);
		} else {
			Sample.INSTANCE.play(Assets.Sounds.SKILL_SILVERSLASH);
			GameScene.flash(0x60FFFFFF);
			nextHit(hero, victims, 1);
		}
	}

	//one blow per slash mark, each waits for the previous animation to finish
	private void nextHit(final Hero hero, final ArrayList<Char> victims, final int hit) {

		//drop anything that has already fallen
		for (int i = victims.size() - 1; i >= 0; i--) {
			if (!victims.get(i).isAlive()) {
				victims.remove(i);
			}
		}

		if (hit > ABILITY_HITS || victims.isEmpty()) {
			finishAbility(hero);
			return;
		}

		final Char enemy = Random.element(victims);
		int dmg = Math.round(damageRoll(hero) * ABILITY_HIT_DAMAGE_FACTOR);

		if (hit == ABILITY_HITS) {
			//the finisher is doubled and ignores armor and resistances
			TrueDamage.deal(enemy, Math.round(dmg * (1 + ABILITY_FINISHER_BONUS)), hero);
		} else {
			//spell damage, the same source a wand uses
			enemy.damage(dmg, new WandOfMagicMissile());
		}

		Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG, 0.6f, Random.Float(0.9f, 1.1f));

		//the next blow starts once this slash mark has played out
		ShadowlessCover cover = new ShadowlessCover();
		cover.origin.set(cover.width()/2f, cover.height()/2f);
		ShadowlessFinishCover finishCover = new ShadowlessFinishCover();
		finishCover.origin.set(cover.width()/2f, cover.height()/2f);
		cover.angle = Random.Float(360f);
		if (hit == ABILITY_HITS) {
			finishCover.onFinish = new Callback() {
				@Override
				public void call() {
					//continue on the next frame, outside of the scene's update loop
					Game.runOnRenderThread(new Callback() {
						@Override
						public void call() {
							nextHit(hero, victims, hit + 1);
						}
					});
				}
			};
			finishCover.centerAndPlay(enemy.pos);
		}else{
			cover.onFinish = new Callback() {
				@Override
				public void call() {
					//continue on the next frame, outside of the scene's update loop
					Game.runOnRenderThread(new Callback() {
						@Override
						public void call() {
							nextHit(hero, victims, hit + 1);
						}
					});
				}
			};
			cover.centerAndPlay(enemy.pos);
		}
	}

	private void finishAbility(Hero hero) {
		//the hero reappears where they landed
		/*new FrostBlastCover().centerAndPlay(hero.pos);*/
		Invisibility.dispel();
		hero.sprite.alpha(1f);

		afterAbilityUsed(hero);
		hero.spendAndNext(Actor.TICK);
	}

	@Override
	public String status() {

		//if the weapon isn't IDed, or is cursed, don't display anything
		if (!isIdentified() || cursed) {
			return null;
		}

		//display as percent
		if (chargeCap == 100) {
			return Messages.format("%d%%", charge);
		}

		return null;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		int curretComboCount = getCurrentComboCount();

		if (curretComboCount > 0) {
			setCurrentComboCount(curretComboCount - 1);
			if (!attacker.attack(defender)) {
				setCurrentComboCount(getBaseComboCount());
			} else {
				defender.sprite.bloodBurstA( defender.sprite.center(), 4 );
				defender.sprite.flash();
				if (attacker instanceof Hero && Dungeon.hero.subClassSet.contains(HeroSubClass.GLADIATOR)) {
					Buff.affect(attacker, Combo.class).bounshit(defender);
				}
				else if (attacker instanceof Hero && Dungeon.hero.heroClass == HeroClass.CHEN) {
					Buff.affect(attacker, ChenCombo.class).bounshit(defender);
				}
			}
		}else setCurrentComboCount(getBaseComboCount());

		if (attacker instanceof Hero) {
			if (Dungeon.hero.belongings.getItem(ChaliceOfBlood.class) != null) {
				if (((Hero) attacker).belongings.getItem(ChaliceOfBlood.class).isEquipped(Dungeon.hero)) {
					if (Random.Int(20) < 1)
						damage *= 1.5f;
				}
			}
		}
		return super.proc(attacker, defender, damage);
	}

	@Override
	public String desc() {
		String info = Messages.get(this, "desc");
		if (Dungeon.hero.belongings.getItem(ChaliceOfBlood.class) != null) {
		if (Dungeon.hero.belongings.getItem(ChaliceOfBlood.class).isEquipped(Dungeon.hero))
			info += "\n\n" + Messages.get( ShadowFirmament.class, "setbouns");}

		return info;
	}
}
