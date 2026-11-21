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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.coversprite.WolvesCover;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Dagger extends MeleeWeapon {
	
	{
		image = ItemSpriteSheet.DAGGER;
		hitSound = Assets.Sounds.HIT_KNIFE;
		hitSoundPitch = 1.1f;

		tier = 1;
		
		bones = false;
	}

	//狼群: blink in and let the pack bite whatever is beside the landing spot
	public static final float ABILITY_DAMAGE_MULT = 2f;
	public static final float ABILITY_PARALYSIS = 2f;

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	public String abilityInfo() {
		return Messages.get(this, "ability_desc",
				(int) (ABILITY_DAMAGE_MULT * 100), (int) ABILITY_PARALYSIS);
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		if (target == null || target == hero.pos) {
			return;
		}
		if (!Dungeon.level.heroFOV[target] || Dungeon.level.solid[target]
				|| !Dungeon.level.passable[target] || Actor.findChar(target) != null) {
			GLog.w(Messages.get(this, "bad_target"));
			return;
		}

		hero.belongings.abilityWeapon = this;
		beforeAbilityUsed(hero, null);

		//the pack answers the call as the hero arrives
		new WolvesCover().centerAndPlay(target);

		//blink, exactly like the teleport scroll does for the hero
		ScrollOfTeleportation.appear(hero, target);
		Dungeon.level.occupyCell(hero);
		Dungeon.observe();
		GameScene.updateFog();

		//and the pack goes for the four neighbours
		int dmg = Math.round(hero.damageRoll() * ABILITY_DAMAGE_MULT);

		for (int i : PathFinder.NEIGHBOURS4) {
			int cell = target + i;
			if (!Dungeon.level.insideMap(cell)) continue;
			if (Dungeon.level.distance(target, cell) != 1) continue;

			Char ch = Actor.findChar(cell);
			if (ch == null || ch == hero || !ch.isAlive()) continue;
			if (ch.alignment != Char.Alignment.ENEMY) continue;

			ch.damage(dmg, hero);
			if (ch.isAlive()) {
				Buff.affect(ch, Paralysis.class, ABILITY_PARALYSIS);
				ch.sprite.centerEmitter().burst(Speck.factory(Speck.LIGHT), 4);
			}
		}

		CellEmitter.get(target).burst(Speck.factory(Speck.LIGHT), 8);
		Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
		Invisibility.dispel();

		afterAbilityUsed(hero);
		hero.spendAndNext(Actor.TICK);
	}

	@Override
	public int max(int lvl) {
		return  4*(tier+1) + 1 +    //9 base, down from 10
				lvl*(tier+1);   //scaling unchanged
	}
	@Override
	public float wepCorrect(){
		return 0.75f;
	}
}
