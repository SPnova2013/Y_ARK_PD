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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.FrostBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public abstract class ChampionEnemy extends Buff {

	{
		type = buffType.POSITIVE;
	}

	public int color;

	@Override
	public int icon() {
		return BuffIndicator.CORRUPT;
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(color);
	}

	@Override
	public void fx(boolean on) {
		if (on) target.sprite.aura( color );
		else target.sprite.clearAura();
	}

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc");
	}
	public void onAttackProc(Char enemy, int damage ){

	}

	public boolean canAttackWithExtraReach( Char enemy ){
		return false;
	}

	public float meleeDamageFactor(){
		return 1f;
	}

	public float damageTakenFactor(){
		return 1f;
	}

	public float evasionAndAccuracyFactor(){
		return 1f;
	}

	{
		immunities.add(Corruption.class);
	}

	public static void rollForChampion(Mob m){
		if (Dungeon.mobsToChampion <= 0) Dungeon.mobsToChampion = 8;

		Dungeon.mobsToChampion--;

		if (Dungeon.mobsToChampion <= 0){
			switch (Random.Int(6)){
				case 0: default:    Buff.affect(m, Blazing.class);      break;
				case 1:             Buff.affect(m, Projecting.class);   break;
				case 2:             Buff.affect(m, AntiMagic.class);    break;
				case 3:             Buff.affect(m, Giant.class);        break;
				case 4:             Buff.affect(m, Blessed.class);      break;
				case 5:             Buff.affect(m, Growing.class);      break;
			}
			m.state = m.WANDERING;
		}
	}
	public static void rollForHonor(Mob m){
		if (Dungeon.mobsToHonor <= 0) Dungeon.mobsToHonor = Random.IntRange(7,10);

		Dungeon.mobsToHonor--;

		if (Dungeon.mobsToHonor <= 0){
			switch (Random.Int(7)){
				case 0: default:    Buff.affect(m, R2Blazing.class);      break;
				case 1:
					Buff.affect(m, R2Overloading.class);
					Buff.affect(m, ROR2Shield.class).setMaxShield(m.HT/2);
					break;
				case 2:     Buff.affect(m, R2Glacial.class);      break;
				case 3:     Buff.affect(m, R2Malachite.class);      break;
				case 4:     Buff.affect(m, R2Celestine.class);      break;
				case 5:
					if(Random.Int(2)==0){
						Buff.affect(m, R2Perfected.class);
						Buff.affect(m, ROR2Shield.class).setMaxShield(m.HT);
					}
					break;
				case 6:     Buff.affect(m, R2Mending.class);      break;
			}
			m.state = m.WANDERING;
		}
	}

	public static class Blazing extends ChampionEnemy {

		{
			color = 0xFF8800;
		}

		@Override
		public void onAttackProc(Char enemy, int damage) {
			Buff.affect(enemy, Burning.class).reignite(enemy);
		}

		@Override
		public void detach() {
			for (int i : PathFinder.NEIGHBOURS9){
				if (!Dungeon.level.solid[target.pos+i]){
					GameScene.add(Blob.seed(target.pos+i, 2, Fire.class));
				}
			}
			super.detach();
		}

		@Override
		public float meleeDamageFactor() {
			return 1.25f;
		}

		{
			immunities.add(Burning.class);
		}
	}

	public static class Projecting extends ChampionEnemy {

		{
			color = 0x8800FF;
		}

		@Override
		public float meleeDamageFactor() {
			return 1.25f;
		}

		@Override
		public boolean canAttackWithExtraReach( Char enemy ) {
			return target.fieldOfView[enemy.pos]; //if it can see it, it can attack it.
		}
	}

	public static class AntiMagic extends ChampionEnemy {

		{
			color = 0x00FF00;
		}

		@Override
		public float damageTakenFactor() {
			return 0.75f;
		}

		{
			immunities.addAll(com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic.RESISTS);
		}

	}

	//Also makes target large, see Char.properties()
	public static class Giant extends ChampionEnemy {

		{
			color = 0x0088FF;
		}

		@Override
		public float damageTakenFactor() {
			return 0.25f;
		}

		@Override
		public boolean canAttackWithExtraReach(Char enemy) {
			//attack range of 2
			return target.fieldOfView[enemy.pos] && Dungeon.level.distance(target.pos, enemy.pos) <= 2;
		}
	}

	public static class Blessed extends ChampionEnemy {

		{
			color = 0xFFFF00;
		}

		@Override
		public float evasionAndAccuracyFactor() {
			return 3f;
		}
	}

	public static class Growing extends ChampionEnemy {

		{
			color = 0xFF0000;
		}

		private float multiplier = 1.19f;

		@Override
		public boolean act() {
			multiplier += 0.01f;
			spend(3*TICK);
			return true;
		}

		@Override
		public float meleeDamageFactor() {
			return multiplier;
		}

		@Override
		public float damageTakenFactor() {
			return 1f/multiplier;
		}

		@Override
		public float evasionAndAccuracyFactor() {
			return multiplier;
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", (int)(100*(multiplier-1)), (int)(100*(1 - 1f/multiplier)));
		}

		private static final String MULTIPLIER = "multiplier";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(MULTIPLIER, multiplier);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			multiplier = bundle.getFloat(MULTIPLIER);
		}
	}

	public static class R2Blazing extends ChampionEnemy {
		{
			color = 0xFF6600;
		}
		@Override
		public void onAttackProc(Char enemy, int damage) {
			Buff.affect(enemy, Burning.class).reignite(enemy);
		}
		@Override
		public boolean act() {
			GameScene.add(Blob.seed(target.pos, 3, Fire.class));
			spend(TICK);
			return true;
		}
		{
			immunities.add(Burning.class);
		}
	}
	public static class R2Overloading extends ChampionEnemy {
		{
			color = 0x55AAFF;
		}
		@Override
		public void onAttackProc(Char enemy, int damage) {
			Buff.append(enemy, R2Overloading.BlazingBomb.class).setup(enemy, damage, Dungeon.depth);
		}
		@Override
		public boolean act() {
			target.HP = Math.min(target.HT/2, target.HP);
			spend(TICK);
			return true;
		}
		public static class BlazingBomb extends Buff{
			private Char victim;
			private int explodeDepth;
			private int left;
			private int damage;

			public void setup( Char victim,int damage, int explodeDepth){
				this.victim = victim;
				this.explodeDepth = explodeDepth;
				this.damage = damage;
				left = 3;
			}
			@Override
			public boolean act() {
				if (explodeDepth == Dungeon.depth){
					left--;
					if (left <= 0){
						WandOfLightning wol = new WandOfLightning();
						victim.damage(damage,wol);
						if (victim==Dungeon.hero && !Dungeon.hero.isAlive()) {
							Dungeon.fail( getClass() );
							GLog.n( Messages.get(this, "blazingbomb_kill") );
						}
						next();
						detach();
						return false;
					}
				}
				spend( TICK );
				return true;
			}
		}
	}
	public static class R2Glacial extends ChampionEnemy {
		{
			color = 0xCCEEFF;
		}
		@Override
		public void onAttackProc(Char enemy, int damage) {
			Buff.affect(enemy, Chill.class, 2f);
		}
		@Override
		public void detach() {
			Bomb bomb = new GlacialBomb();
			Actor.addDelayed(bomb.fuse = new Bomb.Fuse().ignite(bomb), 2);
			Dungeon.level.drop(bomb, target.pos);
			super.detach();
		}
		public class GlacialBomb extends Bomb {
			{
				image = ItemSpriteSheet.FROST_BOMB;
			}
			@Override
			public void explode(int cell) {
				PathFinder.buildDistanceMap( cell, BArray.not( Dungeon.level.solid, null ), 2 );
				for (int i = 0; i < PathFinder.distance.length; i++) {
					if (PathFinder.distance[i] < Integer.MAX_VALUE) {
						Char ch = Actor.findChar(i);
						if (ch != null){
							ch.damage(Dungeon.depth, this);
							Buff.affect(ch, Frost.class, 2f);
						}
					}
				}
			}
			@Override
			public boolean doPickUp(Hero hero) {
				return false;
			}
			@Override
			public String desc() {
				return Messages.get(this, "desc");
			}
		}
	}
	public static class R2Malachite extends ChampionEnemy {
		{
			color = 0x005500;
		}
		@Override
		public void onAttackProc(Char enemy, int damage) {
			Buff.affect(enemy, DisableHealing.class).set(enemy.HP, DisableHealing.DURATION);
		}
	}
	public static class R2Celestine extends ChampionEnemy {
		{
			color = 0x006688;
		}
		@Override
		public void onAttackProc(Char enemy, int damage) {
			Buff.affect(enemy, Cripple.class, 1f);
		}
		@Override
		public boolean act() {
			ArrayList<Char> affected = new ArrayList<>();
			for (Mob mob : Dungeon.level.mobs) {
				if (Dungeon.level.distance(target.pos, mob.pos) <= 3
						&& mob.buff(R2Celestine.class) == null) {
					affected.add(mob);
				}
			}
			for ( Char ch : affected ){
				Buff.prolong(ch, Vanish.class, 2f);
			}
			spend(TICK);
			return true;
		}
	}
	public static class R2Perfected extends ChampionEnemy {
		{
			color = 0x7777DD;
		}
		@Override
		public boolean act() {
			target.HP = Math.min(1, target.HP);
			spend(TICK);
			return true;
		}
		@Override
		public void onAttackProc(Char enemy, int damage) {
			Buff.affect(enemy, Cripple.class, 1f);
			Buff.affect(enemy, Vulnerable.class, 1f);
		}
	}
	public static class R2Mending extends ChampionEnemy {
		{
			color = 0x99FF00;
		}
		@Override
		public boolean act() {
			ArrayList<Char> affected = new ArrayList<>();
			for (Mob mob : Dungeon.level.mobs) {
				if (Dungeon.level.distance(target.pos, mob.pos) <= 2
						&& mob.buff(R2Mending.class) == null) {
					affected.add(mob);
				}
			}
			for ( Char ch : affected ){
				ch.HP++;
				ch.HP=Math.min(ch.HP, ch.HT);
				ch.sprite.emitter().start( Speck.factory( Speck.HEALING ), 0.4f, 4 );
			}
			spend(TICK);
			return true;
		}
		@Override
		public void detach() {
			Bomb bomb = new MendingBomb();
			Actor.addDelayed(bomb.fuse = new Bomb.Fuse().ignite(bomb), 2);
			Dungeon.level.drop(bomb, target.pos);
			super.detach();
		}
		public class MendingBomb extends Bomb {
			{
				image = ItemSpriteSheet.FROST_BOMB;
			}
			@Override
			public void explode(int cell) {
				PathFinder.buildDistanceMap( cell, BArray.not( Dungeon.level.solid, null ), 2 );
				for (int i = 0; i < PathFinder.distance.length; i++) {
					if (PathFinder.distance[i] < Integer.MAX_VALUE) {
						Char ch = Actor.findChar(i);
						if (ch != null){
							ch.HP+=ch.HT/5;
							ch.HP=Math.min(ch.HP,ch.HT);
							ch.sprite.emitter().start( Speck.factory( Speck.HEALING ), 0.4f, 4 );
						}
					}
				}
			}
			@Override
			public boolean doPickUp(Hero hero) {
				return false;
			}
			@Override
			public String desc() {
				return Messages.get(this, "desc");
			}
		}
	}
}
