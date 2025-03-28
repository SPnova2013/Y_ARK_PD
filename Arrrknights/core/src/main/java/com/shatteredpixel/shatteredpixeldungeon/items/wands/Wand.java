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

package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import static com.shatteredpixel.shatteredpixeldungeon.items.ror2items.LightFluxPauldron.LFPChargeMultiplier;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Camouflage;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SoulMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.Bonk;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.StaffKit;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.LightFluxPauldron;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SP.StaffOfSkyfire;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SSP.StaffOfConcept;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public abstract class Wand extends Item {

	public static final String AC_ZAP	= "ZAP";

	private static final float TIME_TO_ZAP	= 1f;
	
	public int maxCharges = initialCharges();
	public int curCharges = maxCharges;
	public float partialCharge = 0f;
	
	protected Charger charger;
	
	private boolean curChargeKnown = false;
	
	public boolean curseInfusionBonus = false;
	
	private static final int USES_TO_ID = 10;
	private float usesLeftToID = USES_TO_ID;
	private float availableUsesToID = USES_TO_ID/2f;

	protected int collisionProperties = Ballistica.MAGIC_BOLT;

	public boolean solidified = false;
	public int solidrarity = -1;
	public int solidtype = -1;
	
	{
		defaultAction = AC_ZAP;
		usesTargeting = true;
		bones = true;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (curCharges > 0 || !curChargeKnown) {
			actions.add( AC_ZAP );
		}

		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals( AC_ZAP )) {
			
			curUser = hero;
			curItem = this;
			GameScene.selectCell( zapper );
			
		}
	}

	@Override
	public int targetingPos(Hero user, int dst) {
		return new Ballistica( user.pos, dst, collisionProperties ).collisionPos;
	}

	protected abstract void onZap(Ballistica attack );

	public abstract void onHit( MagesStaff staff, Char attacker, Char defender, int damage);

	public boolean tryToZap( Hero owner, int target ){

		if (owner.buff(MagicImmune.class) != null || owner.buff(Silence.class) != null){
			GLog.w( Messages.get(this, "no_magic") );
			return false;
		}

		if ( curCharges >= (cursed ? 1 : chargesPerCast())){
			return true;
		} else {
			GLog.w(Messages.get(this, "fizzles"));
			return false;
		}
	}

	@Override
	public boolean collect( Bag container ) {
		if (super.collect( container )) {
			if (container.owner != null) {
				if (container instanceof MagicalHolster)
					charge( container.owner, ((MagicalHolster) container).HOLSTER_SCALE_FACTOR );
				else
					charge( container.owner );
			}
			return true;
		} else {
			return false;
		}
	}

	public void gainCharge( float amt ){
		gainCharge( amt, false );
	}

	public void gainCharge( float amt, boolean overcharge ){
		partialCharge += amt;
		while (partialCharge >= 1) {
			if (overcharge) curCharges = Math.min(maxCharges+(int)amt, curCharges+1);
			else curCharges = Math.min(maxCharges, curCharges+1);
			partialCharge--;
			updateQuickslot();
		}
	}
	
	public void charge( Char owner ) {
		if (charger == null) charger = new Charger();
		charger.attachTo( owner );
	}

	public void charge( Char owner, float chargeScaleFactor ){
		charge( owner );
		charger.setScaleFactor( chargeScaleFactor );
	}

	protected void processSoulMark(Char target, int chargesUsed){
		processSoulMark(target, buffedLvl(), chargesUsed);
		if (Dungeon.hero.hasTalent(Talent.MIND_CRASH)&&target != Dungeon.hero) { Buff.affect(target, Talent.MindCrashMark.class, 2f); }
	}

	//TODO some naming issues here. Consider renaming this method and externalizing char awareness buff
	protected static void processSoulMark(Char target, int wandLevel, int chargesUsed){
		if (Dungeon.hero.hasTalent(Talent.ARCANE_SNIPE)) {
			int dur = 5*Dungeon.hero.pointsInTalent(Talent.ARCANE_SNIPE);
			Buff.append(Dungeon.hero, TalismanOfForesight.CharAwareness.class, dur).charID = target.id();
			Camouflage.dispelCamouflage(target);
		}

		if (target != Dungeon.hero &&
				Dungeon.hero.subClassSet.contains(HeroSubClass.WARLOCK) &&
				//standard 1 - 0.95^x chance, plus 7%. Starts at 15%, 특성으로 0.8부분에 수치당 +0.83.. 3업시 0.25
				Random.Float() > (Math.pow(0.95f - Dungeon.hero.pointsInTalent(Talent.EMOTION) / 15f, (wandLevel*chargesUsed)+1) - 0.07f)){//change from budding
			SoulMark.prolong(target, SoulMark.class, SoulMark.DURATION + wandLevel + (Dungeon.hero.pointsInTalent(Talent.LORD) * 2));
		}
	}

	@Override
	public void onDetach( ) {
		stopCharging();
	}

	public void stopCharging() {
		if (charger != null) {
			charger.detach();
			charger = null;
		}
	}
	
	public void level( int value) {
		super.level( value );
		updateLevel();
	}
	
	@Override
	public Item identify() {
		
		curChargeKnown = true;
		super.identify();
		
		updateQuickslot();
		
		return this;
	}
	
	public void onHeroGainExp( float levelPercent, Hero hero ){
		levelPercent *= Talent.itemIDSpeedFactor(hero, this);
		if (!isIdentified() && availableUsesToID <= USES_TO_ID/2f) {
			//gains enough uses to ID over 1 level
			availableUsesToID = Math.min(USES_TO_ID/2f, availableUsesToID + levelPercent * USES_TO_ID/2f);
		}
	}

	@Override
	public String info() {
		String desc = desc();

		desc += "\n\n" + statsDesc();

		if (cursed && cursedKnown) {
			desc += "\n\n" + Messages.get(Wand.class, "cursed");
			desc += (solidified? Messages.get(Wand.class, "solid"):Messages.get(Wand.class, "notsolid"));
			if(solidified){
				desc += "\n\n" + Messages.get(Wand.class, "solidified") + Messages.get(Wand.class, "solidified"+solidrarity+"-"+solidtype);
			}
		} else if (!isIdentified() && cursedKnown){
			desc += "\n\n" + Messages.get(Wand.class, "not_cursed");
		}

		if (Dungeon.hero.subClassSet.contains(HeroSubClass.BATTLEMAGE)){
			desc += "\n\n" + Messages.get(this, "bmage_desc");
		}

		return desc;
	}

	public String statsDesc(){
		return Messages.get(this, "stats_desc");
	}
	
	@Override
	public boolean isIdentified() {
		return super.isIdentified() && curChargeKnown;
	}
	
	@Override
	public String status() {
		if (levelKnown) {
			return (curChargeKnown ? curCharges : "?") + "/" + maxCharges;
		} else {
			return null;
		}
	}
	
	@Override
	public int level() {
		if (!cursed && curseInfusionBonus){
			curseInfusionBonus = false;
			updateLevel();
		}
		return super.level() + (curseInfusionBonus ? 1 : 0);
	}
	
	@Override
	public Item upgrade() {

		super.upgrade();

		if (Random.Int(3) == 0) {
			cursed = false;
		}

		updateLevel();
		curCharges = Math.min( curCharges + 1, maxCharges );
		updateQuickslot();
		
		return this;
	}
	
	@Override
	public Item degrade() {
		super.degrade();
		
		updateLevel();
		updateQuickslot();
		
		return this;
	}

	@Override
	public int buffedLvl() {
		int lvl = super.buffedLvl();

		if (charger != null && charger.target != null) {
			WandOfMagicMissile.MagicCharge buff = charger.target.buff(WandOfMagicMissile.MagicCharge.class);
			if (buff != null && buff.level() > lvl){
				return buff.level();
			}
		}
		return lvl;
	}

	public void updateLevel() {
		maxCharges = Math.min( initialCharges() + level(), 10 );
		curCharges = Math.min( curCharges, maxCharges );
	}
	
	protected int initialCharges() {
		return 2;
	}

	protected int chargesPerCast() {
		return 1;
	}
	
	protected void fx( Ballistica bolt, Callback callback ) {
		MagicMissile.boltFromChar( curUser.sprite.parent,
				MagicMissile.MAGIC_MISSILE,
				curUser.sprite,
				bolt.collisionPos,
				callback);
		Sample.INSTANCE.play( Assets.Sounds.ZAP );
	}

	protected void wandUsed() {
		if (!isIdentified()) {
			float uses = Math.min( availableUsesToID, Talent.itemIDSpeedFactor(Dungeon.hero, this) );
			availableUsesToID -= uses;
			usesLeftToID -= uses;
			if (usesLeftToID <= 0 || Dungeon.hero.pointsInTalent(Talent.SCHOLARS_INTUITION) == 2) {
				identify();
				GLog.p( Messages.get(Wand.class, "identify") );
				Badges.validateItemLevelAquired( this );
			}
		}
		
		curCharges -= cursed ? 1 : chargesPerCast();

		WandOfMagicMissile.MagicCharge buff = curUser.buff(WandOfMagicMissile.MagicCharge.class);
		if (buff != null && buff.level() > super.buffedLvl()){
			buff.detach();
		}

		//if the wand is owned by the hero, but not in their inventory, it must be in the staff
		if (charger != null
				&& charger.target == Dungeon.hero
				&& !Dungeon.hero.belongings.contains(this)) {
			if (curCharges == 0 && Dungeon.hero.hasTalent(Talent.BACKUP_BARRIER)) {
				//grants 4/6 shielding
				Buff.affect(Dungeon.hero, Barrier.class).setShield(3 * Dungeon.hero.pointsInTalent(Talent.BACKUP_BARRIER));
			}
			if (Dungeon.hero.hasTalent(Talent.EMPOWERED_STRIKE)){
				Buff.prolong(Dungeon.hero, Talent.EmpoweredStrikeTracker.class, 5f);
			}
		}

		Invisibility.dispel();
		updateQuickslot();

		if((curItem instanceof StaffOfConcept)){
			curUser.spendAndNext(0f);
		}else{
			curUser.spendAndNext(TIME_TO_ZAP);
		}
	}
	
	@Override
	public Item random() {
		//+0: 66.67% (2/3)
		//+1: 26.67% (4/15)
		//+2: 6.67%  (1/15)
		int n = 0;
		if (Random.Int(3) == 0) {
			n++;
			if (Random.Int(5) == 0){
				n++;
			}
		}
		level(n);
		curCharges += n;
		
		//30% chance to be cursed
		if (Random.Float() < 0.3f) {
			cursed = true;
		}

		return this;
	}

	public Class upToStaff() {
		return null;
	}
	public void upToStaff(Class c) {
		if(c != null){
			Wand wand = this;
			Wand n = (Wand)Reflection.newInstance( c ); // 이 부분이랑 조건만 바꾸면 됨.
			n.level(0);
			int level = wand.level();
			if (wand.curseInfusionBonus) level--;
			n.upgrade(level);
			n.levelKnown = wand.levelKnown;
			n.cursedKnown = wand.cursedKnown;
			n.cursed = wand.cursed;
			n.curseInfusionBonus = wand.curseInfusionBonus;

			n.collect();
			wand.detach( curUser.belongings.backpack );
		}else{
			GLog.w( Messages.get(StaffKit.class, "fail") );
			StaffKit S = new StaffKit();
			S.quantity(1).collect();
		}
	}
	
	@Override
	public int value() {
		int price = 75;
		if (cursed && cursedKnown) {
			price /= 2;
		}
		if (levelKnown) {
			if (level() > 0) {
				price *= (level() + 1);
			} else if (level() < 0) {
				price /= (1 - level());
			}
		}
		if (price < 1) {
			price = 1;
		}
		return price;
	}
	
	private static final String USES_LEFT_TO_ID = "uses_left_to_id";
	private static final String AVAILABLE_USES  = "available_uses";
	private static final String CUR_CHARGES         = "curCharges";
	private static final String CUR_CHARGE_KNOWN    = "curChargeKnown";
	private static final String PARTIALCHARGE       = "partialCharge";
	private static final String CURSE_INFUSION_BONUS = "curse_infusion_bonus";
	private static final String SOLIDIFIED = "solidified";
	private static final String SOLIDRARITY = "solidrarity";
	private static final String SOLIDTYPE = "solidtype";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( USES_LEFT_TO_ID, usesLeftToID );
		bundle.put( AVAILABLE_USES, availableUsesToID );
		bundle.put( CUR_CHARGES, curCharges );
		bundle.put( CUR_CHARGE_KNOWN, curChargeKnown );
		bundle.put( PARTIALCHARGE , partialCharge );
		bundle.put(CURSE_INFUSION_BONUS, curseInfusionBonus );
		bundle.put( SOLIDIFIED, solidified );
		bundle.put( SOLIDRARITY , solidrarity );
		bundle.put(SOLIDTYPE, solidtype );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		usesLeftToID = bundle.getInt( USES_LEFT_TO_ID );
		availableUsesToID = bundle.getInt( AVAILABLE_USES );
		
		curCharges = bundle.getInt( CUR_CHARGES );
		curChargeKnown = bundle.getBoolean( CUR_CHARGE_KNOWN );
		partialCharge = bundle.getFloat( PARTIALCHARGE );
		curseInfusionBonus = bundle.getBoolean(CURSE_INFUSION_BONUS);
		solidified = bundle.getBoolean(SOLIDIFIED);
		solidrarity = bundle.getInt(SOLIDRARITY);
		solidtype = bundle.getInt(SOLIDTYPE);
	}
	
	@Override
	public void reset() {
		super.reset();
		usesLeftToID = USES_TO_ID;
		availableUsesToID = USES_TO_ID/2f;
	}

	protected int collisionProperties( int target ){
		return collisionProperties;
	}
	
	protected static CellSelector.Listener zapper = new  CellSelector.Listener() {
		
		@Override
		public void onSelect( Integer target ) {
			
			if (target != null) {
				
				//FIXME this safety check shouldn't be necessary
				//it would be better to eliminate the curItem static variable.
				final Wand curWand;
				if (curItem instanceof Wand) {
					curWand = (Wand) Wand.curItem;
				} else {
					return;
				}

				final Ballistica shot = new Ballistica( curUser.pos, target, curWand.collisionProperties(target));
				int cell = shot.collisionPos;
				
				if (target == curUser.pos || cell == curUser.pos) {
					if (target == curUser.pos && curUser.hasTalent(Talent.SHIELD_BATTERY)){
						float shield = curUser.HT * (0.025f*curWand.curCharges);
						if (curUser.pointsInTalent(Talent.SHIELD_BATTERY) == 2) shield *= 2f;
						Buff.affect(curUser, Barrier.class).setShield(Math.round(shield));
						curWand.curCharges = 0;
						curUser.sprite.operate(curUser.pos);
						Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
						ScrollOfRecharging.charge(curUser);
						updateQuickslot();
						curUser.spend(Actor.TICK);
						return;
					}
					GLog.i( Messages.get(Wand.class, "self_target") );
					return;
				}

				curUser.sprite.zap(cell);
				if(curUser.buff(Bonk.BonkBuff.class) != null) Buff.detach(curUser, Bonk.BonkBuff.class);

				//attempts to target the cell aimed at if something is there, otherwise targets the collision pos.
				if (Actor.findChar(target) != null)
					QuickSlotButton.target(Actor.findChar(target));
				else
					QuickSlotButton.target(Actor.findChar(cell));
				
				if (curWand.tryToZap(curUser, target)) {
					
					curUser.busy();
					
					if (curWand.cursed){
						if (!curWand.cursedKnown){
							GLog.n(Messages.get(Wand.class, "curse_discover", curWand.name()));
						}
						if(curWand.solidified) CursedWand.confirmSolid(((Wand) curItem).solidrarity, ((Wand) curItem).solidtype);
						else CursedWand.logoutSolid();
						CursedWand.cursedZap(curWand,
								curUser,
								new Ballistica(curUser.pos, target, Ballistica.MAGIC_BOLT),
								new Callback() {
									@Override
									public void call() {
										curWand.wandUsed();
									}
								});
					} else {
						curWand.fx(shot, new Callback() {
							public void call() {
								curWand.onZap(shot);
								curWand.wandUsed();
							}
						});
					}
					curWand.cursedKnown = true;
					
				}
				
			}
		}
		
		@Override
		public String prompt() {
			return Messages.get(Wand.class, "prompt");
		}
	};
	
	public class Charger extends Buff {
		
		private static final float BASE_CHARGE_DELAY = 10f;
		private static final float SCALING_CHARGE_ADDITION = 40f;
		private static final float NORMAL_SCALE_FACTOR = 0.875f;

		private static final float CHARGE_BUFF_BONUS = 0.25f;

		float scalingFactor = NORMAL_SCALE_FACTOR;
		
		@Override
		public boolean attachTo( Char target ) {
			super.attachTo( target );
			
			return true;
		}
		
		@Override
		public boolean act() {
			if (curCharges < maxCharges)
				recharge();
			
			while (partialCharge >= 1 && curCharges < maxCharges) {
				partialCharge--;
				curCharges++;
				updateQuickslot();
			}
			
			if (curCharges == maxCharges){
				partialCharge = 0;
			}
			
			spend( TICK );
			
			return true;
		}

		private void recharge(){
			int missingCharges = maxCharges - curCharges;
			missingCharges = Math.max(0, missingCharges);

			float turnsToCharge = (float) (BASE_CHARGE_DELAY
					+ (SCALING_CHARGE_ADDITION * Math.pow(scalingFactor, missingCharges)));

			LockedFloor lock = target.buff(LockedFloor.class);
			if (lock == null || lock.regenOn())
				partialCharge += (1f/turnsToCharge) * RingOfEnergy.wandChargeMultiplier(target)*LFPChargeMultiplier();

			for (Recharging bonus : target.buffs(Recharging.class)){
				if (bonus != null && bonus.remainder() > 0f) {
					partialCharge += CHARGE_BUFF_BONUS * bonus.remainder();
				}
			}
		}
		
		public Wand wand(){
			return Wand.this;
		}

		public void gainCharge(float charge){
			if (curCharges < maxCharges) {
				partialCharge += charge;
				while (partialCharge >= 1f) {
					curCharges++;
					partialCharge--;
				}
				curCharges = Math.min(curCharges, maxCharges);
				updateQuickslot();
			}
		}

		private void setScaleFactor(float value){
			this.scalingFactor = value;
		}
	}


	public static class PlaceHolder extends Wand {

		{
			image = ItemSpriteSheet.WAND_HOLDER;
		}

		@Override
		public boolean isSimilar(Item item) {
			return item instanceof Wand;
		}

		@Override
		protected void onZap(Ballistica attack) {
		}

		@Override
		public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {

		}

		@Override
		public String info() {
			return "";
		}
	}
}
