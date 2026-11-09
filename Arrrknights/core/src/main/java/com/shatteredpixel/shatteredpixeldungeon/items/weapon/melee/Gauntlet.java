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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Twilight;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class Gauntlet extends MeleeWeapon {
	public static final String AC_ZAP = "ZAP";

	{
		image = ItemSpriteSheet.GAUNTLETS;
		hitSound = Assets.Sounds.HIT_PUNCH;
		hitSoundPitch = 1.2f;

		defaultAction = AC_ZAP;

		tier = 5;
		DLY = 0.2f; //2x speed
	}

	private boolean swiching = false;

	//震地碎岩击: two full swings, then everything is thrown back
	public static final int ABILITY_HITS = 2;
	public static final int ABILITY_POWER = 2;

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		hero.belongings.abilityWeapon = this;
		beforeAbilityUsed(hero, null);

		int dmg = hero.damageRoll();
		int w = Dungeon.level.width();

		for (int i : PathFinder.NEIGHBOURS9) {
			int cell = hero.pos + i;
			if (Dungeon.level.heroFOV[cell]) {
				CellEmitter.get(cell).burst(SmokeParticle.FACTORY, 4);
			}
			if (cell < 0 || cell >= Dungeon.level.length()) continue;
			//guard against stepping around the edge of a row
			if (Dungeon.level.distance(hero.pos, cell) != 1) continue;

			Char ch = Actor.findChar(cell);
			if (ch == null || ch == hero || !ch.isAlive()) continue;
			if (ch.alignment != Char.Alignment.ENEMY) continue;

			for (int hit = 0; hit < ABILITY_HITS && ch.isAlive(); hit++) {
				ch.damage(dmg, hero);
			}
			if (!ch.isAlive()) continue;

			//throw it two tiles away from the hero, exactly like a blast wave
			int dirX = Integer.signum((ch.pos % w) - (hero.pos % w));
			int dirY = Integer.signum((ch.pos / w) - (hero.pos / w));
			int ex = Math.max(0, Math.min(w - 1, (ch.pos % w) + ABILITY_POWER * dirX));
			int ey = Math.max(0, Math.min(Dungeon.level.height() - 1, (ch.pos / w) + ABILITY_POWER * dirY));

			Ballistica trajectory = new Ballistica(ch.pos, ey * w + ex, Ballistica.MAGIC_BOLT);
			WandOfBlastWave.throwChar(ch, trajectory, ABILITY_POWER, false, true, true);
		}

		CellEmitter.get(hero.pos).burst(ElmoParticle.FACTORY, 16);
		Sample.INSTANCE.play(Assets.Sounds.BLAST);
		Invisibility.dispel();

		afterAbilityUsed(hero);
		hero.spendAndNext(Actor.TICK);
	}

	@Override
	public String abilityInfo() {
		return Messages.get(this, "ability_desc", ABILITY_HITS, ABILITY_POWER);
	}

	@Override
	public int max(int lvl) {
		if (swiching == false) {
			return Math.round(1f * (tier + 1)) +
					lvl * Math.round(0.5f * (tier + 1));
		} else {
			return Math.round(2f * (tier + 1)) +
					lvl * Math.round(1f * (tier + 1)); }
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
		if (swiching == true) {swiching = false; DLY = 0.2f;}
		else {swiching = true; DLY = 0.33f;}

		updateQuickslot();
		curUser.spendAndNext(0.5f);}
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
