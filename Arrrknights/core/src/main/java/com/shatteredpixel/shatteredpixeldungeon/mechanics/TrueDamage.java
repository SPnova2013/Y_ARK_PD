package com.shatteredpixel.shatteredpixeldungeon.mechanics;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;

/*
	Damage applied straight to HP: no armor, no resistances, no immunities, no shields.

	This is the same behaviour as the second rank of the MYSTERY_SHOT talent, but it can be
	called from anywhere. Because the normal damage pipeline is skipped, mobs are still woken
	up here so they notice whoever hit them.

		TrueDamage.deal( enemy, 50, Dungeon.hero );
 */
public class TrueDamage {

	public static void deal( Char target, int dmg, Object src ) {

		if (target == null || !target.isAlive() || dmg <= 0) {
			return;
		}

		if (target.sprite != null) {
			target.sprite.showStatus( CharSprite.DEFAULT, Integer.toString( dmg ) );
		}

		target.HP -= dmg;
		if (target.HP < 0) {
			target.HP = 0;
		}

		//the damage pipeline was skipped, so make mobs react by hand
		if (target instanceof Mob) {
			((Mob) target).beckon( Dungeon.hero.pos );
			((Mob) target).damage( -1, src );
		}

		if (!target.isAlive()) {
			target.die( src );
		}
	}
}
