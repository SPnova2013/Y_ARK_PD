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

package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Transmuting;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.AlchemicalCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.Brew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.Elixir;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.ROR2item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

public class ScrollOfTransmutation extends InventoryScroll {
	
	{
		icon = ItemSpriteSheet.Icons.SCROLL_TRANSMUTE;
		mode = WndBag.Mode.TRANMSUTABLE;
		
		bones = true;
	}
	
	public static boolean canTransmute(Item item){
		return item instanceof MeleeWeapon ||
				(item instanceof MissileWeapon && !(item instanceof Dart)) ||
				(item instanceof Potion && !(item instanceof Elixir || item instanceof Brew || item instanceof AlchemicalCatalyst)) ||
				item instanceof Scroll ||
				item instanceof Ring ||
				item instanceof Wand ||
				item instanceof Plant.Seed ||
				item instanceof Runestone ||
				item instanceof Artifact ||
				item instanceof ROR2item;
	}
	
	@Override
	protected void onItemSelected(Item item) {
		
		Item result;
		
		if (item instanceof MagesStaff) {
			changeStaff( (MagesStaff)item );
			return;
		} else if (item instanceof MeleeWeapon || item instanceof MissileWeapon) {
			changeWeapon( (Weapon)item );
			return;
		} else if (item instanceof Scroll) {
			result = changeScroll( (Scroll)item );
		} else if (item instanceof Potion) {
			result = changePotion( (Potion)item );
		} else if (item instanceof Ring) {
			changeRing( (Ring)item );
			return;
		} else if (item instanceof Wand) {
			changeWand( (Wand)item );
			return;
		} else if (item instanceof Plant.Seed) {
			result = changeSeed((Plant.Seed) item);
		} else if (item instanceof Runestone) {
			result = changeStone((Runestone) item);
		} else if (item instanceof Artifact) {
			changeArtifact( (Artifact)item );
			return;
		}
		else if (item instanceof ROR2item) {
			changeROR2item( (ROR2item)item );
			return;
		}
		else {
			result = null;
		}
		
		if (result == null){
			//This shouldn't ever trigger
			GLog.n( Messages.get(this, "nothing") );
			curItem.collect( curUser.belongings.backpack );
		} else {
			if (item.isEquipped(Dungeon.hero)){
				item.cursed = false; //to allow it to be unequipped
				((EquipableItem)item).doUnequip(Dungeon.hero, false);
				((EquipableItem)result).doEquip(Dungeon.hero);
				Dungeon.hero.spend(-Dungeon.hero.cooldown());
			} else {
				item.detach(Dungeon.hero.belongings.backpack);
				if (!result.collect()){
					Dungeon.level.drop(result, curUser.pos).sprite.drop();
				}
			}
			if (result.isIdentified()){
				Catalog.setSeen(result.getClass());
			}
			Transmuting.show(curUser, item, result);
			curUser.sprite.emitter().start(Speck.factory(Speck.CHANGE), 0.2f, 10);
			GLog.p( Messages.get(this, "morph") );
		}
		
	}
	
	private void changeStaff( MagesStaff staff ){
		Class<?extends Wand> wandClass = staff.wandClass();
		
		if (wandClass == null){
			return;
		} else {
			final Wand[] wands = new Wand[3];
			int safecount = 0;
			do{
				wands[0] = (Wand) Generator.random(Generator.Category.WAND);
				safecount++;
			}while ((Challenges.isItemBlocked(wands[0]) || wands[0].getClass() == wandClass) && safecount<=10);
			safecount = 0;
			do{
				wands[1] = (Wand) Generator.random(Generator.Category.WAND);
				safecount++;
			}while ((Challenges.isItemBlocked(wands[1]) || wands[1].getClass() == wandClass || wands[1].getClass() == wands[0].getClass() ) && safecount<=10);
			safecount = 0;
			do{
				wands[2] = (Wand) Generator.random(Generator.Category.WAND);
				safecount++;
			}while ((Challenges.isItemBlocked(wands[2]) || wands[2].getClass() == wandClass || wands[2].getClass() == wands[0].getClass() || wands[2].getClass() == wands[1].getClass()) && safecount<=10);
			GameScene.show(new WndOptions(Messages.titleCase(ScrollOfTransmutation.this.name()),
					Messages.get(this, "inv_title") ,
					wands[0].name(),
					wands[1].name(),
					wands[2].name()) {

				@Override
				protected void onSelect(int index) {
					Wand n = wands[index];
					n.level(0);
					n.identify();
					staff.imbueWand(n, null);
				}
				@Override
				public void onBackPressed() {
				}
			});
		}
	}
	
