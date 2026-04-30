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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ActiveOriginium;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Camouflage;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SSP.StaffOfMageHand;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.EnragedSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Monk extends Mob {
	
	{
		spriteClass = EnragedSprite.class;
		
		HP = HT = 105;
		drMax = 2;
		drMin = 0;
		defenseSkill = 18;
		
		EXP = 11;
		maxLvl = 21;
		
		loot = new Food();
		lootChance = 0.083f;

		immunities.add(Silence.class);
	}
	
	@Override
	public int damageRoll()
	{
		Focus f = buff(Focus.class);
		if (f != null) {
			return Random.NormalIntRange( 20+damageMinIncRate*rounds/2, 32+damageMaxIncRate*rounds/2 );
		}
		return Random.NormalIntRange( 10+damageMinIncRate*rounds/2, 27+damageMaxIncRate*rounds/2 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		Buff.affect(this, ActiveOriginium.class).set(HT * 0.1f);
		return 30+attackSkillIncRate*rounds;
	}
	
	@Override
	protected float attackDelay() {
		if (this.buff(ActiveOriginium.class) == null) {
			return super.attackDelay();
		} else
		{
			return super.attackDelay() * 0.5f;
		}
	}

	@Override
	public float speed() {
		Focus f = buff(Focus.class);
		if (f != null) {
			return super.speed()*2;
		}
		return super.speed();
	}
	@Override
	public void rollToDropLoot() {
		if(buff(StaffOfMageHand.MageHandStolenTracker.class)==null) {
			Imp.Quest.process( this );
		}
		
		super.rollToDropLoot();
	}
	
	protected float focusCooldown = 0;
	
	@Override
	protected boolean act() {
		
		if (Dungeon.isChallenged(Challenges.TACTICAL_UPGRADE) && Camouflage.CamoFlageEnemy(this)) Buff.affect(this, Camouflage.class, 10f);//change from budding;originally behind the super
		
		boolean result = super.act();
		if (buff(Focus.class) == null && state == HUNTING && focusCooldown <= 0) {
			Buff.affect( this, Focus.class );
		}

		return result;
	}
	
	@Override
	protected void spend( float time ) {
		focusCooldown -= time;
		super.spend( time );
	}
	
	@Override
	public void move( int step ,boolean travelling) {
		// moving reduces cooldown by an additional 0.67, giving a total reduction of 1.67f.
		// basically monks will become focused notably faster if you kite them.
		if (travelling) focusCooldown -= 0.67f;
		super.move( step ,travelling);
	}
	
	@Override
	public int defenseSkill( Char enemy ) {
		if (buff(Focus.class) != null && paralysed == 0 && state != SLEEPING){
			return INFINITE_EVASION;
		}
		return super.defenseSkill( enemy );
	}
	
	@Override
	public String defenseVerb() {
		Focus f = buff(Focus.class);
		if (f == null) {
			return super.defenseVerb();
		} else if(Dungeon.level.feeling == Level.Feeling.TYPHOON){
			f.detach();
			focusCooldown = Random.NormalFloat( 9,11 );
			return super.defenseVerb();
		}
		else{
			f.detach();
			Sample.INSTANCE.play( Assets.Sounds.HIT_PARRY, 1, Random.Float(0.96f, 1.05f));
			focusCooldown = Random.NormalFloat( 9,11 );
			return Messages.get(this, "parried");
		}
	}

	@Override
	public void die( Object cause ) {
		super.die(cause);
		if (Random.Int(0,100) <= 1)
		{
			Dungeon.level.drop(Generator.random(Generator.Category.SKL_T2), pos ).sprite.drop( pos );
		}
	}
	
	private static String FOCUS_COOLDOWN = "focus_cooldown";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( FOCUS_COOLDOWN, focusCooldown );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		focusCooldown = bundle.getInt( FOCUS_COOLDOWN );
	}
	
	public static class Focus extends Buff {
		
		{
			type = buffType.POSITIVE;
			announced = true;
		}
		
		@Override
		public int icon() {
			return BuffIndicator.MIND_VISION;
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
	}

	@Override
	public boolean hasNotebookSkill(){ return true;}
}
