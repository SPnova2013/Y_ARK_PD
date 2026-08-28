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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.IsekaiItem;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

public class ThermiteBlade extends MeleeWeapon {

	{
		image = ItemSpriteSheet.MACE;
		hitSound = Assets.Sounds.ATK_SPIRITBOW;
		hitSoundPitch = 1f;

		tier = 3;
		ACC = 1.28f; //28% boost to accuracy
	}

	@Override
	public int min(int lvl) { return  5 + buffedLvl(); }


	@Override
	public int max(int lvl) {
		return  4*(tier) +    //12 + 3
				lvl*(tier);
	}
	@Override
	public int damageRoll(Char owner) {
		int dmg = super.damageRoll(owner);
		if (curUser.buff(ExtremeSharpness.class) != null){dmg*=2;}
		return dmg;
	}
	@Override
	public String desc() {
		String info = Messages.get(this, "desc");
		if (Dungeon.hero.belongings.getItem(IsekaiItem.class) != null) {
			if (Dungeon.hero.belongings.getItem(IsekaiItem.class).isEquipped(Dungeon.hero))
				info += "\n\n" + Messages.get( ThermiteBlade.class, "setbouns");}

		return info;
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		hero.belongings.abilityWeapon = this;
		MeleeWeapon wep = this;
		wep.beforeAbilityUsed(hero, null);
		Buff.affect(hero, ExtremeSharpness.class).set(5f + buffedLvl());
		wep.afterAbilityUsed(hero);
	}

	public static class ExtremeSharpness extends Buff {
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
			icon.hardlight(0.25f, 1.5f, 1f);
		}
		@Override
		public String toString() {
			return Messages.get(this, "name");
		}
		@Override
		public String desc() {
			return Messages.get(this, "desc");
		}

		float left;
		private void set(float turns){
			left = turns;
		}
		@Override
		public boolean act() {
			left--;
			if(left<=0) detach();
			spend( TICK );
			return true;
		}
	}
}
