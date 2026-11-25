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
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Camouflage;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Brute;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.MudrockZealot;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.NewDM300;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Pompeii;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Raider;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Succubus;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogDzewa;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.miniboss.BloodMagister;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.miniboss.Faust;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.miniboss.Mon3tr;
import com.shatteredpixel.shatteredpixeldungeon.effects.CheckedCell;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;

import java.util.ArrayList;

public class BattleAxe extends MeleeWeapon {
	public static final String AC_ZAP = "ZAP";
	{
		image = ItemSpriteSheet.BATTLE_AXE;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 0.9f;

		defaultAction = AC_ZAP;

		tier = 4;
		ACC = 1.15f; //24% boost to accuracy
	}

	@Override
	public int max(int lvl) {
		return  3*(tier+1) +    //15+3
				lvl*(tier-1);
	}

	private int starpower = 0 ;
	private int starpowercap = 3;

	//星座守护: the stars above lay the ground bare
	public static final int ABILITY_RANGE_BASE = 5;

	public int abilityRange() {
		return ABILITY_RANGE_BASE + buffedLvl() / 2;
	}

	@Override
	public String abilityInfo() {
		return Messages.get(this, "ability_desc", abilityRange());
	}
	@Override
	public String statusBottom() {
		if (!isIdentified() || cursed) return null;
		if (isEquipped(Dungeon.hero)
				&& Dungeon.hero.buff(Charger.class) != null) {
			Charger buff = Dungeon.hero.buff(Charger.class);
			return String.valueOf(buff.charges);
		} else {
			return super.status();
		}
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		hero.belongings.abilityWeapon = this;
		beforeAbilityUsed(hero, null);

		//the reveal itself, exactly like the stone of clairvoyance
		revealAround(hero.pos, abilityRange());

		//and one more point of star power
		if (starpower < starpowercap) {
			starpower++;
		}
		if (starpower > 0 && hero.buff(StarpowerBuff.class) == null) {
			Buff.affect(hero, StarpowerBuff.class);
		}
		updateQuickslot();

		hero.sprite.operate(hero.pos);
		Sample.INSTANCE.play(Assets.Sounds.SKILL_BASIC);
		Invisibility.dispel();

		afterAbilityUsed(hero);
		hero.spendAndNext(Actor.TICK);
	}

