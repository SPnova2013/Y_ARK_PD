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
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SkillBook;
import com.shatteredpixel.shatteredpixeldungeon.items.StaffKit;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SP.StaffOfAbsinthe;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SP.StaffOfVigna;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorrosion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorruption;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUseItem;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class MagesStaff extends MeleeWeapon {

	private Wand wand;

	public static final String AC_IMBUE = "IMBUE";
	public static final String AC_ZAP	= "ZAP";

	private static final float STAFF_SCALE_FACTOR = 0.75f;

	//mental burst: needs a fully charged staff, splits every cast into weaker bolts
	public static final int ABILITY_HITS_BASE = 6;
	//one extra bolt at this level and again at twice this level
	public static final int ABILITY_HITS_BONUS_LEVEL = 5;
	//every bolt keeps this much of the original power, plus a little per level
	public static final float ABILITY_POWER_BASE = 0.33f;
	public static final float ABILITY_POWER_PER_LEVEL = 0.01f;
	//the burst lasts ABILITY_DURATION_BASE + weaponLevel/2 turns
	public static final int ABILITY_DURATION_BASE = 10;
	//and the staff cannot burst again for ABILITY_COOLDOWN_BASE - weaponLevel turns
	public static final int ABILITY_COOLDOWN_BASE = 66;
	//paralysis the hero suffers once the burst is over
	public static final float ABILITY_PARALYSIS = 5f;

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		if (wand == null) {
			GLog.w(Messages.get(this, "ability_no_wand"));
			return;
		}
		if (hero.buff(MentalBurstCooldown.class) != null) {
			GLog.w(Messages.get(this, "ability_cooling"));
			return;
		}

		hero.belongings.abilityWeapon = this;
		beforeAbilityUsed(hero, null);

		int lvl = buffedLvl();
		int hits = ABILITY_HITS_BASE
				+ (lvl >= ABILITY_HITS_BONUS_LEVEL ? 1 : 0)
				+ (lvl >= ABILITY_HITS_BONUS_LEVEL * 2 ? 1 : 0);
		float power = ABILITY_POWER_BASE + ABILITY_POWER_PER_LEVEL * lvl;

		Buff.prolong(hero, MentalBurst.class, ABILITY_DURATION_BASE + lvl/2).setup(hits, power);
		Buff.affect(hero, MentalBurstCooldown.class).setDuration(Math.max(1, ABILITY_COOLDOWN_BASE - lvl));

		hero.sprite.operate(hero.pos);
		Sample.INSTANCE.play(Assets.Sounds.SKILL_BASIC);
		Invisibility.dispel();

		afterAbilityUsed(hero);
		hero.spendAndNext(Actor.TICK);
	}

	@Override
	public String abilityInfo() {
		int lvl = buffedLvl();
		int hits = ABILITY_HITS_BASE
				+ (lvl >= ABILITY_HITS_BONUS_LEVEL ? 1 : 0)
				+ (lvl >= ABILITY_HITS_BONUS_LEVEL * 2 ? 1 : 0);
		int power = Math.round((ABILITY_POWER_BASE + ABILITY_POWER_PER_LEVEL * lvl) * 100f);
		return Messages.get(this, "ability_desc",
				ABILITY_DURATION_BASE + lvl/2,             //%1$d duration in turns
				hits,                                       //%2$d bolts per cast
				power,                                      //%3$d percent each bolt keeps
				Math.max(1, ABILITY_COOLDOWN_BASE - lvl));  //%4$d cooldown in turns
	}

	//every cast is split into this many weaker bolts while the burst lasts
	public static class MentalBurst extends FlavourBuff {

		private int hits = ABILITY_HITS_BASE;
		private float power = ABILITY_POWER_BASE;

		{
			type = buffType.POSITIVE;
			announced = true;
		}

		public void setup(int hits, float power) {
			this.hits = hits;
			this.power = power;
		}

		public int hits() {
			return hits;
		}

		public float power() {
			return power;
		}

		@Override
		public void detach() {
			super.detach();
			if (target != null) {
				//the mind pays for the outburst
				Buff.affect(target, Paralysis.class, ABILITY_PARALYSIS);
			}
		}

		@Override
		public int icon() {
			return BuffIndicator.MAGIC_SLEEP;
		}

		private static final String HITS = "hits";
		private static final String POWER = "power";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(HITS, hits);
			bundle.put(POWER, power);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			hits = bundle.getInt(HITS);
			power = bundle.getFloat(POWER);
		}
	}

	//time the staff needs before it can burst again.
	//a plain Buff rather than a FlavourBuff, so it ticks each turn and keeps the slot text live
	public static class MentalBurstCooldown extends Buff {

		{
			type = buffType.NEGATIVE;
			announced = false;
		}

		private float left = 0f;

		public void setDuration(float duration) {
			left = duration;
		}

		public float remaining() {
			return left;
		}

		@Override
		public boolean act() {
			left -= TICK;

			if (left <= 0f) {
				detach();
			} else {
				spend(TICK);
			}

			//nudge the quickslot afterwards, so the slot text shows the new value and
			//clears on the very turn the cooldown runs out
			Item.updateQuickslot();
			return true;
		}

		@Override
		public int icon() {
			return BuffIndicator.COOL_TIME;
		}

		@Override
		public String toString() {
			return Messages.get(this, "name");
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", dispTurns(left));
		}

		private static final String LEFT = "left";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(LEFT, left);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			left = bundle.getFloat(LEFT);
		}
	}

	{
		image = ItemSpriteSheet.MAGES_STAFF;
		hitSound = Assets.Sounds.HIT_MAGIC;
		hitSoundPitch = 1.1f;

		tier = 1;

		defaultAction = AC_ZAP;
		usesTargeting = true;

		unique = true;
		bones = false;
	}

	public MagesStaff() {
		wand = null;
	}

	@Override
	public int max(int lvl) {
		if (Dungeon.hero.subClassSet.contains(HeroSubClass.WARLOCK))
			return  3*(tier+1) +    // 6 + 1
					lvl*(tier);   //scaling unaffected
		else
			return  4*(tier+1) +    //8 + 2
				lvl*(tier+1);   //scaling unaffected
	}

	public MagesStaff(Wand wand){
		this();
		wand.identify();
		wand.cursed = false;
		this.wand = wand;
		updateWand(false);
		wand.curCharges = wand.maxCharges;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions( hero );
		actions.add(AC_IMBUE);
		if (wand!= null && wand.curCharges > 0) {
			actions.add( AC_ZAP );
		}
		return actions;
	}

	@Override
	public void activate( Char ch ) {
		if(wand != null) wand.charge( ch, STAFF_SCALE_FACTOR );
	}

	@Override
	public String statusBottom() {

		//if the weapon isn't IDed, or is cursed, don't display anything
		if (!isIdentified() || cursed) {
			return null;
		}
		if (wand == null || Dungeon.hero == null) {
			return null;
		}

		//while cooling down show the remaining turns, otherwise show that the burst is ready
		MentalBurstCooldown cooldown = Dungeon.hero.buff(MentalBurstCooldown.class);
		if (cooldown != null) {
			return Integer.toString(Math.max(1, Math.round(cooldown.remaining())));
		}
		return "OK";
	}

	@Override
	public void execute(Hero hero, String action) {

		super.execute(hero, action);

		if (action.equals(AC_IMBUE)) {

			curUser = hero;
			GameScene.selectItem(itemSelector, WndBag.Mode.WAND, Messages.get(this, "prompt"));

		} else if (action.equals(AC_ZAP)){

			if (wand == null) {
				GameScene.show(new WndUseItem(null, this));
				return;
			}

			if (cursed || hasCurseEnchant()) wand.cursed = true;
			else                             wand.cursed = false;
			wand.execute(hero, AC_ZAP);
		}
	}

	@Override
	public String defaultAction() {
		return AC_ZAP;
	}
	@Override
	public int buffedLvl() {
		if (wand != null){
			return Math.max(super.buffedLvl(), wand.buffedLvl());
		} else {
			return super.buffedLvl();
		}
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (attacker.buff(Talent.EmpoweredStrikeTracker.class) != null){
			attacker.buff(Talent.EmpoweredStrikeTracker.class).detach();
			damage = Math.round( damage * (1f + Dungeon.hero.pointsInTalent(Talent.EMPOWERED_STRIKE)/5f));
		}

		if (Dungeon.hero.hasTalent(Talent.AZURE_FURY))
		{
			if (Random.Int(20) < Dungeon.hero.pointsInTalent(Talent.AZURE_FURY))//change from budding
			{
				if (Dungeon.hero.belongings.getItem(SkillBook.class) != null) {
					SkillBook Item = Dungeon.hero.belongings.getItem(SkillBook.class);
					Item.SetCharge(2);
				}
			}
		}

		if (attacker instanceof Hero && Random.Int(8) < ((Hero) attacker).pointsInTalent(Talent.EXCESS_CHARGE)){
			Buff.affect(attacker, Barrier.class).setShield(wand.curCharges*2);
		}

		if (attacker instanceof Hero && ((Hero) attacker).hasTalent(Talent.MYSTICAL_CHARGE)){
			Hero hero = (Hero) attacker;
			for (Buff b : hero.buffs()){
				if (b instanceof Artifact.ArtifactBuff) ((Artifact.ArtifactBuff) b).charge(hero, hero.pointsInTalent(Talent.MYSTICAL_CHARGE)/3f);
			}
		}

		if (wand != null &&
				attacker instanceof Hero && ((Hero)attacker).subClassSet.contains(HeroSubClass.BATTLEMAGE)) {
			if (wand.curCharges < wand.maxCharges) {
				float charged = 0.4f;
				if (((Hero) attacker).hasTalent(Talent.SWORDOFLORD)) {
					charged += 0.04f+(((Hero) attacker).pointsInTalent(Talent.SWORDOFLORD) * 0.08f);
				}
				wand.partialCharge += charged;
			}
			ScrollOfRecharging.charge((Hero)attacker);
			wand.onHit(this, attacker, defender, damage);
		}
		return super.proc(attacker, defender, damage);
	}

	@Override
	public int reachFactor(Char owner) {
		int reach = super.reachFactor(owner);
		if (owner instanceof Hero
				&& wand instanceof WandOfDisintegration
				&& ((Hero)owner).subClassSet.contains(HeroSubClass.BATTLEMAGE)){
			reach++;
		}
		else if (owner instanceof Hero
				&& wand instanceof StaffOfVigna
				&& ((Hero)owner).subClassSet.contains(HeroSubClass.BATTLEMAGE)){
			reach++;
		}
		return reach;
	}

	@Override
	public boolean collect( Bag container ) {
		if (super.collect(container)) {
			if (container.owner != null && wand != null) {
				wand.charge(container.owner, STAFF_SCALE_FACTOR);
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onDetach( ) {
		if (wand != null) wand.stopCharging();
	}

	public Item imbueWand(Wand wand, Char owner){

		int oldStaffcharges = this.wand.curCharges;

		if (owner == Dungeon.hero && Dungeon.hero.hasTalent(Talent.WAND_PRESERVATION)
				&& Random.Float() < 0.34f + 0.33f*Dungeon.hero.pointsInTalent(Talent.WAND_PRESERVATION)){

			Talent.WandPreservationCounter counter = Buff.affect(Dungeon.hero, Talent.WandPreservationCounter.class);
			if (counter.count() < 3) {
				counter.countUp(1);
				this.wand.level(0);
				if (!this.wand.collect()) {
					Dungeon.level.drop(this.wand, owner.pos);
				}
				GLog.newLine();
				GLog.p(Messages.get(this, "preserved"));
			}
		}

		this.wand = null;

		//syncs the level of the two items.
		int targetLevel = Math.max(this.level() - (curseInfusionBonus ? 1 : 0), wand.level());

		//if the staff's level is being overridden by the wand, preserve 1 upgrade
		if (wand.level() >= this.level() && this.level() > (curseInfusionBonus ? 1 : 0)) targetLevel++;
		
		level(targetLevel);
		this.wand = wand;
		updateWand(false);
		wand.curCharges = Math.min(wand.maxCharges, wand.curCharges+oldStaffcharges);
		if (owner != null) wand.charge(owner);

		//This is necessary to reset any particles.
		//FIXME this is gross, should implement a better way to fully reset quickslot visuals
		int slot = Dungeon.quickslot.getSlot(this);
		if (slot != -1){
			Dungeon.quickslot.clearSlot(slot);
			updateQuickslot();
			Dungeon.quickslot.setSlot( slot, this );
			updateQuickslot();
		}
		
		Badges.validateItemLevelAquired(this);

		return this;
	}

	public Item imbueWand_kit(Wand wand, Char owner){

		int oldStaffcharges = this.wand.curCharges;

		this.wand = null;

		//syncs the level of the two items.
		int targetLevel = Math.max(this.level() - (curseInfusionBonus ? 1 : 0), wand.level());

		//if the staff's level is being overridden by the wand, preserve 1 upgrade
		if (wand.level() >= this.level() && this.level() > (curseInfusionBonus ? 1 : 0)) targetLevel++;

		level(targetLevel);
		this.wand = wand;
		updateWand(false);
		wand.curCharges = Math.min(wand.maxCharges, wand.curCharges+oldStaffcharges);
		if (owner != null) wand.charge(owner);

		//This is necessary to reset any particles.
		//FIXME this is gross, should implement a better way to fully reset quickslot visuals
		int slot = Dungeon.quickslot.getSlot(this);
		if (slot != -1){
			Dungeon.quickslot.clearSlot(slot);
			updateQuickslot();
			Dungeon.quickslot.setSlot( slot, this );
			updateQuickslot();
		}

		Badges.validateItemLevelAquired(this);

		return this;
	}

	public void gainCharge( float amt ){
		gainCharge(amt, false);
	}

	public void gainCharge( float amt, boolean overcharge ){
		if (wand != null){
			wand.gainCharge(amt, overcharge);
		}
	}

	public Class<?extends Wand> wandClass(){
		return wand != null ? wand.getClass() : null;
	}

	public Wand GetWand()
	{
		return wand;
	}

	@Override
	public Item upgrade(boolean enchant) {
		super.upgrade( enchant );

		updateWand(true);

		return this;
	}

	@Override
	public Item degrade() {
		super.degrade();

		updateWand(false);

		return this;
	}
	
	public void updateWand(boolean levelled){
		if (wand != null) {
			int Bounscharge = 0;
			if (Dungeon.hero.hasTalent(Talent.LIBERATION)) {
				Bounscharge = Dungeon.hero.pointsInTalent(Talent.LIBERATION);
			}
			int curCharges = wand.curCharges;
			wand.level(level());
			//gives the wand one additional max charge
			wand.maxCharges = Math.min(wand.maxCharges + 1 + Bounscharge, 10 + Bounscharge);
			wand.curCharges = Math.min(curCharges + (levelled ? 1 : 0), wand.maxCharges);
			updateQuickslot();
		}
	}

	@Override
	public String status() {
		if (wand == null) return super.status();
		else return wand.status();
	}

	@Override
	public String name() {
		if (wand == null) {
			return super.name();
		} else {
			String name = Messages.get(wand, "staff_name");
			return enchantment != null && (cursedKnown || !enchantment.curse()) ? enchantment.name( name ) : name;
		}
	}

	@Override
	public String info() {
		String info = super.info();

		if (wand != null){
			info += "\n\n" + Messages.get(this, "has_wand", Messages.get(wand, "name"));
			if (!cursed || !cursedKnown)    info += " " + wand.statsDesc();
			else                            info += " " + Messages.get(this, "cursed_wand");

			if (Dungeon.hero.subClassSet.contains(HeroSubClass.BATTLEMAGE)){
				info += "\n\n" + Messages.get(wand, "bmage_desc");
			}
		}

		return info;
	}

	@Override
	public Emitter emitter() {
		if (wand == null) return null;
		Emitter emitter = new Emitter();
		emitter.pos(12.5f, 3);
		emitter.fillTarget = false;
		return emitter;
	}

	private static final String WAND = "wand";
	private static final String MAXCHARGE = "charge";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(WAND, wand);
		bundle.put(MAXCHARGE, wand.maxCharges);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		wand = (Wand) bundle.get(WAND);
		if (wand != null) {
			wand.maxCharges = bundle.getInt(MAXCHARGE);
		}
	}

	@Override
	public int value() {
		return 0;
	}
	
	@Override
	public Weapon enchant(Enchantment ench) {
		if (curseInfusionBonus && (ench == null || !ench.curse())){
			curseInfusionBonus = false;
			updateWand(false);
		}
		return super.enchant(ench);
	}
	
	private final WndBag.Listener itemSelector = new WndBag.Listener() {
		@Override
		public void onSelect( final Item item ) {
			if (item != null) {

				if (!item.isIdentified()) {
					GLog.w(Messages.get(MagesStaff.class, "id_first"));
					return;
				} else if (item.cursed){
					GLog.w(Messages.get(MagesStaff.class, "cursed"));
					return;
				}

				if (wand == null){
					applyWand((Wand)item);
				} else {
					int newLevel;
					if (item.level() >= level()){
						if (level() > 0)    newLevel = item.level() + 1;
						else                newLevel = item.level();
					} else {
						newLevel = level();
					}

					String bodyText = Messages.get(MagesStaff.class, "imbue_desc", newLevel);
					int preservesLeft = Dungeon.hero.hasTalent(Talent.WAND_PRESERVATION) ? 3 : 0;
					if (Dungeon.hero.buff(Talent.WandPreservationCounter.class) != null){
						preservesLeft -= Dungeon.hero.buff(Talent.WandPreservationCounter.class).count();
					}
					if (preservesLeft > 0){
						int preserveChance = Dungeon.hero.pointsInTalent(Talent.WAND_PRESERVATION) == 1 ? 67 : 100;
						bodyText += "\n\n" + Messages.get(MagesStaff.class, "imbue_talent", preserveChance, preservesLeft);
					} else {
						bodyText += "\n\n" + Messages.get(MagesStaff.class, "imbue_lost");
					}

					GameScene.show(
							new WndOptions("",
									bodyText,
									Messages.get(MagesStaff.class, "yes"),
									Messages.get(MagesStaff.class, "no")) {
								@Override
								protected void onSelect(int index) {
									if (index == 0) {
										applyWand((Wand)item);
									}
								}
							}
					);
				}
			}
		}

		private void applyWand(Wand wand){
			Sample.INSTANCE.play(Assets.Sounds.BURNING);
			curUser.sprite.emitter().burst( ElmoParticle.FACTORY, 12 );
			evoke(curUser);

			Dungeon.quickslot.clearItem(wand);

			wand.detach(curUser.belongings.backpack);

			GLog.p( Messages.get(MagesStaff.class, "imbue", wand.name()));
			imbueWand( wand, curUser );

			updateQuickslot();
		}
	};

	public void staffkitwand(Wand wand){
		Sample.INSTANCE.play(Assets.Sounds.BURNING);
		curUser.sprite.emitter().burst( ElmoParticle.FACTORY, 12 );
		evoke(curUser);
		Dungeon.quickslot.clearItem(wand);
		imbueWand_kit( wand, curUser );

		wand.identify();

		updateQuickslot();
	}

	public void upToStaff(Class c) {
		if(c != null){
			Wand n = (Wand) Reflection.newInstance( c ); // 이 부분이랑 조건만 바꾸면 됨.
			n.level(0);
			n.levelKnown = wand.levelKnown;
			n.cursedKnown = wand.cursedKnown;
			n.cursed = wand.cursed;
			n.curseInfusionBonus = wand.curseInfusionBonus;

			wand = n;
			staffkitwand(wand);
		}else{
			GLog.w( Messages.get(StaffKit.class, "fail") );
			StaffKit S = new StaffKit();
			S.quantity(1).collect();
		}
	}
}
