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

package com.shatteredpixel.shatteredpixeldungeon.actors;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPChallenges;
import com.shatteredpixel.shatteredpixeldungeon.TomorrowRogueNight;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArcaneArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionHero;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corrosion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DangerDanceBonus;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dream;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ExecutMode;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FireImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hallucination;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HuntingMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LanceCharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LifeLink;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MechanicalSight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Oblivion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PermanentArmorReduction;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PotatoAimReady;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Preparation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ROR2Shield;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.RabbitTime;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SeethingBurst;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SnipersMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Speed;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Stamina;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Dummy;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK3.ExtremeSharpness;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK3.TrueSilverSlash;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.LeatherArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Potential;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfDragonsBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfElements;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfTenacity;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.LuckyLeaf;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.ROR2item;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.StunGrenade;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.WakeOfVultures;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfSacrifice;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SP.StaffOfGreyy;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SP.StaffOfLeaf;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SP.StaffOfSkyfire;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFireblast;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.chimera.Song;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.chimera.Sylvestris;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.chimera.Thermit;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blazing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Shocking;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.AssassinsBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.FlametailSword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.FolkSong;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.KRISSVector;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.KollamSword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.ThermiteBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Naginata;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RhodesSword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Violin;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WarJournalist;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.ShockingDart;
import com.shatteredpixel.shatteredpixeldungeon.items.testtool.ImmortalShield;//change from budding
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrimTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.Arrays;
import java.util.HashSet;

public abstract class Char extends Actor {
	
	public int pos = 0;
	
	public CharSprite sprite;
	
	public int HT;
	public int HP;
	
	protected float baseSpeed	= 1;
	protected PathFinder.Path path;

	public int paralysed	    = 0;
	public boolean rooted		= false;
	public boolean flying		= false;
	public int invisible		= 0;
	
	//these are relative to the hero
	public enum Alignment{
		ENEMY,
		NEUTRAL,
		ALLY
	}
	public Alignment alignment;
	
	public int viewDistance	= 8;
	
	public boolean[] fieldOfView = null;
	
	private HashSet<Buff> buffs = new HashSet<>();
	
	@Override
	protected boolean act() {
		if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()){
			fieldOfView = new boolean[Dungeon.level.length()];
		}
		Dungeon.level.updateFieldOfView( this, fieldOfView );