	private void revealAround(int cell, int range) {
		int dist = Math.min(range, ShadowCaster.MAX_DISTANCE);

		Point c = Dungeon.level.cellToPoint(cell);
		int[] rounding = ShadowCaster.rounding[dist];

		int left, right, curr;
		boolean noticed = false;

		for (int y = Math.max(0, c.y - dist); y <= Math.min(Dungeon.level.height() - 1, c.y + dist); y++) {
			if (rounding[Math.abs(c.y - y)] < Math.abs(c.y - y)) {
				left = c.x - rounding[Math.abs(c.y - y)];
			} else {
				left = dist;
				while (rounding[left] < rounding[Math.abs(c.y - y)]) {
					left--;
				}
				left = c.x - left;
			}
			right = Math.min(Dungeon.level.width() - 1, c.x + c.x - left);
			left = Math.max(0, left);
			for (curr = left + y * Dungeon.level.width(); curr <= right + y * Dungeon.level.width(); curr++) {
				GameScene.effectOverFog(new CheckedCell(curr, cell));
				Dungeon.level.mapped[curr] = true;

				if (Dungeon.level.secret[curr]) {
					Dungeon.level.discover(curr);
					if (Dungeon.level.heroFOV[curr]) {
						GameScene.discoverTile(curr, Dungeon.level.map[curr]);
						ScrollOfMagicMapping.discover(curr);
						noticed = true;
					}
				}
			}
		}

		if (noticed) {
			Sample.INSTANCE.play(Assets.Sounds.SECRET);
		}

		Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
		GameScene.updateFog();
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_ZAP);
		return actions;
	}

	@Override
	public boolean doEquip(Hero hero) {
		boolean result = super.doEquip(hero);
		if (result && starpower > 0) {
			Buff.affect(hero, StarpowerBuff.class);
		}
		return result;
	}
	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {
		Buff.detach(hero, StarpowerBuff.class);
		return super.doUnequip(hero, collect, single);
	}
	@Override
	public void execute(Hero hero, String action) {

		super.execute(hero, action);

		if (action.equals(AC_ZAP) && Dungeon.hero.belongings.weapon == this) {
			if (starpower < starpowercap) {
				starpower++;
				updateQuickslot();
				hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(BattleAxe.class, "charge"));
				if (hero.buff(StarpowerBuff.class) == null) {
					Buff.affect(hero, StarpowerBuff.class);
				}
				if (Dungeon.hero.belongings.getItem(TalismanOfForesight.class) != null) {
					if (Dungeon.hero.belongings.getItem(TalismanOfForesight.class).isEquipped(Dungeon.hero)) {
						curUser.spendAndNext(0.75f);
					}
					else curUser.spendAndNext(2f);
				}
				else curUser.spendAndNext(2f);
			} else
				hero.sprite.showStatus(CharSprite.NEGATIVE, Messages.get(BattleAxe.class, "charge_fail"));
		}
	}


	@Override
	public int proc(Char attacker, Char defender, int damage) {

		if (starpower >= 1 && attacker instanceof Hero) {
			for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
				if (mob.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[mob.pos]) {
					int dmg = attacker.damageRoll() - defender.drRoll();
					dmg = Math.round(dmg * (starpower * 0.7f));

					mob.damage(dmg, attacker);
				}
			}
			if (starpower == 3) {
				GameScene.flash(0x80FFFFFF);
				dispel(defender);
			}
			Camera.main.shake(2, starpower / 3);

			Sample.INSTANCE.play(Assets.Sounds.HIT_SLASH, 1.76f, 1.76f);
			attacker.sprite.showStatus(CharSprite.POSITIVE, Messages.get(BattleAxe.class, "attack"));
		}

		starpower = 0;
		updateQuickslot();
		Buff.detach(attacker, StarpowerBuff.class);
		return super.proc(attacker, defender, damage);
	}

	@Override
	public String desc() {
		String info = Messages.get(this, "desc");
		if (Dungeon.hero.belongings.getItem(TalismanOfForesight.class) != null) {
		if (Dungeon.hero.belongings.getItem(TalismanOfForesight.class).isEquipped(Dungeon.hero))
			info += "\n\n" + Messages.get( BattleAxe.class, "setbouns");}

		return info;
	}

	@Override
	public String status() {
		if (this.isIdentified()) return starpower + "/" + starpowercap;
		else return null;}

	private static final String POWER = "starpower";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(POWER, starpower);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (starpowercap > 0) starpower = Math.min(starpowercap, bundle.getInt(POWER));
		else starpower = bundle.getInt(POWER);
	}

	private void dispel(Char defender) {
		for (Buff b : defender.buffs()) {
			if (b.type == Buff.buffType.POSITIVE) {
				b.detach();
			}
		}
		defender.addImmune(Camouflage.class);
		if (defender instanceof MudrockZealot) ((MudrockZealot) defender).breakBarrier();
		if (defender instanceof BloodMagister) ((BloodMagister) defender).clearAttackPower();
		if (defender instanceof NewDM300) {
			if(((NewDM300) defender).DamageUP>0)defender.sprite.showStatus( CharSprite.NEGATIVE, Messages.get(NewDM300.class, "dispel"));
			((NewDM300) defender).DamageUP = 0;
		}
		if(defender instanceof Faust) ((Faust) defender).clearCharge();
		if(defender instanceof Succubus) ((Succubus) defender).clearASPlus();
		if(defender instanceof Raider) ((Raider) defender).clearASPlus();
		if(defender instanceof YogDzewa) {
			if(Statistics.spawnersAlive != 0)defender.sprite.showStatus( CharSprite.NEGATIVE, Messages.get(YogDzewa.class, "dispel"));
			Statistics.spawnersAlive = 0;
		}
		if(defender instanceof Mon3tr) ((Mon3tr) defender).clearSkill();
		if(defender instanceof Pompeii) {
			if(Statistics.coreAlive>0)defender.sprite.showStatus( CharSprite.NEGATIVE, Messages.get(Pompeii.class, "dispel"));
			Statistics.coreAlive = 0;
		}
		if(defender instanceof Brute){
			if(defender.HP <= 0) defender.die(null);
		}
	}

	public static class StarpowerBuff extends Buff {
		private ArrayList<OrbitingParticle> particles = new ArrayList<>();
		private int lastStarpower = -1;
		@Override
		public boolean act() {
			if (target == null || !target.isAlive()) {
				detach();
				return true;
			}
			Hero hero = (Hero) target;
			if (hero.belongings.weapon == null || !(hero.belongings.weapon instanceof BattleAxe)) {
				detach();
				return true;
			}
			BattleAxe axe = (BattleAxe) hero.belongings.weapon;
			int sp = axe.starpower;

			if (sp <= 0) {
				detach();
				return true;
			}
			if (sp != lastStarpower) {
				updateParticles(sp);
				lastStarpower = sp;
			}
			if (target.sprite != null) {
				for (OrbitingParticle p : particles) {
					if (p != null) p.visible = target.sprite.visible;
				}
			}
			spend(TICK);
			return true;
		}
		private void updateParticles(int count) {
			for (OrbitingParticle p : particles) {
				if (p != null) p.killAndErase();
			}
			particles.clear();
			CharSprite sprite = (CharSprite) target.sprite;
			for (int i = 0; i < count; i++) {
				float phaseOffset = (float) i / count * 2f * (float) Math.PI;
				particles.add(new OrbitingParticle(sprite, phaseOffset));
			}
		}

		@Override
		public void detach() {
			super.detach();
			for (OrbitingParticle p : particles) {
				if (p != null) p.killAndErase();
			}
			particles.clear();
			lastStarpower = -1;
		}
	}

	// 放在 BattleAxe 类内部
	public static class OrbitingParticle extends Image {

		private CharSprite target;
		private float phase;          // 固定相位偏移
		private float angle;          // 当前旋转角度（弧度）
		private float angularSpeed = 2.094f; // 120度/秒 => 2π/3 rad/s

		public OrbitingParticle(CharSprite target, float phaseOffset) {
			super(new Speck().image(Speck.STAR));
			scale.set(1f);
			color(0xAAAAFF);
			this.target = target;
			this.phase = phaseOffset;
			this.angle = 0f;
			if (target != null && target.parent != null) {
				target.parent.add(this);
				visible = target.visible;
			}
		}

		@Override
		public void update() {
			super.update();
			if (target == null || target.parent == null) {
				killAndErase();
				return;
			}
			angle += angularSpeed * Game.elapsed;
			float totalAngle = angle + phase;
			PointF center = target.center();
			float baseRadius = (target.width() + target.height()) / 4f + 4f;
			float radiusX = baseRadius * 1f;
			float radiusY = baseRadius * 0.4f;

			float px = center.x + (float) Math.cos(totalAngle) * radiusX - width() / 2f;
			float py = center.y + (float) Math.sin(totalAngle) * radiusY - height() / 2f;
			point(px, py);

			visible = target.visible;

			adjustDepth(center.y);
		}
		private void adjustDepth(float targetCenterY) {
			Group parent = target.parent;
			if (parent == null) return;

			int targetIdx = parent.indexOf(target);
			int myIdx = parent.indexOf(this);
			if (targetIdx < 0 || myIdx < 0) return;

			float myCenterY = this.y + height() / 2f;
			boolean isAbove = myCenterY < targetCenterY;

			if (isAbove) {
				if (myIdx > targetIdx) {
					parent.sendToBack(this);
				}
			} else {
				if (myIdx < targetIdx) {
					parent.bringToFront(this);
				}
			}
		}
		public OrbitingParticle point(float x, float y ) {
			this.x = x;
			this.y = y;
			return this;
		}
	}
}
