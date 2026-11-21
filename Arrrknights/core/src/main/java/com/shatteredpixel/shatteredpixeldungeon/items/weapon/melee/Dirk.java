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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NightShade;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfAssassin;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Dirk extends MeleeWeapon {

	{
		image = ItemSpriteSheet.DIRK;
		hitSound = Assets.Sounds.HIT_KNIFE;
		hitSoundPitch = 1f;

		tier = 2;
	}

	//夜幕突袭: call a shadow, and let the dark sweep through both of you
	public static final int ABILITY_RADIUS = 1;
	public static final float ABILITY_STATUS_DURATION = 3f;

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	public String abilityInfo() {
		return Messages.get(this, "ability_desc", (int) ABILITY_STATUS_DURATION);
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		if (target == null) {
			return;
		}
		if (target == hero.pos || !Dungeon.level.heroFOV[target]
				|| Dungeon.level.solid[target] || !Dungeon.level.passable[target]
				|| Actor.findChar(target) != null) {
			GLog.w(Messages.get(this, "bad_target"));
			return;
		}

		hero.belongings.abilityWeapon = this;
		beforeAbilityUsed(hero, null);

		NightShade shade = new NightShade();
		shade.setup(hero);
		GameScene.add(shade);
		ScrollOfTeleportation.appear(shade, target);
		Sample.INSTANCE.play(Assets.Sounds.TELEPORT);

		//everything around either of them is hit, standing next to both means twice
		sweep(hero, hero.pos);
		sweep(hero, shade.pos);

		Invisibility.dispel();

		afterAbilityUsed(hero);
		hero.spendAndNext(Actor.TICK);
	}

	//damage, shove and a random pin for everything next to one centre
	private void sweep(Hero hero, int center) {
		int dmg = hero.damageRoll();

		for (int i : PathFinder.NEIGHBOURS8) {
			int cell = center + i;
			if (cell < 0 || cell >= Dungeon.level.length()) continue;
			//guard against stepping around the edge of a row
			if (Dungeon.level.distance(center, cell) != 1) continue;

			Char ch = Actor.findChar(cell);
			if (ch == null || ch == hero || !ch.isAlive()) continue;
			if (ch.alignment != Char.Alignment.ENEMY) continue;

			ch.damage(dmg, hero);
			if (!ch.isAlive()) continue;

			//shove it a tile away from the centre it was standing next to
			Ballistica trajectory = new Ballistica(center, ch.pos, Ballistica.STOP_TARGET);
			trajectory = new Ballistica(trajectory.collisionPos,
					trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
			WandOfBlastWave.throwChar(ch, trajectory, 1, true, true, true);

			//then one of three ways to keep it there
			switch (Random.Int(3)) {
				case 0:  Buff.affect(ch, Paralysis.class, ABILITY_STATUS_DURATION); break;
				case 1:  Buff.affect(ch, Roots.class, ABILITY_STATUS_DURATION); break;
				default: Buff.affect(ch, Vertigo.class, ABILITY_STATUS_DURATION); break;
			}
		}
	}

	@Override
	public int max(int lvl) {
		return  4*(tier+1) +    //12 base, down from 15
				lvl*(tier+1);   //scaling unchanged
	}
	@Override
	public float wepCorrect(){
		if (Dungeon.hero.belongings.getItem(RingOfAssassin.class) != null && Dungeon.hero.belongings.getItem(RingOfAssassin.class).isEquipped(Dungeon.hero))
			return 1f;
		else return 0.67f;
	}

	@Override
	public String desc() {
		String info = Messages.get(this, "desc");
		if (Dungeon.hero.belongings.getItem(RingOfAssassin.class) != null) {
			if (Dungeon.hero.belongings.getItem(RingOfAssassin.class).isEquipped(Dungeon.hero))
				info += "\n\n" + Messages.get( Dirk.class, "setbouns");}

		return info;
	}

}
