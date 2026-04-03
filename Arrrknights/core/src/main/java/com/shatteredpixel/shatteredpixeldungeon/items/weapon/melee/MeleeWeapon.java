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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArtifactRecharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.MidoriAccessories;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMistress;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.SP.Badge;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
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

	public interface BlockingWeapon {
		int DRMax(int lvl);
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
	public static String AC_ABILITY = "ABILITY";

	@Override
	public void activate(Char ch) {
		super.activate(ch);
		if (ch instanceof Hero && ((Hero) ch).heroClass == HeroClass.IRENE){
			Buff.affect(ch, Charger.class);
		}
	}
	@Override
	public String defaultAction() {
		if (Dungeon.hero != null && (Dungeon.hero.heroClass == HeroClass.IRENE/*
				|| Dungeon.hero.hasTalent(Talent.SWIFT_EQUIP)*/)){
			return AC_ABILITY;
		} else {
			return super.defaultAction();
		}
	}
	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (hero.heroClass == HeroClass.MIDORI){
			actions.add(AC_DISASSEMBLE);
		}
		if (isEquipped(hero) && hero.heroClass == HeroClass.IRENE){
			actions.add(AC_ABILITY);
		}
		return actions;
	}
	@Override
	public String actionName(String action, Hero hero) {
		if (action.equals(AC_ABILITY)){
			return Messages.get(this, "ability_name");
		} else {
			return super.actionName(action, hero);
		}
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

		if (action.equals(AC_ABILITY)){
			usesTargeting = false;
			if (!isEquipped(hero)) {
				/*if (hero.hasTalent(Talent.SWIFT_EQUIP)){
					if (hero.buff(Talent.SwiftEquipCooldown.class) == null
							|| hero.buff(Talent.SwiftEquipCooldown.class).hasSecondUse()){
						execute(hero, AC_EQUIP);
					} else if (hero.heroClass == HeroClass.IRENE) {
						GLog.w(Messages.get(this, "ability_need_equip"));
					}
				} else */if (hero.heroClass == HeroClass.IRENE) {
					GLog.w(Messages.get(this, "ability_need_equip"));
				}
			} else if (hero.heroClass != HeroClass.IRENE){
				//do nothing
			} else if (STRReq() > hero.STR()){
				GLog.w(Messages.get(this, "ability_low_str"));
			} else if ((Buff.affect(hero, Charger.class).charges + Buff.affect(hero, Charger.class).partialCharge) < abilityChargeUse(hero, null)) {
				GLog.w(Messages.get(this, "ability_no_charge"));
			} else {

				if (targetingPrompt() == null){
					duelistAbility(hero, hero.pos);
					updateQuickslot();
				} else {
					usesTargeting = useTargeting();
					GameScene.selectCell(new CellSelector.Listener() {
						@Override
						public void onSelect(Integer cell) {
							if (cell != null) {
								duelistAbility(hero, cell);
								updateQuickslot();
							}
						}

						@Override
						public String prompt() {
							return targetingPrompt();
						}
					});
				}
			}
		}
	}
	//leave null for no targeting
	public String targetingPrompt(){
		return null;
	}

	public boolean useTargeting(){
		return targetingPrompt() != null;
	}

	@Override
	public int targetingPos(Hero user, int dst) {
		return dst; //weapon abilities do not use projectile logic, no autoaim
	}

	protected void duelistAbility( Hero hero, Integer target ){
		//do nothing by default
	}

	protected void beforeAbilityUsed(Hero hero, Char target){
		hero.belongings.abilityWeapon = this;
		Charger charger = Buff.affect(hero, Charger.class);

		charger.partialCharge -= abilityChargeUse(hero, target);
		while (charger.partialCharge < 0 && charger.charges > 0) {
			charger.charges--;
			charger.partialCharge++;
		}
		updateQuickslot();
	}

	protected void afterAbilityUsed( Hero hero ){}
	public static void onAbilityKill( Hero hero, Char killed ){}
	protected int baseChargeUse(Hero hero, Char target){
		return 1; //abilities use 1 charge by default
	}

	public final float abilityChargeUse(Hero hero, Char target){
		return baseChargeUse(hero, target);
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

		if (Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.IRENE && !(this instanceof MagesStaff)){
			info += "\n\n" + abilityInfo();
		}
		
		return info;
	}
	public String abilityInfo() {
		return Messages.get(this, "ability_desc");
	}
	
	public String statsInfo(){
		return Messages.get(this, "stats_desc");
	}
	@Override
	public String status() {
		if (isEquipped(Dungeon.hero)
				&& Dungeon.hero.buff(Charger.class) != null) {
			Charger buff = Dungeon.hero.buff(Charger.class);
			return buff.charges + "/" + buff.chargeCap();
		} else {
			return super.status();
		}
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

	public static class Charger extends Buff implements ActionIndicator.Action {

		{
			//so that duelist keeps weapon charge on ankh revive
			//revivePersists = true;
			isPermanent = true;
		}

		public int charges = 2;
		public float partialCharge;

		@Override
		public boolean act() {
			if (charges < chargeCap()){
				if (Regeneration.regenOn()){
					//60 to 45 turns per charge
					float chargeToGain = 1/(60f-1.5f*(chargeCap()-charges));

					//40 to 30 turns per charge for champion
					if (Dungeon.hero.subClassSet.contains(HeroSubClass.INQUISITOR)){
						chargeToGain *= 1.5f;
					}

					//50% slower charge gain with brawler's stance enabled, even if buff is inactive
					if (Dungeon.hero.buff(RingOfForce.BrawlersStance.class) != null){
						chargeToGain *= 0.50f;
					}

					partialCharge += chargeToGain;
				}

				int points = 0/*((Hero)target).pointsInTalent(Talent.WEAPON_RECHARGING)*/;
				if (points > 0 && target.buff(Recharging.class) != null || target.buff(ArtifactRecharge.class) != null){
					//1 every 15 turns at +1, 10 turns at +2
					partialCharge += 1/(20f - 5f*points);
				}

				if (partialCharge >= 1){
					charges++;
					partialCharge--;
					updateQuickslot();
				}
			} else {
				partialCharge = 0;
			}

			if (ActionIndicator.action != this && Dungeon.hero.subClassSet.contains(HeroSubClass.INQUISITOR)) {
				ActionIndicator.setAction(this);
			}

			spend(TICK);
			return true;
		}

		@Override
		public void fx(boolean on) {
			if (on && Dungeon.hero.subClassSet.contains(HeroSubClass.INQUISITOR)) {
				ActionIndicator.setAction(this);
			}
		}

		@Override
		public void detach() {
			super.detach();
			ActionIndicator.clearAction(this);
		}

		public int chargeCap(){
			//caps at level 19 with 8 or 10 charges
			if (Dungeon.hero.subClassSet.contains(HeroSubClass.INQUISITOR)){
				return Math.min(10, 4 + (Dungeon.hero.lvl - 1) / 3);
			} else {
				return Math.min(8, 2 + (Dungeon.hero.lvl - 1) / 3);
			}
		}

		public void gainCharge( float charge ){
			if (charges < chargeCap()) {
				partialCharge += charge;
				while (partialCharge >= 1f) {
					charges++;
					partialCharge--;
				}
				if (charges >= chargeCap()){
					partialCharge = 0;
					charges = chargeCap();
				}
				updateQuickslot();
			}
		}

		public static final String CHARGES          = "charges";
		private static final String PARTIALCHARGE   = "partialCharge";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(CHARGES, charges);
			bundle.put(PARTIALCHARGE, partialCharge);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			charges = bundle.getInt(CHARGES);
			partialCharge = bundle.getFloat(PARTIALCHARGE);
		}

		@Override
		public String actionName() {
			return Messages.get(MeleeWeapon.class, "swap");
		}

		@Override
		public int actionIcon() {
			return HeroIcon.WEAPON_SWAP;
		}

		@Override
		public Visual primaryVisual() {
			Image ico;
			if (Dungeon.hero.belongings.weapon == null){
				ico = new HeroIcon(this);
			} else {
				ico = new ItemSprite(Dungeon.hero.belongings.weapon);
			}
			ico.width += 4; //shift slightly to the left to separate from smaller icon
			return ico;
		}

		@Override
		public Visual secondaryVisual() {
			Image ico;
			if (Dungeon.hero.belongings.secondWep == null){
				ico = new HeroIcon(this);
			} else {
				ico = new ItemSprite(Dungeon.hero.belongings.secondWep);
			}
			ico.scale.set(PixelScene.align(0.51f));
			ico.brightness(0.6f);
			return ico;
		}

		@Override
		public int indicatorColor() {
			return 0x5500BB;
		}

		@Override
		public void doAction() {
			if (Dungeon.hero.subClassSet.contains((HeroSubClass.INQUISITOR))){
				return;
			}

			if (Dungeon.hero.belongings.secondWep == null && Dungeon.hero.belongings.backpack.items.size() >= Dungeon.hero.belongings.backpack.capacity()){
				GLog.w(Messages.get(MeleeWeapon.class, "swap_full"));
				return;
			}

			KindOfWeapon temp = Dungeon.hero.belongings.weapon;
			Dungeon.hero.belongings.weapon = Dungeon.hero.belongings.secondWep;
			Dungeon.hero.belongings.secondWep = temp;

			Dungeon.hero.sprite.operate(Dungeon.hero.pos);
			Sample.INSTANCE.play(Assets.Sounds.UNLOCK);

			ActionIndicator.setAction(this);
			Item.updateQuickslot();
			AttackIndicator.updateState();
		}
	}
}
