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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import static com.shatteredpixel.shatteredpixeldungeon.items.ror2items.LightFluxPauldron.LFPChargeMultiplier;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ChaliceOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.Aegis;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.TitanicKnurl;

public class Regeneration extends Buff {
	
	{
		//unlike other buffs, this one acts after the hero and takes priority against other effects
		//healing is much more useful if you get some of it off before taking damage
		actPriority = HERO_PRIO - 1;
	}
	
	private static final float REGENERATION_DELAY = 10;

	private static int finalregamt = 1;
	
	@Override
	public boolean act() {
		finalregamt = 1;
		if(Dungeon.hero.hasTalent(Talent.LICK_BLOOD)){
			if(Dungeon.hero.pointsInTalent(Talent.LICK_BLOOD)==1){
				if(Dungeon.hero.HP*3<Dungeon.hero.HT) finalregamt += 2;
				else if(Dungeon.hero.HP*2<Dungeon.hero.HT) finalregamt ++;
			}else if(Dungeon.hero.pointsInTalent(Talent.LICK_BLOOD)==2){
				if(Dungeon.hero.HP*2<Dungeon.hero.HT) finalregamt += 2;
				else if(Dungeon.hero.HP*4<Dungeon.hero.HT*3) finalregamt ++;
			}
		}//这写的nm什么玩意啊

		if(Dungeon.hero.buff(TitanicKnurl.TitanicKnurlBuff.class) != null) finalregamt += 1;

		if (target.isAlive()) {

			if (target.HP < regencap() && !((Hero)target).isStarving()) {
				LockedFloor lock = target.buff(LockedFloor.class);
				if (target.HP > 0 && (lock == null || lock.regenOn())) {
					if (target instanceof Hero && Dungeon.hero.buff( Aegis.AegisBuff.class) != null){
						Aegis.addShield(finalregamt);
					}
					target.HP += finalregamt;
					if(target.HP> target.HT)target.HP= target.HT;
					if (target.HP == regencap()) {
						((Hero) target).resting = false;
					}
				}
			}

			ChaliceOfBlood.chaliceRegen regenBuff = Dungeon.hero.buff( ChaliceOfBlood.chaliceRegen.class);

			float delay = REGENERATION_DELAY;
			if (regenBuff != null) {
				if (regenBuff.isCursed()) {
					delay *= 1.5f;
				} else {
					delay -= regenBuff.itemLevel()*0.81f;
					delay /= RingOfEnergy.artifactChargeMultiplier(target) * LFPChargeMultiplier();
				}
			}
			if(Dungeon.hero.resting && Dungeon.hero.hasTalent(Talent.FAST_TRIM)){
				delay *= (1 - Dungeon.hero.pointsInTalent(Talent.FAST_TRIM)/10.0f);
			}
			spend( delay );
			
		} else {
			
			diactivate();
			
		}
		
		return true;
	}
	
	public int regencap(){
		return target.HT;
	}

	public static boolean regenOn(){
		LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
		if (lock != null && !lock.regenOn()){
			return false;
		}
		/*if (Dungeon.level instanceof VaultLevel){
			return false;
		}*/
		return true;
	}
}