	private void changeWeapon( Weapon w ) {
		Generator.Category c;
		if (w instanceof MeleeWeapon) {
			c = Generator.wepTiers[((MeleeWeapon)w).tier - 1];
		} else {
			c = Generator.misTiers[((MissileWeapon)w).tier - 1];
		}

		final Weapon[] weapons = new Weapon[3];
		int safecount = 0;

		do{
			weapons[0] = (Weapon) Reflection.newInstance(c.classes[Random.chances(c.probs)]);
			safecount++;
		}while ((Challenges.isItemBlocked(weapons[0]) || weapons[0].getClass() == w.getClass()) && safecount<=10);
		safecount = 0;
		do{
			weapons[1] = (Weapon) Reflection.newInstance(c.classes[Random.chances(c.probs)]);
			safecount++;
		}while ((Challenges.isItemBlocked(weapons[1]) || weapons[1].getClass() == w.getClass() || weapons[1].getClass() == weapons[0].getClass() ) && safecount<=10);
		safecount = 0;
		do{
			weapons[2] = (Weapon) Reflection.newInstance(c.classes[Random.chances(c.probs)]);
			safecount++;
		}while ((Challenges.isItemBlocked(weapons[2]) || weapons[2].getClass() == w.getClass() || weapons[2].getClass() == weapons[0].getClass() || weapons[2].getClass() == weapons[1].getClass()) && safecount<=10);
		GameScene.show(new WndOptions(Messages.titleCase(ScrollOfTransmutation.this.name()),
				Messages.get(this, "inv_title") ,
				weapons[0].name(),
				weapons[1].name(),
				weapons[2].name()) {

			@Override
			protected void onSelect(int index) {
					Weapon n = weapons[index];
					n.level(0);

					int level = w.level();
					if (w.curseInfusionBonus) {level--;}
					if (level > 0) {
						n.upgrade(level);
					} else if (level < 0) {
						n.degrade(-level);
					}
					n.enchantment = w.enchantment;
					n.curseInfusionBonus = w.curseInfusionBonus;
					n.levelKnown = w.levelKnown;
					n.cursedKnown = w.cursedKnown;
					n.cursed = w.cursed;
					n.augment = w.augment;
					n.chimeras = w.chimeras;
					if (w.isEquipped(Dungeon.hero)) {
						w.cursed = false; //to allow it to be unequipped
						((EquipableItem) w).doUnequip(Dungeon.hero, false);
						((EquipableItem) n).doEquip(Dungeon.hero);
						Dungeon.hero.spend(-Dungeon.hero.cooldown());
					} else {
						w.detach(Dungeon.hero.belongings.backpack);
						if (!n.collect()) {
							Dungeon.level.drop(n, curUser.pos).sprite.drop();
						}

						n.identify();
						Sample.INSTANCE.play(Assets.Sounds.EVOKE);
					}
			}

			@Override
			public void onBackPressed() {
			}
		});
	}
	
	private void changeRing( Ring r ) {
		final Ring[] rings = new Ring[3];
		int safecount = 0;
		do{
			rings[0] = (Ring) Generator.random(Generator.Category.RING);
			safecount++;
		}while ((Challenges.isItemBlocked(rings[0]) || rings[0].getClass() == r.getClass()) && safecount<=10);
		safecount = 0;
		do{
			rings[1] = (Ring) Generator.random(Generator.Category.RING);
			safecount++;
		}while ((Challenges.isItemBlocked(rings[1]) || rings[1].getClass() == r.getClass() || rings[1].getClass() == rings[0].getClass() ) && safecount<=10);
		safecount = 0;
		do{
			rings[2] = (Ring) Generator.random(Generator.Category.RING);
			safecount++;
		}while ((Challenges.isItemBlocked(rings[2]) || rings[2].getClass() == r.getClass() || rings[2].getClass() == rings[0].getClass() || rings[2].getClass() == rings[1].getClass()) && safecount<=10);
		GameScene.show(new WndOptions(Messages.titleCase(ScrollOfTransmutation.this.name()),
				Messages.get(this, "inv_title") ,
				rings[0].name(),
				rings[1].name(),
				rings[2].name()) {

			@Override
			protected void onSelect(int index) {
				Ring n = rings[index];
				n.level(0);

				int level = r.level();
				if (level > 0) {
					n.upgrade(level);
				} else if (level < 0) {
					n.degrade(-level);
				}
				n.levelKnown = r.levelKnown;
				n.cursedKnown = r.cursedKnown;
				n.cursed = r.cursed;
				if (r.isEquipped(Dungeon.hero)) {
					r.cursed = false; //to allow it to be unequipped
					((EquipableItem) r).doUnequip(Dungeon.hero, false);
					((EquipableItem) n).doEquip(Dungeon.hero);
					Dungeon.hero.spend(-Dungeon.hero.cooldown());
				} else {
					r.detach(Dungeon.hero.belongings.backpack);
					if (!n.collect()) {
						Dungeon.level.drop(n, curUser.pos).sprite.drop();
					}
					Sample.INSTANCE.play(Assets.Sounds.EVOKE);
				}
			}

			@Override
			public void onBackPressed() {
			}
		});

	}
	
