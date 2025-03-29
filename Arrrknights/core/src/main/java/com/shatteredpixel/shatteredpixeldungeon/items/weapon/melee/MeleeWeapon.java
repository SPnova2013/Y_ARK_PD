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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.MidoriAccessories;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMistress;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.SP.Badge;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class MeleeWeapon extends Weapon {
	
	public int tier;

	public int charge = 100;
	public int chargeCap = 100;

	@Override
	public int min(int lvl) {
		return  tier +  //base
				lvl	+	//level scaling
						((Dungeon.hero.hasTalent(Talent.PROFICIENCY)&& Maccessories>0)?
								(Maccessories*(int)Math.round(Dungeon.hero.pointsInTalent(Talent.PROFICIENCY)*0.6f)) : 0);
	}

	@Override
	public int max(int lvl) {
		return  5*(tier+1) +    //base
				lvl*(tier+1)+	//level scaling
				((Dungeon.hero.hasTalent(Talent.PROFICIENCY)&& Maccessories>0)?
						(Maccessories*(int)Math.round(Dungeon.hero.pointsInTalent(Talent.PROFICIENCY)*0.8f)) : 0);
	}

	public int STRReq(int lvl){
		int strreq=STRReq(tier, lvl);//change from budding
		if (Dungeon.hero.hasTalent(Talent.CHAINSAW_EXTEND) && isEquipped( Dungeon.hero )) {
			strreq += 5 - Dungeon.hero.pointsInTalent(Talent.CHAINSAW_EXTEND);
		}//change from budding
		if(Dungeon.hero.hasTalent(Talent.PROTECTIONOFLIGHT)) strreq += Maccessories;
		return strreq;//change from budding
	}

	public static String AC_DISASSEMBLE = "DISASSEMBLE";
	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (hero.heroClass == HeroClass.MIDORI){
			actions.add(AC_DISASSEMBLE);
		}
		return actions;
	}
	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (action.equals(AC_DISASSEMBLE)){
			MidoriAccessories fia = new MidoriAccessories();
			if (fia.doPickUp( Dungeon.hero )) {
				GLog.i( Messages.get(Dungeon.hero, "you_now_have", fia.name()) );
				Dungeon.hero.spend(-TIME_TO_PICK_UP);
			} else {
				Dungeon.level.drop( fia, hero.pos ).sprite.drop();
			}
			if(this instanceof GunWeapon){
				MidoriAccessories fia2 = new MidoriAccessories();
				if (fia2.doPickUp( Dungeon.hero )) {
					GLog.i( Messages.get(Dungeon.hero, "you_now_have", fia2.name()) );
					Dungeon.hero.spend(-TIME_TO_PICK_UP);
				} else {
					Dungeon.level.drop( fia2, hero.pos ).sprite.drop();
				}
			}
			if(this.isEquipped(Dungeon.hero)){
				this.cursed = false;
				this.doUnequip(hero, false);
			}else{
				this.detach(hero.belongings.backpack);
			}
		}
	}

	protected int Maccessories = 0;
	public void addAccessories(){
		Maccessories++;
	}
	public void SPCharge(int value) {
		int chargevalue = value;
		chargevalue *= Math.round(RingOfMistress.SPMultiplier(Dungeon.hero)+ Badge.MistressMultiper() -1);
		charge = Math.min(charge+chargevalue, chargeCap);
		updateQuickslot();
	}
	@Override
	public float wepCorrect(){
		return 0;
	}
	@Override
	public int damageRoll(Char owner) {
		int damage = augment.damageFactor(super.damageRoll( owner ));

		if (owner instanceof Hero) {
			int exStr = ((Hero)owner).STR() - STRReq();
			if (exStr > 0) {
				damage += Random.IntRange( 0, exStr );
			}
			if(Dungeon.hero.hasTalent(Talent.STRONGMAN)){
				damage += Random.IntRange( 0, exStr )* ((Hero) owner).pointsInTalent(Talent.STRONGMAN);
			}
		}
		
		return damage;
	}
	
	@Override
	public String info() {

		String info = desc();

		if (levelKnown) {
			info += "\n\n" + Messages.get(MeleeWeapon.class, "stats_known", tier, augment.damageFactor(min()), augment.damageFactor(max()), STRReq());
			if (STRReq() > Dungeon.hero.STR()) {
				info += " " + Messages.get(Weapon.class, "too_heavy");
			} else if (Dungeon.hero.STR() > STRReq()){
				if (Dungeon.hero.hasTalent(Talent.STRONGMAN)) info += " " + Messages.get(Weapon.class, "excess_str", (Dungeon.hero.STR() - STRReq())* (Dungeon.hero.pointsInTalent(Talent.STRONGMAN)+1));
				else info += " " + Messages.get(Weapon.class, "excess_str", Dungeon.hero.STR() - STRReq());
			}
		} else {
			info += "\n\n" + Messages.get(MeleeWeapon.class, "stats_unknown", tier, min(0), max(0), STRReq(0));
			if (isEquipped( Dungeon.hero )){
				if ((STRReq() - Dungeon.hero.STR()) <-1) info += " " + Messages.get(MeleeWeapon.class, "feels_very_light");
				if ((STRReq() - Dungeon.hero.STR())==-1) info += " " + Messages.get(MeleeWeapon.class, "feels_bit_light");
				if ((STRReq() - Dungeon.hero.STR())== 0) info += " " + Messages.get(MeleeWeapon.class, "feels_just_right");
				if ((STRReq() - Dungeon.hero.STR())== 1) info += " " + Messages.get(MeleeWeapon.class, "feels_little_heavy");
				if ((STRReq() - Dungeon.hero.STR()) > 1) info += " " + Messages.get(MeleeWeapon.class, "feels_very_heavy");
			}else if (STRReq(0) > Dungeon.hero.STR()) {
				info += " " + Messages.get(MeleeWeapon.class, "probably_too_heavy");
			}
		}

		String statsInfo = statsInfo();
		if (!statsInfo.equals("")) info += "\n\n" + statsInfo;

		switch (augment) {
			case SPEED:
				info += " " + Messages.get(Weapon.class, "faster");
				break;
			case DAMAGE:
				info += " " + Messages.get(Weapon.class, "stronger");
				break;
			case OVERLOAD:
				info += " " + Messages.get(Weapon.class, "overload");
				break;
			case NONE:
		}

		if (enchantment != null && (cursedKnown || !enchantment.curse())){
			info += "\n\n" + Messages.get(Weapon.class, "enchanted", enchantment.name());
			info += " " + Messages.get(enchantment, "desc");
		}
		if(chimeras != null){
			for(Chimera chis:chimeras){
				info += "\n" + Messages.get(Weapon.class, "chimeraed", chis.name());
				info += " " + Messages.get(chis, "desc");
			}
		}

		if (cursed && isEquipped( Dungeon.hero )) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed_worn");
		} else if (cursedKnown && cursed) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed");
		} else if (!isIdentified() && cursedKnown){
			info += "\n\n" + Messages.get(Weapon.class, "not_cursed");
		}
		
		return info;
	}
	
	public String statsInfo(){
		return Messages.get(this, "stats_desc");
	}
	
	@Override
	public int value() {
		int price = 20 * tier;
		if (hasGoodEnchant()) {
			price *= 1.5;
		}
		if (cursedKnown && (cursed || hasCurseEnchant())) {
			price /= 2;
		}
		if (levelKnown && level() > 0) {
			price *= (level() + 1);
		}
		if (price < 1) {
			price = 1;
		}
		return price;
	}

	private static final String CHARGE = "charge";
	private static final String MACCESSORIES = "maccessories";
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHARGE, charge);
		bundle.put(MACCESSORIES, Maccessories);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (chargeCap > 0) charge = Math.min(chargeCap, bundle.getInt(CHARGE));
		else charge = bundle.getInt(CHARGE);
		Maccessories = bundle.getInt(MACCESSORIES);
	}


}
