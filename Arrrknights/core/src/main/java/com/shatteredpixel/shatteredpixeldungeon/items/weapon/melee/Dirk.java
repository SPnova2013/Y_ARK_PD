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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfAssassin;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Dirk extends MeleeWeapon {

	{
		image = ItemSpriteSheet.DIRK;
		hitSound = Assets.Sounds.HIT_KNIFE;
		hitSoundPitch = 1f;

		tier = 2;
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
