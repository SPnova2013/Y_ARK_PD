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
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Camouflage;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Preparation;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ArmoredBrute;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Brute;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.MudrockZealot;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.NewDM300;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Pompeii;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Raider;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Succubus;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogDzewa;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.miniboss.BloodMagister;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.miniboss.Faust;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.miniboss.Mon3tr;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ChaliceOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class BattleAxe extends MeleeWeapon {
	public static final String AC_ZAP = "ZAP";
	{
		image = ItemSpriteSheet.BATTLE_AXE;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 0.9f;

		defaultAction = AC_ZAP;

		tier = 4;
		ACC = 1.15f; //24% boost to accuracy
	}

	@Override
	public int max(int lvl) {
		return  3*(tier+1) +    //15+3
				lvl*(tier-1);
	}

	private int starpower = 0 ;
	private int starpowercap = 3;

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_ZAP);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {

		super.execute(hero, action);

		if (action.equals(AC_ZAP) && Dungeon.hero.belongings.weapon == this) {
			if (starpower < starpowercap) {
				starpower++;
				updateQuickslot();
				hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(BattleAxe.class, "charge"));
				if (Dungeon.hero.belongings.getItem(TalismanOfForesight.class) != null) {
					if (Dungeon.hero.belongings.getItem(TalismanOfForesight.class).isEquipped(Dungeon.hero)) {
						curUser.spendAndNext(0.75f);
					}
					else curUser.spendAndNext(2f);
				}
				else curUser.spendAndNext(2f);
			} else
				hero.sprite.showStatus(CharSprite.NEGATIVE, Messages.get(BattleAxe.class, "charge_fail"));
		}
	}


	@Override
	public int proc(Char attacker, Char defender, int damage) {

		if (starpower >= 1 && attacker instanceof Hero) {
			for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
				if (mob.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[mob.pos]) {
					int dmg = attacker.damageRoll() - defender.drRoll();
					dmg = Math.round(dmg * (starpower * 0.7f));

					mob.damage(dmg, attacker);
				}
			}
			if (starpower == 3) {
				GameScene.flash(0x80FFFFFF);
				dispel(defender);
			}
			Camera.main.shake(2, starpower / 3);

			Sample.INSTANCE.play(Assets.Sounds.HIT_SLASH, 1.76f, 1.76f);
			attacker.sprite.showStatus(CharSprite.POSITIVE, Messages.get(BattleAxe.class, "attack"));
		}

		starpower = 0;
		updateQuickslot();
		return super.proc(attacker, defender, damage);
	}

	@Override
	public String desc() {
		String info = Messages.get(this, "desc");
		if (Dungeon.hero.belongings.getItem(TalismanOfForesight.class) != null) {
		if (Dungeon.hero.belongings.getItem(TalismanOfForesight.class).isEquipped(Dungeon.hero))
			info += "\n\n" + Messages.get( BattleAxe.class, "setbouns");}

		return info;
	}

	@Override
	public String status() {
		if (this.isIdentified()) return starpower + "/" + starpowercap;
		else return null;}

	private static final String POWER = "starpower";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(POWER, starpower);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (starpowercap > 0) starpower = Math.min(starpowercap, bundle.getInt(POWER));
		else starpower = bundle.getInt(POWER);
	}

	private void dispel(Char defender) {
		for (Buff b : defender.buffs()) {
			if (b.type == Buff.buffType.POSITIVE) {
				b.detach();
			}
		}
		defender.addImmune(Camouflage.class);
		if (defender instanceof MudrockZealot) ((MudrockZealot) defender).breakBarrier();
		if (defender instanceof BloodMagister) ((BloodMagister) defender).clearAttackPower();
		if (defender instanceof NewDM300) {
			if(((NewDM300) defender).DamageUP>0)defender.sprite.showStatus( CharSprite.NEGATIVE, Messages.get(NewDM300.class, "dispel"));
			((NewDM300) defender).DamageUP = 0;
		}
		if(defender instanceof Faust) ((Faust) defender).clearCharge();
		if(defender instanceof Succubus) ((Succubus) defender).clearASPlus();
		if(defender instanceof Raider) ((Raider) defender).clearASPlus();
		if(defender instanceof YogDzewa) {
			if(Statistics.spawnersAlive != 0)defender.sprite.showStatus( CharSprite.NEGATIVE, Messages.get(YogDzewa.class, "dispel"));
			Statistics.spawnersAlive = 0;
		}
		if(defender instanceof Mon3tr) ((Mon3tr) defender).clearSkill();
		if(defender instanceof Pompeii) {
			if(Statistics.coreAlive>0)defender.sprite.showStatus( CharSprite.NEGATIVE, Messages.get(Pompeii.class, "dispel"));
			Statistics.coreAlive = 0;
		}
		if(defender instanceof Brute){
			if(defender.HP <= 0) defender.die(null);
		}
	}
}
