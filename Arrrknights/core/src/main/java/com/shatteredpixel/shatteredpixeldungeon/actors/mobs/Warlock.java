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

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ActiveOriginium;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Degrade;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.OriginiumShard;
import com.shatteredpixel.shatteredpixeldungeon.items.ScholarNotebook;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.EtherealChains;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MidnightSword;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ThrowerSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class Warlock extends Mob implements Callback {
	
	private static final float TIME_TO_ZAP	= 1f;
	
	{
		spriteClass = ThrowerSprite.class;
		
		HP = HT = 75;
		damageMax = 18;
		damageMin = 12;
		drMax = 8;
		drMin = 0;
		defenseSkill = 18;
		
		EXP = 11;
		maxLvl = 21;
		
		loot = Generator.Category.POTION;
		lootChance = 0.5f;

	}
	@Override
	public int attackSkill( Char target ) {
		Buff.affect(this, ActiveOriginium.class).set(HT * 0.1f);
		return 25+attackSkillIncRate*rounds;
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
	protected boolean canAttack( Char enemy ) {
		if(this.buff(Silence.class) != null) return false;
		return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}
	
	protected boolean doAttack( Char enemy ) {

		if (Dungeon.level.adjacent( pos, enemy.pos )) {
			
			return super.doAttack( enemy );
			
		} else {
			
			if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
				sprite.zap( enemy.pos );
				return false;
			} else {
				zap();
				return true;
			}
		}
	}
	
	//used so resistances can differentiate between melee and magical attacks
	public static class DarkBolt{}
	
	private void zap() {
		spend( TIME_TO_ZAP );

		if (hit( this, enemy, true )) {

			int dmg = Random.NormalIntRange( 14+damageMinIncRate*rounds/2, 18+damageMaxIncRate*rounds/2 );
			enemy.damage( dmg, new DarkBolt() );
			if (Dungeon.isChallenged(Challenges.TACTICAL_UPGRADE)) Buff.affect(enemy, Weakness.class, 2f);
			
			if (enemy == Dungeon.hero && !enemy.isAlive()) {
				Dungeon.fail( getClass() );
				GLog.n( Messages.get(this, "bolt_kill") );
			}
		} else {
			enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
		}
	}
	
	public void onZapComplete() {
		zap();
		next();
	}
	
	@Override
	public void call() {
		next();
	}

	@Override
	public Item createLoot(){

		// 1/6 chance for healing, scaling to 0 over 8 drops
		if (Random.Int(2) == 0 && Random.Int(8) > Dungeon.LimitedDrops.WARLOCK_HP.count ){
			Dungeon.LimitedDrops.WARLOCK_HP.count++;
			return new PotionOfHealing();
		} else {
			Item i = Generator.randomUsingDefaults(Generator.Category.POTION);
			int healingTried = 0;
			while (i instanceof PotionOfHealing){
				healingTried++;
				i = Generator.randomUsingDefaults(Generator.Category.POTION);
			}

			//return the attempted healing potion drops to the pool
			if (healingTried > 0){
				for (int j = 0; j < Generator.Category.POTION.classes.length; j++){
					if (Generator.Category.POTION.classes[j] == PotionOfHealing.class){
						Generator.Category.POTION.probs[j] += healingTried;
					}
				}
			}

			return i;
		}

	}
	@Override
	public boolean hasNotebookSkill(){ return true;}
	@Override
	public void notebookSkill(ScholarNotebook notebook, int index){
		GameScene.selectCell(caster);
	}
	private CellSelector.Listener caster = new CellSelector.Listener(){
		@Override
		public void onSelect(Integer target) {
			if (target == null) return;
			final Ballistica shot = new Ballistica( hero.pos, target, Ballistica.MAGIC_BOLT);
			int cell = shot.collisionPos;
			if (target == hero.pos || cell == hero.pos) {
				GLog.i( Messages.get(Wand.class, "self_target") );
				return;
			}
			Char ch = Actor.findChar( shot.collisionPos );
			if(ch == null)return;
			hero.sprite.zap(cell);
			if (hero.buff(MagicImmune.class) != null || hero.buff(Silence.class) != null){
				GLog.w( Messages.get(MidnightSword.class, "no_magic") );
				return;
			}
			hero.busy();
			MagicMissile.boltFromChar( hero.sprite.parent,
					MagicMissile.SHADOW,
					hero.sprite,
					cell,
					new Callback() {
						@Override
						public void call() {
						}
					} );
			Buff.affect(ch,ActiveOriginium.class).set(10);
			int dmg = Random.NormalIntRange( 14, 18 );
			ch.damage( dmg, new DarkBolt() );
			hero.next();
		}
		@Override
		public String prompt() {
			return Messages.get(EtherealChains.class, "prompt");
		}
	};
}