	private void changeArtifact( Artifact a ) {
		final Artifact[] artifacts = new Artifact[3];
		int safecount = 0;
		Generator.Category cat = Generator.Category.ARTIFACT;
		do{
			int i = Random.chances(cat.probs);
			//if no artifacts are left, return null
			if (i == -1) {
				GLog.n( Messages.get(this, "nothing") );
				return;
			}
			artifacts[0] = (Artifact) Reflection.newInstance((Class<? extends Artifact>) cat.classes[i]).random();
			safecount++;
		}while ((Challenges.isItemBlocked(artifacts[0]) || artifacts[0].getClass() == a.getClass()) && safecount<=10);
		safecount = 0;
		do{
			int j = Random.chances(cat.probs);
			//if no artifacts are left, return null
			if (j == -1) {
				return;
			}
			artifacts[1] = (Artifact) Reflection.newInstance((Class<? extends Artifact>) cat.classes[j]).random();
			safecount++;
		}while ((Challenges.isItemBlocked(artifacts[1]) || artifacts[1].getClass() == a.getClass() || artifacts[1].getClass() == artifacts[0].getClass() ) && safecount<=10);
		safecount = 0;
		do{
			int k = Random.chances(cat.probs);
			//if no artifacts are left, return null
			if (k == -1) {
				return;
			}
			artifacts[2] = (Artifact) Reflection.newInstance((Class<? extends Artifact>) cat.classes[k]).random();
			safecount++;
		}while ((Challenges.isItemBlocked(artifacts[2]) || artifacts[2].getClass() == a.getClass() || artifacts[2].getClass() == artifacts[0].getClass() || artifacts[2].getClass() == artifacts[1].getClass()) && safecount<=10);
		GameScene.show(new WndOptions(Messages.titleCase(ScrollOfTransmutation.this.name()),
				Messages.get(this, "inv_title") ,
				artifacts[0].name(),
				artifacts[1].name(),
				artifacts[2].name()) {

			@Override
			protected void onSelect(int index) {
				Artifact n = artifacts[index];
				n.levelKnown = a.levelKnown;
				n.cursedKnown = a.cursedKnown;
				n.cursed = a.cursed;
				n.transferUpgrade(a.visiblyUpgraded());
				if (a.isEquipped(Dungeon.hero)) {
					a.cursed = false; //to allow it to be unequipped
					((EquipableItem) a).doUnequip(Dungeon.hero, false);
					((EquipableItem) n).doEquip(Dungeon.hero);
					Dungeon.hero.spend(-Dungeon.hero.cooldown());
				} else {
					a.detach(Dungeon.hero.belongings.backpack);
					if (!n.collect()) {
						Dungeon.level.drop(n, curUser.pos).sprite.drop();
					}
					n.identify();
					Sample.INSTANCE.play(Assets.Sounds.EVOKE);
				}
				Generator.removeArtifact(n.getClass());
			}

			@Override
			public void onBackPressed() {
			}
		});


	}
	
	private void changeWand( Wand w ) {
		final Wand[] wands = new Wand[3];
		int safecount = 0;
		do{
			wands[0] = (Wand) Generator.random(Generator.Category.WAND);
			safecount++;
		}while ((Challenges.isItemBlocked(wands[0]) || wands[0].getClass() == w.getClass()) && safecount<=10);
		safecount = 0;
		do{
			wands[1] = (Wand) Generator.random(Generator.Category.WAND);
			safecount++;
		}while ((Challenges.isItemBlocked(wands[1]) || wands[1].getClass() == w.getClass() || wands[1].getClass() == wands[0].getClass() ) && safecount<=10);
		safecount = 0;
		do{
			wands[2] = (Wand) Generator.random(Generator.Category.WAND);
			safecount++;
		}while ((Challenges.isItemBlocked(wands[2]) || wands[2].getClass() == w.getClass() || wands[2].getClass() == wands[0].getClass() || wands[2].getClass() == wands[1].getClass()) && safecount<=10);
		GameScene.show(new WndOptions(Messages.titleCase(ScrollOfTransmutation.this.name()),
				Messages.get(this, "inv_title") ,
				wands[0].name(),
				wands[1].name(),
				wands[2].name()) {

			@Override
			protected void onSelect(int index) {
				Wand n = wands[index];
				n.level(0);

				int level = w.level();
				if (w.curseInfusionBonus) level--;
				if (level > 0) {
					n.upgrade(level);
				} else if (level < 0) {
					n.degrade(-level);
				}
				n.levelKnown = w.levelKnown;
				n.cursedKnown = w.cursedKnown;
				n.cursed = w.cursed;
				n.curseInfusionBonus = w.curseInfusionBonus;
				w.detach(Dungeon.hero.belongings.backpack);
				if (!n.collect()) {
					Dungeon.level.drop(n, curUser.pos).sprite.drop();
				}
				Sample.INSTANCE.play(Assets.Sounds.EVOKE);
			}


			@Override
			public void onBackPressed() {
			}
		});
	}

