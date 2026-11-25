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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.LeatherArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class AssassinsBlade extends MeleeWeapon {

	{
		image = ItemSpriteSheet.ASSASSINS_BLADE;
		hitSound = Assets.Sounds.HIT_KNIFE;
		hitSoundPitch = 0.9f;

		tier = 4;
	}

	//潜行: blink a short way, and vanish for a moment
	public static final int ABILITY_RANGE = 5;
	public static final float ABILITY_INVIS = 5f;

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	public String abilityInfo() {
		return Messages.get(this, "ability_desc", ABILITY_RANGE, (int) ABILITY_INVIS);
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		if (target == null || target == hero.pos) {
			return;
		}
		if (Dungeon.level.distance(hero.pos, target) > ABILITY_RANGE
				|| !Dungeon.level.heroFOV[target]
				|| Dungeon.level.solid[target]
				|| !Dungeon.level.passable[target]
				|| Actor.findChar(target) != null) {
			GLog.w(Messages.get(this, "bad_target"));
			return;
		}

		hero.belongings.abilityWeapon = this;
		beforeAbilityUsed(hero, null);

		//the step itself, exactly like a teleport scroll
		ScrollOfTeleportation.appear(hero, target);
		Dungeon.level.occupyCell(hero);
		Dungeon.observe();
		GameScene.updateFog();

		//and out of sight for a moment
		Buff.affect(hero, Invisibility.class, ABILITY_INVIS);

		Sample.INSTANCE.play(Assets.Sounds.TELEPORT);

		afterAbilityUsed(hero);
		hero.spendAndNext(Actor.TICK);
	}

	@Override
	public int max(int lvl) {
		return  4*(tier+1) +    //20 base, down from 25
				lvl*(tier+1);   //scaling unchanged
	}
	@Override
	public float wepCorrect(){
		if (Dungeon.hero.belongings.armor instanceof LeatherArmor) return 0.60f;
		return 0.50f;
	}

	@Override
	public String desc() {
		String info = Messages.get(this, "desc");
		if (Dungeon.hero.belongings.armor instanceof LeatherArmor) info += "\n\n" + Messages.get( AssassinsBlade.class, "setbouns");

		return info;
	}

}