		//throw any items that are on top of an immovable char
		if (properties().contains(Property.IMMOVABLE)){
			throwItems();
		}
		return false;
	}

	protected void throwItems(){
		Heap heap = Dungeon.level.heaps.get( pos );
		if (heap != null && heap.type == Heap.Type.HEAP) {
			int n;
			do {
				n = pos + PathFinder.NEIGHBOURS8[Random.Int( 8 )];
			} while (!Dungeon.level.passable[n] && !Dungeon.level.avoid[n]);
			Dungeon.level.drop( heap.pickUp(), n ).sprite.drop( pos );
		}
	}

	public String name(){
		return Messages.get(this, "name");
	}

	public boolean canInteract(Char c){
		if (Dungeon.level.adjacent( pos, c.pos )){
			return true;
		} else if (c instanceof Hero
				&& alignment == Alignment.ALLY
				&& Dungeon.level.distance(pos, c.pos) <= 2*Dungeon.hero.pointsInTalent(Talent.ALLY_WARP)){
			return true;
		} else {
			return false;
		}
	}
	
	//swaps places by default
	public boolean interact(Char c){

		//can't spawn places if one char has restricted movement
		if (rooted || c.rooted || buff(Vertigo.class) != null || c.buff(Vertigo.class) != null){
			return true;
		}

		//don't allow char to swap onto hazard unless they're flying
		//you can swap onto a hazard though, as you're not the one instigating the swap
		if (!Dungeon.level.passable[pos] && !c.flying){
			return true;
		}

		//can't swap into a space without room
		if (properties().contains(Property.LARGE) && !Dungeon.level.openSpace[c.pos]
			|| c.properties().contains(Property.LARGE) && !Dungeon.level.openSpace[pos]){
			return true;
		}

		int curPos = pos;

		//warp instantly with allies in this case
		if (Dungeon.hero.hasTalent(Talent.ALLY_WARP)){
			PathFinder.buildDistanceMap(c.pos, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));
			if (PathFinder.distance[pos] == Integer.MAX_VALUE){
				return true;
			}
			ScrollOfTeleportation.appear(this, Dungeon.hero.pos);
			ScrollOfTeleportation.appear(Dungeon.hero, curPos);
			Dungeon.observe();
			GameScene.updateFog();
			return true;
		}
		
		moveSprite( pos, Dungeon.hero.pos );
		move( Dungeon.hero.pos );
		
		Dungeon.hero.sprite.move( Dungeon.hero.pos, curPos );
		Dungeon.hero.move( curPos );
		
		Dungeon.hero.spend( 1 / Dungeon.hero.speed() );
		Dungeon.hero.busy();
		
		return true;
	}
	
	protected boolean moveSprite( int from, int to ) {
		
		if (sprite.isVisible() && (Dungeon.level.heroFOV[from] || Dungeon.level.heroFOV[to])) {
			sprite.move( from, to );
			return true;
		} else {
			sprite.turnTo(from, to);
			sprite.place( to );
			return true;
		}
	}

	public void hitSound( float pitch ){
		Sample.INSTANCE.play(Assets.Sounds.HIT, 1, pitch);
	}

	public boolean blockSound( float pitch ) {
		return false;
	}
	
	protected static final String POS       = "pos";
	protected static final String TAG_HP    = "HP";
	protected static final String TAG_HT    = "HT";
	protected static final String TAG_SHLD  = "SHLD";
	protected static final String BUFFS	    = "buffs";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		
		super.storeInBundle( bundle );
		
		bundle.put( POS, pos );
		bundle.put( TAG_HP, HP );
		bundle.put( TAG_HT, HT );
		bundle.put( BUFFS, buffs );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		
		super.restoreFromBundle( bundle );
		
		pos = bundle.getInt( POS );
		HP = bundle.getInt( TAG_HP );
		HT = bundle.getInt( TAG_HT );
		
		for (Bundlable b : bundle.getCollection( BUFFS )) {
			if (b != null) {
				((Buff)b).attachTo( this );
			}
		}
	}
	
	public boolean attack( Char enemy ) {

		if (enemy == null) return false;
		
		boolean visibleFight = Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[enemy.pos];

		if (enemy.isInvulnerable(getClass())) {

			if (visibleFight) {
				enemy.sprite.showStatus( CharSprite.POSITIVE, Messages.get(this, "invulnerable") );

				Sample.INSTANCE.play(Assets.Sounds.HIT_PARRY, 1f, Random.Float(0.96f, 1.05f));
			}

			return false;

		} else if (hit( this, enemy, false ))
		{
			
			int dr = enemy.drRoll();
			
			if (this instanceof Hero){
				Hero h = (Hero)this;
				if (h.belongings.weapon instanceof MissileWeapon
						&& h.subClassSet.contains(HeroSubClass.SNIPER)
						&& !Dungeon.level.adjacent(h.pos, enemy.pos)){
					dr = 0; }

				SpiritBow weapon = h.belongings.getItem(SpiritBow.class);
					if (weapon != null && h.belongings.weapon instanceof SpiritBow.SpiritArrow
							&& h.subClassSet.contains(HeroSubClass.WARDEN)) {
						if (weapon.EatSeed >= 15) dr/=2;
					}
				if (h.belongings.weapon instanceof ThermiteBlade) dr = 0;
				if (h.belongings.weapon instanceof RhodesSword) dr = 0;
				if (h.belongings.weapon instanceof KollamSword) dr = 0;
				if (h.belongings.weapon!=null && ((Weapon)h.belongings.weapon).hasChimera(Thermit.class)) dr = 0;

				if (h.belongings.getItem(RingOfTenacity.class) != null) {
					if (h.belongings.getItem(RingOfTenacity.class).isEquipped(Dungeon.hero) &&
							(h.belongings.weapon instanceof FolkSong || (h.belongings.weapon!=null && ((Weapon)h.belongings.weapon).hasChimera(Song.class)))
					) {
						dr /= 2;
					}
				}
				if (KRISSVector.VectorSetBouns()) dr/=2;

				if(h.buff(ExtremeSharpness.SharpnessBuff.class) != null) {
					dr = 0;
				}

				if (h.hasTalent(Talent.ZANTETSUKEN)) {
					if (Random.Int(10) < h.pointsInTalent(Talent.ZANTETSUKEN)) dr = 0;
				}
			}
			PermanentArmorReduction par = enemy.buff(PermanentArmorReduction.class);
			if(par!=null) dr -= par.get();

			int dmg;
			Preparation prep = buff(Preparation.class);
			if (prep != null){
				dmg = prep.damageRoll(this);
				if (this == Dungeon.hero && Dungeon.hero.hasTalent(Talent.BOUNTY_HUNTER)) {
					Buff.affect(Dungeon.hero, Talent.BountyHunterTracker.class, 0.0f);
				}
				if (this == Dungeon.hero && Dungeon.hero.hasTalent(Talent.SWEEP)) {
						Buff.affect(Dungeon.hero, Talent.SWEEPTraker.class, 0.0f); }
			} else {
				dmg = damageRoll();
			}
			
			int effectiveDamage = enemy.defenseProc( this, dmg );
			effectiveDamage = Math.max( effectiveDamage - dr, 0 );
			
			if ( enemy.buff( Vulnerable.class ) != null){
				effectiveDamage *= 1.33f;
			}
			
			effectiveDamage = attackProc( enemy, effectiveDamage );
			
			if (visibleFight) {
				if (effectiveDamage > 0 || !enemy.blockSound(Random.Float(0.96f, 1.05f))) {
					hitSound(Random.Float(0.87f, 1.15f));
				}
			}

			// If the enemy is already dead, interrupt the attack.
			// This matters as defence procs can sometimes inflict self-damage, such as armor glyphs.
			if (!enemy.isAlive()){
				return true;
			}

			enemy.damage( effectiveDamage, this );
			if(Dungeon.level.feeling == Level.Feeling.HEAVYFOG){
				if(this instanceof Hero) this.HP = Math.min(this.HP+(int)Math.ceil(effectiveDamage/20f), this.HT);
				else this.HP = Math.min(this.HP+effectiveDamage/2, this.HT);
			}

			if(buff(StunGrenade.StunGrenadeBuff.class)!=null) {
				if(Random.Int(5)==0){
					CellEmitter.get(enemy.pos).start(Speck.factory(Speck.LIGHT), 0.02f, 9);
					Buff.affect( enemy, Paralysis.class, 2f );
					Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );
				}
			}
			if (buff(FireImbue.class) != null)  buff(FireImbue.class).proc(enemy);
			if (buff(Hallucination.class) != null)  buff(Hallucination.class).proc();
			if (buff(ElixirOfDragonsBlood.Dragonsblood.class) != null)  buff(ElixirOfDragonsBlood.Dragonsblood.class).proc(this, enemy);
			if (buff(FrostImbue.class) != null) buff(FrostImbue.class).proc(enemy);

			if (enemy.isAlive() && prep != null && prep.canKO(enemy)){
				enemy.HP = 0;
				if (!enemy.isAlive()) {
					enemy.die(this);
				} else {
					//helps with triggering any on-damage effects that need to activate
					enemy.damage(-1, this);
				}
				enemy.sprite.showStatus(CharSprite.NEGATIVE, Messages.get(Preparation.class, "assassinated"));
			}

			enemy.sprite.bloodBurstA( sprite.center(), effectiveDamage );
			enemy.sprite.flash();

			if (!enemy.isAlive()) {
				if (this instanceof Hero) {
					Hero h = (Hero) this;
					if ((h.belongings.weapon instanceof RhodesSword)) {
						new FlavourBuff(){
							{actPriority = VFX_PRIO;}
							public boolean act() {
								Buff.affect( Dungeon.hero, Invisibility.class ,5f);
								return super.act();
							}
						}.attachTo(this);
					}
					if(h.belongings.misc instanceof ROR2item){
						ROR2item r2i = (ROR2item)h.belongings.misc;
						r2i.uponKill(this, enemy, effectiveDamage);
					}
				}
			}

			if(Dungeon.hero.subClassSet.contains(HeroSubClass.KILLER) && Dungeon.hero.hasTalent(Talent.DANGER_DANCE)){
				if(this instanceof Hero){
					Buff.affect(this, DangerDanceBonus.class).add(4-Dungeon.hero.pointsInTalent(Talent.DANGER_DANCE));
				}else if(enemy instanceof Hero){
					Buff.affect(enemy,DangerDanceBonus.class).decrease(Dungeon.hero.pointsInTalent(Talent.DANGER_DANCE));
				}
			}
			if(!enemy.isAlive()){
				if(this instanceof Hero){
						new FlavourBuff(){
							{actPriority = VFX_PRIO;}
							public boolean act() {
								if (target instanceof Hero && ((Hero) target).subClassSet.contains(HeroSubClass.KILLER)){
									Buff.affect(target, RabbitTime.class).add(0.5f);
									if(Dungeon.hero.hasTalent(Talent.LINGER_ON)){
										Buff.affect(target, RabbitTime.class).add(Dungeon.hero.pointsInTalent(Talent.LINGER_ON)*0.5f);
									}
								}
								return super.act();
							}
						}.attachTo(this);
				}
			}

			if (!enemy.isAlive() && visibleFight) {
				if (enemy == Dungeon.hero) {
					
					if (this == Dungeon.hero) {
						return true;
					}

					Dungeon.fail( getClass() );
					GLog.n( Messages.capitalize(Messages.get(Char.class, "kill", name())) );
					
				} else if (this == Dungeon.hero) {
					GLog.i( Messages.capitalize(Messages.get(Char.class, "defeat", enemy.name())) );
				}
			}

			if (enemy.isAlive() && enemy.HP < enemy.HT * 0.15f &&
			enemy != Dungeon.hero && Dungeon.hero.belongings.weapon instanceof Naginata && this instanceof Hero &&
				!enemy.properties().contains(Char.Property.BOSS) && !enemy.properties().contains(Char.Property.MINIBOSS)) {
					sprite.showStatus(CharSprite.NEUTRAL, Messages.get(Naginata.class, "skill"));
					enemy.damage(108108, new Naginata.naginataSkill());
					SpellSprite.show(enemy, SpellSprite.FOOD);

			}

			if(this instanceof Hero){
				if(Dungeon.hero.buff(PotatoAimReady.class)!=null && Dungeon.hero.buff(PotatoAimReady.class).isReady()){
					PotatoAimReady.PotatoKill(enemy);
				}
			}
			
			return true;
			
		} else {
			
			if (visibleFight) {
				String defense = enemy.defenseVerb();
				enemy.sprite.showStatus( CharSprite.NEUTRAL, defense );

				//TODO enemy.defenseSound? currently miss plays for monks/crab even when they parry
				Sample.INSTANCE.play(Assets.Sounds.MISS);
			}

			if (enemy instanceof Hero) {
				if (Dungeon.hero.belongings.weapon instanceof FlametailSword ||
						(Dungeon.hero.belongings.weapon!=null && ((Weapon)Dungeon.hero.belongings.weapon).hasChimera(Sylvestris.class))) {
					enemy.sprite.centerEmitter().start( Speck.factory( Speck.LIGHT ), 0.1f, 3 );
					Buff.affect(enemy, FlametailSword.FlametaillBuff.class);
				}
			}

			if(Dungeon.hero.subClassSet.contains(HeroSubClass.KILLER)){
				if(enemy instanceof Hero) {
					Buff.affect(enemy, RabbitTime.class).add(0.2f + Dungeon.hero.pointsInTalent(Talent.LINGER_ON) * 0.1f);
				}
				if(Dungeon.hero.hasTalent(Talent.DANGER_DANCE)) {
					if (enemy instanceof Hero) {
						Buff.affect(enemy, DangerDanceBonus.class).add(4 - Dungeon.hero.pointsInTalent(Talent.DANGER_DANCE));
					} else if (this instanceof Hero) {
						Buff.affect(this, DangerDanceBonus.class).decrease(Dungeon.hero.pointsInTalent(Talent.DANGER_DANCE));
					}
				}
			}
			
			return false;
			
		}
	}

	public static final int INFINITE_ACCURACY = 1_000_000;
	public static final int INFINITE_EVASION = 1_000_000;

	public static boolean hit( Char attacker, Char defender, boolean magic ) {
		float acuStat = attacker.attackSkill(defender);
		float defStat = defender.defenseSkill(attacker);

		//if accuracy or evasion are large enough, treat them as infinite.
		//note that infinite evasion beats infinite accuracy
		if (defStat >= INFINITE_EVASION) {
			return false;
		} else if (acuStat >= INFINITE_ACCURACY) {
			return true;
		}

		if (attacker instanceof Hero) {
			if (Dungeon.hero.pointsInTalent(Talent.SIMPLE_COMBO) == 2) {
				if (Dungeon.hero.buff(Violin.InstantViolin.class) != null) {
					return true;
				}
			}
			if (Dungeon.hero.hasTalent(Talent.ACCURATE_HIT)) {
				if (Dungeon.hero.buff(RabbitTime.class) != null) {
					return true;
				}
			}
			MechanicalSight ms = Dungeon.hero.buff(MechanicalSight.class);
			if(ms!=null){
				ms.decrement();
				return true;
			}
		}


		float acuRoll;
		if (attacker.buff(LuckyLeaf.LuckyLeafBuff.class) != null) {
			acuRoll = Math.max(Random.Float(acuStat), Random.Float(acuStat));
		} else {
			acuRoll = Random.Float(acuStat);
		}
		if (attacker.buff(Bless.class) != null) acuRoll *= 1.25f;
		if (attacker.buff(Hex.class) != null) acuRoll *= 0.8f;
		if (Dungeon.hero.hasTalent(Talent.RESTRICTION)) {
			float phan = 1f - (Dungeon.hero.pointsInTalent(Talent.RESTRICTION) * 0.05f);
			acuRoll *= phan;
		}
		if (attacker.buff(Hallucination.class) != null) acuRoll *= 0.65f;
		if (attacker.buff(HuntingMark.class) != null) acuRoll *= 0.80f;
		for (ChampionEnemy buff : attacker.buffs(ChampionEnemy.class)) {
			acuRoll *= buff.evasionAndAccuracyFactor();
		}
		for (ChampionHero buff : attacker.buffs(ChampionHero.class)) {
			acuRoll *= buff.evasionAndAccuracyFactor();
		}

		float defRoll;
		if (defender.buff(LuckyLeaf.LuckyLeafBuff.class) != null) {
			defRoll = Math.max(Random.Float(defStat), Random.Float(defStat));
		} else {
			defRoll = Random.Float(defStat);
		}
		if (defender.buff(Bless.class) != null) defRoll *= 1.25f;
		if (defender.buff(ExecutMode.class) != null) defRoll *= 1.3f;
		if (defender instanceof Hero && Dungeon.hero.belongings.weapon instanceof AssassinsBlade && Dungeon.hero.belongings.armor instanceof LeatherArmor)
			defRoll *= 1.1f;
		if (defender.buff(Hex.class) != null) defRoll *= 0.8f;
		if (defender.buff(HuntingMark.class) != null) defRoll *= 0.8f;
		for (ChampionEnemy buff : defender.buffs(ChampionEnemy.class)) {
			defRoll *= buff.evasionAndAccuracyFactor();
		}
		for (ChampionHero buff : defender.buffs(ChampionHero.class)) {
			defRoll *= buff.evasionAndAccuracyFactor();
		}
		boolean result = (magic ? acuRoll * 2 : acuRoll) >= defRoll;

		if(Dungeon.hero.buff(PotatoAimReady.class)!=null && Dungeon.hero.buff(PotatoAimReady.class).isReady())return true;
		if(!result) {
			if (attacker instanceof Hero && ((Hero) attacker).subClassSet.contains(HeroSubClass.KEYANIMATOR) && ((Hero) attacker).hasTalent(Talent.POTATO_AIM)) {
				Buff.affect(Dungeon.hero,PotatoAimReady.class).set();
				Buff.affect(Dungeon.hero,PotatoAimReady.class).addCount();
			}
		}else{
			if (Dungeon.hero.buff(PotatoAimReady.class)!=null){
				Buff.detach( Dungeon.hero, PotatoAimReady.class );
			}
		}
		if(Dungeon.level.feeling == Level.Feeling.TYPHOON) return true;
		return result;
	}
	
	public int attackSkill( Char target ) {
		return 0;
	}
	
	public int defenseSkill( Char enemy ) {
		return 0;
	}
	
	public String defenseVerb() {
		return Messages.get(this, "def_verb");
	}
	
	public int drRoll() {
		return 0;
	}
	
	public int damageRoll() {
		return 1;
	}
	
	//TODO it would be nice to have a pre-armor and post-armor proc.
	// atm attack is always post-armor and defence is already pre-armor
	
	public int attackProc( Char enemy, int damage ) {
		if ( buff(Weakness.class) != null ){
			damage *= 0.67f;
		}
		for (ChampionEnemy buff : buffs(ChampionEnemy.class)){
			damage *= buff.meleeDamageFactor();
			buff.onAttackProc( enemy, damage );
		}for (ChampionHero buff : buffs(ChampionHero.class)){
			damage *= buff.meleeDamageFactor();
			buff.onAttackProc( enemy, damage );
		}

		if (Dungeon.hero.hasTalent(Talent.RESTRICTION)) {
			float restr = 1f - (Dungeon.hero.pointsInTalent(Talent.RESTRICTION) * 0.05f);
			damage *= restr;
		}

		return damage;
	}
	
	public int defenseProc( Char enemy, int damage ) {
		return damage;
	}
	
	public float speed() {
		float speed = baseSpeed;
		if ( buff( Cripple.class ) != null ) speed /= 2f;
		if ( buff( Stamina.class ) != null) speed *= 1.5f;
		if ( buff( Adrenaline.class ) != null) speed *= 2f;
		if ( buff( Haste.class ) != null) speed *= 3f;
		if ( buff (LanceCharge.class) != null) speed *= 5f;
		if ( this instanceof Hero && Dungeon.hero.hasTalent(Talent.GALLOP) ) speed *= 1.05f;
		if(!this.flying && Dungeon.level.feeling == Level.Feeling.TEMPEST) speed *= 1.4f;
		return speed;
	}
	
	//used so that buffs(Shieldbuff.class) isn't called every time unnecessarily
	private int cachedShield = 0;
	public boolean needsShieldUpdate = true;
	
	public int shielding(){
		if (!needsShieldUpdate){
			return cachedShield;
		}
		
		cachedShield = 0;
		for (ShieldBuff s : buffs(ShieldBuff.class)){
			cachedShield += s.shielding();
		}
		needsShieldUpdate = false;
		return cachedShield;
	}
	
	public void damage( int dmg, Object src ) {
		
		if (!isAlive() || dmg < 0) {
			return;
		}

		if(isInvulnerable(src.getClass())){
			sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "invulnerable"));
			return;
		}

		for (ChampionEnemy buff : buffs(ChampionEnemy.class)){
			dmg = (int) Math.ceil(dmg * buff.damageTakenFactor());
		}
		for (ChampionHero buff : buffs(ChampionHero.class)){
			dmg = (int) Math.ceil(dmg * buff.damageTakenFactor());
		}

		if (!(src instanceof LifeLink || src instanceof Naginata.naginataSkill || src instanceof ScrollOfSacrifice || src instanceof WarJournalist.PanoramaBuff
				|| src instanceof TrueSilverSlash.tss) && buff(LifeLink.class) != null){
			HashSet<LifeLink> links = buffs(LifeLink.class);
			for (LifeLink link : links.toArray(new LifeLink[0])){
				if (Actor.findById(link.object) == null){
					links.remove(link);
					link.detach();
				}
			}
			dmg = (int)Math.ceil(dmg / (float)(links.size()+1));
			for (LifeLink link : links){
				Char ch = (Char)Actor.findById(link.object);
				ch.damage(dmg, link);
				if (!ch.isAlive()){
					link.detach();
				}
			}
		}

		Terror t = buff(Terror.class);
		if (t != null){
			t.recover();
		}
		Charm c = buff(Charm.class);
		if (c != null){
			c.recover(src);
		}
		if (this.buff(Frost.class) != null){
			Buff.detach( this, Frost.class );
		}
		if (this.buff(MagicalSleep.class) != null){
			Buff.detach(this, MagicalSleep.class);
			if (this.isAlive()){
				if (this.buff(Dream.class) != null) {
					dmg+=Random.NormalIntRange(8 + Dungeon.hero.lvl, 12 + Dungeon.hero.lvl * 2);//change from budding
					//this.damage(Random.NormalIntRange(8 + Dungeon.hero.lvl, 12 + Dungeon.hero.lvl * 2), this);
					Buff.detach(this, Dream.class);
				}}
		}
		if (this.buff(Doom.class) != null && !isImmune(Doom.class)){
			dmg *= 2;
		}
		
		Class<?> srcClass = src.getClass();
		if (isImmune( srcClass )) {
			dmg = 0;
		} else {
			dmg = Math.round( dmg * resist( srcClass ));
		}
		
		//TODO improve this when I have proper damage source logic
		if (AntiMagic.RESISTS.contains(src.getClass()) && buff(ArcaneArmor.class) != null){
			dmg -= Random.NormalIntRange(0, buff(ArcaneArmor.class).level());
			if (dmg < 0) dmg = 0;
		}
		
		if (buff( Paralysis.class ) != null) {
			buff( Paralysis.class ).processDamage(dmg);
		}

		if (buff( SeethingBurst.class ) != null) {
			buff( SeethingBurst.class ).GetDamage(dmg);
		}

		int shielded = dmg;
		//FIXME: when I add proper damage properties, should add an IGNORES_SHIELDS property to use here.
		if (!(src instanceof Hunger)){
			for (ShieldBuff s : buffs(ShieldBuff.class)){
				dmg = s.absorbDamage(dmg);
				if (dmg == 0) break;
			}
		}

		shielded -= dmg;
		if(this instanceof Hero && this.HT<=this.HP && dmg>=this.HT && Dungeon.isSPChallenged(SPChallenges.GLASS) && buff(ROR2Shield.class)==null) {
			dmg = this.HT-1;
			if (sprite != null) {sprite.showStatus(CharSprite.NEGATIVE,
						"—!—");
			}
		}
		if (!Dummy.kkdy && !ImmortalShield.isImmortal(this))HP -= dmg;//change from budding

		if (sprite != null) {
			if(AntiMagic.RESISTS.contains(src.getClass())){
				sprite.showStatus(CharSprite.MAGIC,
						Integer.toString(dmg + shielded));
			}else{sprite.showStatus(HP > HT / 2 ?
							CharSprite.WARNING :
							CharSprite.NEGATIVE,
					Integer.toString(dmg + shielded));}
		}//显示伤害数字部分
		if(Dungeon.hero.belongings.misc instanceof WakeOfVultures && !isAlive()){
			WakeOfVultures wov = (WakeOfVultures)Dungeon.hero.belongings.misc;
			wov.VulturesUponKill(Dungeon.hero, this);
		}

		if (HP < 0) HP = 0;

		if (!isAlive()) {
			die( src );
		}
	}
	
	public void destroy() {
		HP = 0;
		Actor.remove( this );

		for (Char ch : Actor.chars().toArray(new Char[0])){
			if (ch.buff(Charm.class) != null && ch.buff(Charm.class).object == id()){
				ch.buff(Charm.class).detach();
			}
			if (ch.buff(Terror.class) != null && ch.buff(Terror.class).object == id()){
				ch.buff(Terror.class).detach();
			}
			if (ch.buff(SnipersMark.class) != null && ch.buff(SnipersMark.class).object == id()){
				ch.buff(SnipersMark.class).detach();
			}
		}
	}
	
	public void die( Object src ) {
		destroy();
		if (src != Chasm.class) sprite.die();
	}
	
	public boolean isAlive() {
		return HP > 0;
	}
	// change from budding, shattered
	public boolean isActive() {
		return isAlive();
	}
	
	@Override
	protected void spend( float time ) {
		
		float timeScale = 1f;
		if (buff( Slow.class ) != null) {
			timeScale *= 0.5f;
			//slowed and chilled do not stack
		} else if (buff( Chill.class ) != null) {
			timeScale *= buff( Chill.class ).speedFactor();
		}
		if (buff( Speed.class ) != null) {
			timeScale *= 2.0f;
		}
		if (buff(Oblivion.class) != null) {
			timeScale *= 0.5f;
		}
		
		super.spend( time / timeScale );
	}
	
	public synchronized HashSet<Buff> buffs() {
		return new HashSet<>(buffs);
	}
	
	@SuppressWarnings("unchecked")
	//returns all buffs assignable from the given buff class
	public synchronized <T extends Buff> HashSet<T> buffs( Class<T> c ) {
		HashSet<T> filtered = new HashSet<>();
		for (Buff b : buffs) {
			if (c.isInstance( b )) {
				filtered.add( (T)b );
			}
		}
		return filtered;
	}

	@SuppressWarnings("unchecked")
	//returns an instance of the specific buff class, if it exists. Not just assignable
	public synchronized  <T extends Buff> T buff( Class<T> c ) {
		for (Buff b : buffs) {
			if (b.getClass() == c) {
				return (T)b;
			}
		}
		return null;
	}

	public synchronized boolean isCharmedBy( Char ch ) {
		int chID = ch.id();
		for (Buff b : buffs) {
			if (b instanceof Charm && ((Charm)b).object == chID) {
				return true;
			}
		}
		return false;
	}

	public synchronized void add( Buff buff ) {
		
		buffs.add( buff );
		if (Actor.chars().contains(this)) Actor.add( buff );

		if (sprite != null && buff.announced)
			switch(buff.type){
				case POSITIVE:
					sprite.showStatus(CharSprite.POSITIVE, buff.toString());
					break;
				case NEGATIVE:
					sprite.showStatus(CharSprite.NEGATIVE, buff.toString());
					break;
				case NEUTRAL: default:
					sprite.showStatus(CharSprite.NEUTRAL, buff.toString());
					break;
			}

	}
	
	public synchronized void remove( Buff buff ) {
		
		buffs.remove( buff );
		Actor.remove( buff );

	}
	
	public synchronized void remove( Class<? extends Buff> buffClass ) {
		for (Buff buff : buffs( buffClass )) {
			remove( buff );
		}
	}
	
	@Override
	protected synchronized void onRemove() {
		for (Buff buff : buffs.toArray(new Buff[buffs.size()])) {
			buff.detach();
		}
	}
	
	public synchronized void updateSpriteState() {
		TomorrowRogueNight.actorLogger.logActorEntry(this.getClass(),"updateSpriteState");
		for (Buff buff:buffs) {
			buff.fx( true );
		}
	}
	
	public float stealth() {
		return 0;
	}
	public final void move (int step ){//change from budding,shattered
		move(step,true);
	}
	public void move( int step ,boolean travelling) {

		if (travelling && Dungeon.level.adjacent( step, pos ) && buff( Vertigo.class ) != null) {
			sprite.interruptMotion();
			int newPos = pos + PathFinder.NEIGHBOURS8[Random.Int( 8 )];
			if (!(Dungeon.level.passable[newPos] || Dungeon.level.avoid[newPos])
					|| (properties().contains(Property.LARGE) && !Dungeon.level.openSpace[newPos])
					|| Actor.findChar( newPos ) != null)
				return;
			else {
				sprite.move(pos, newPos);
				step = newPos;
			}
		}

		if (Dungeon.level.map[pos] == Terrain.OPEN_DOOR) {
			Door.leave( pos );
		}

		pos = step;
		
		if (this != Dungeon.hero) {
			sprite.visible = Dungeon.level.heroFOV[pos];
		}
		
		Dungeon.level.occupyCell(this );
	}
	
	public int distance( Char other ) {
		return Dungeon.level.distance( pos, other.pos );
	}
	
	public void onMotionComplete() {
		//Does nothing by default
		//The main actor thread already accounts for motion,
		// so calling next() here isn't necessary (see Actor.process)
	}
	
	public void onAttackComplete() {
		next();
	}
	
	public void onOperateComplete() {
		next();
	}
	public void onEatComplete() {
		next();
	}
	
	protected final HashSet<Class> resistances = new HashSet<>();
	
	//returns percent effectiveness after resistances
	//TODO currently resistances reduce effectiveness by a static 50%, and do not stack.
	public float resist( Class effect ){
		HashSet<Class> resists = new HashSet<>(resistances);
		for (Property p : properties()){
			resists.addAll(p.resistances());
		}
		for (Buff b : buffs()){
			resists.addAll(b.resistances());
		}
		
		float result = 1f;
		for (Class c : resists){
			if (c.isAssignableFrom(effect)){
				result *= 0.5f;
			}
		}
		return result * RingOfElements.resist(this, effect);
	}
	
	protected final HashSet<Class> immunities = new HashSet<>();
	
	public boolean isImmune(Class effect ){
		HashSet<Class> immunes = new HashSet<>(immunities);
		for (Property p : properties()){
			immunes.addAll(p.immunities());
		}
		for (Buff b : buffs()){
			immunes.addAll(b.immunities());
		}
		
		for (Class c : immunes){
			if (c.isAssignableFrom(effect)){
				return true;
			}
		}
		return false;
	}

	public void addImmune(Class effect){
		if(!this.isImmune(effect)){
			this.immunities.add(effect);
		}
	}

	//similar to isImmune, but only factors in damage.
	//Is used in AI decision-making
	public boolean isInvulnerable( Class effect ){
		return false;
	}

	protected HashSet<Property> properties = new HashSet<>();

	public HashSet<Property> properties() {
		HashSet<Property> props = new HashSet<>(properties);
		//TODO any more of these and we should make it a property of the buff, like with resistances/immunities
		if (buff(ChampionEnemy.Giant.class) != null) {
			props.add(Property.LARGE);
		}
		if (buff(ChampionHero.Giant.class) != null) {
			props.add(Property.LARGE);
		}
		return props;
	}

	public enum Property{
		BOSS ( new HashSet<Class>( Arrays.asList(Grim.class, GrimTrap.class, ScrollOfRetribution.class, ScrollOfPsionicBlast.class, Oblivion.class)),
				new HashSet<Class>( Arrays.asList(Corruption.class, StoneOfAggression.Aggression.class, Silence.class) )),
		MINIBOSS ( new HashSet<Class>(),
				new HashSet<Class>( Arrays.asList(Corruption.class, Silence.class) )),
		UNDEAD,
		DEMONIC,
		INORGANIC ( new HashSet<Class>(),
				new HashSet<Class>( Arrays.asList(Bleeding.class, ToxicGas.class, Poison.class) )),
		FIERY ( new HashSet<Class>( Arrays.asList(WandOfFireblast.class, StaffOfSkyfire.class, Elemental.FireElemental.class)),
				new HashSet<Class>( Arrays.asList(Burning.class, Blazing.class))),
		ICY ( new HashSet<Class>( Arrays.asList(WandOfFrost.class, StaffOfLeaf.class, Elemental.FrostElemental.class)),
				new HashSet<Class>( Arrays.asList(Frost.class, Chill.class))),
		ACIDIC ( new HashSet<Class>( Arrays.asList(Corrosion.class)),
				new HashSet<Class>( Arrays.asList(Ooze.class))),
		ELECTRIC ( new HashSet<Class>( Arrays.asList(WandOfLightning.class, StaffOfGreyy.class, Shocking.class, Potential.class, Electricity.class, ShockingDart.class, Elemental.ShockElemental.class )),
				new HashSet<Class>()),
		LARGE,
		IMMOVABLE,
		NO_KNOCKBACK,
		// 명픽던 추가 요소
		DRONE ( new HashSet<Class>(),
				new HashSet<Class>(Arrays.asList(Bleeding.class, Roots.class))),
		SARKAZ( new HashSet<Class>( Arrays.asList(Grim.class, WandOfDisintegration.class, Oblivion.class)),
				new HashSet<Class>()),
		NPC ( new HashSet<Class>(),
				new HashSet<Class>(Arrays.asList(Corruption.class, Amok.class, Terror.class, MagicalSleep.class))),
		SEA,
		INFECTED;

		private HashSet<Class> resistances;
		private HashSet<Class> immunities;
		
		Property(){
			this(new HashSet<Class>(), new HashSet<Class>());
		}
		
		Property( HashSet<Class> resistances, HashSet<Class> immunities){
			this.resistances = resistances;
			this.immunities = immunities;
		}
		
		public HashSet<Class> resistances(){
			return new HashSet<>(resistances);
		}
		
		public HashSet<Class> immunities(){
			return new HashSet<>(immunities);
		}

	}

	public static boolean hasProp( Char ch, Property p){
		return (ch != null && ch.properties().contains(p));
	}
}