	private void changeROR2item( ROR2item a ) {
		final ROR2item[] ror2items = new ROR2item[3];
		int safecount = 0;
		Generator.Category cat = Generator.Category.ROR2ITEM;
		do{
			int i = Random.chances(cat.probs);
			//if no artifacts are left, return null
			if (i == -1) {
				GLog.n( Messages.get(this, "nothing") );
				return;
			}
			ror2items[0] = (ROR2item) Reflection.newInstance((Class<? extends ROR2item>) cat.classes[i]).random();
			safecount++;
		}while ((Challenges.isItemBlocked(ror2items[0]) || ror2items[0].getClass() == a.getClass()) && safecount<=10);
		safecount = 0;
		do{
			int j = Random.chances(cat.probs);
			//if no artifacts are left, return null
			if (j == -1) {
				return;
			}
			ror2items[1] = (ROR2item) Reflection.newInstance((Class<? extends ROR2item>) cat.classes[j]).random();
			safecount++;
		}while ((Challenges.isItemBlocked(ror2items[1]) || ror2items[1].getClass() == a.getClass() || ror2items[1].getClass() == ror2items[0].getClass() ) && safecount<=10);
		safecount = 0;
		do{
			int k = Random.chances(cat.probs);
			//if no artifacts are left, return null
			if (k == -1) {
				return;
			}
			ror2items[2] = (ROR2item) Reflection.newInstance((Class<? extends ROR2item>) cat.classes[k]).random();
			safecount++;
		}while ((Challenges.isItemBlocked(ror2items[2]) || ror2items[2].getClass() == a.getClass() || ror2items[2].getClass() == ror2items[0].getClass() || ror2items[2].getClass() == ror2items[1].getClass()) && safecount<=10);
		GameScene.show(new WndOptions(Messages.titleCase(ScrollOfTransmutation.this.name()),
				Messages.get(this, "inv_title") ,
				ror2items[0].name(),
				ror2items[1].name(),
				ror2items[2].name()) {

			@Override
			protected void onSelect(int index) {
				ROR2item n = ror2items[index];
				n.levelKnown = a.levelKnown;
				n.cursedKnown = a.cursedKnown;
				n.cursed = a.cursed;
				if (a.isEquipped(Dungeon.hero)) {
					a.cursed = false; //to allow it to be unequipped
					((EquipableItem) a).doUnequip(Dungeon.hero, false);
					((EquipableItem) n).doEquip(Dungeon.hero);
					Dungeon.hero.spend(-Dungeon.hero.cooldown());
				} else {
					a.detach(Dungeon.hero.belongings.backpack);
					if (!n.collect()) {
						Dungeon.level.drop(n, curUser.pos).sprite.drop();
					}
					n.identify();
					Sample.INSTANCE.play(Assets.Sounds.EVOKE);
				}
				Generator.removeR2i(n.getClass());
			}

			@Override
			public void onBackPressed() {
			}
		});


	}
	
	private Plant.Seed changeSeed( Plant.Seed s ) {
		
		Plant.Seed n;
		
		do {
			n = (Plant.Seed)Generator.randomUsingDefaults( Generator.Category.SEED );
		} while (n.getClass() == s.getClass());
		
		return n;
	}
	
	private Runestone changeStone( Runestone r ) {
		
		Runestone n;
		
		do {
			n = (Runestone) Generator.randomUsingDefaults( Generator.Category.STONE );
		} while (n.getClass() == r.getClass());
		
		return n;
	}
	
	private Scroll changeScroll( Scroll s ) {
		if (s instanceof ExoticScroll) {
			return Reflection.newInstance(ExoticScroll.exoToReg.get(s.getClass()));
		} else {
			return Reflection.newInstance(ExoticScroll.regToExo.get(s.getClass()));
		}
	}
	
	private Potion changePotion( Potion p ) {
		if	(p instanceof ExoticPotion) {
			return Reflection.newInstance(ExoticPotion.exoToReg.get(p.getClass()));
		} else {
			return Reflection.newInstance(ExoticPotion.regToExo.get(p.getClass()));
		}
	}
	
	@Override
	public int value() {
		return isKnown() ? 50 * quantity : super.value();
	}
}
