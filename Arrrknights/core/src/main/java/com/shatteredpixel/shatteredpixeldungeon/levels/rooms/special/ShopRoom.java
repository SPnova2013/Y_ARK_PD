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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.Bonk;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.MerchantsBeacon;
import com.shatteredpixel.shatteredpixeldungeon.items.OriginiumShard;
import com.shatteredpixel.shatteredpixeldungeon.items.PortableCover;
import com.shatteredpixel.shatteredpixeldungeon.items.RandomBox;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.Torch;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.LeatherArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.MailArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.PlateArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ScaleArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.EquipmentsBag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.PotionBandolier;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ScrollHolder;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.food.SmallRation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfDivineInspiration;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.AquaBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.OathofFire;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAugmentation;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Enfild;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.PurgatoryKnife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Ragesawblade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashMap;

public class ShopRoom extends SpecialRoom {

	private ArrayList<Item> itemsToSpawn;
	
	@Override
	public int minWidth() {
		return Math.max(8, (int)(Math.sqrt(itemCount())+4));
	}
	
	@Override
	public int minHeight() {
		return Math.max(8, (int)(Math.sqrt(itemCount())+4));
	}

	public int itemCount(){
		if (itemsToSpawn == null) itemsToSpawn = generateItems();
		return itemsToSpawn.size();
	}
	
	public void paint( Level level ) {
		
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.EMPTY_SP );

		placeShopkeeper( level );

		placeItems( level );
		
		for (Door door : connected.values()) {
			door.set( Door.Type.REGULAR );
		}

	}

	protected void placeShopkeeper( Level level ) {

		int pos = level.pointToCell(center());

		Mob shopkeeper = new Shopkeeper();
		shopkeeper.pos = pos;
		level.mobs.add( shopkeeper );

	}

	protected void placeItems( Level level ){

		if (itemsToSpawn == null){
			itemsToSpawn = generateItems();
		}

		Point itemPlacement = new Point(entrance());
		if (itemPlacement.y == top){
			itemPlacement.y++;
		} else if (itemPlacement.y == bottom) {
			itemPlacement.y--;
		} else if (itemPlacement.x == left){
			itemPlacement.x++;
		} else {
			itemPlacement.x--;
		}
		int maxAttempts = 100;
		for (Item item : itemsToSpawn) {

			if (itemPlacement.x == left+1 && itemPlacement.y != top+1){
				itemPlacement.y--;
			} else if (itemPlacement.y == top+1 && itemPlacement.x != right-1){
				itemPlacement.x++;
			} else if (itemPlacement.x == right-1 && itemPlacement.y != bottom-1){
				itemPlacement.y++;
			} else {
				itemPlacement.x--;
			}

			int cell = level.pointToCell(itemPlacement);

			if (level.heaps.get( cell ) != null) {
				int attempts = 0;
				do {
					cell = level.pointToCell(random());
					if (++attempts > maxAttempts) {
						break;
					}
				} while (level.heaps.get( cell ) != null || level.findMob( cell ) != null);
			}

			level.drop( item, cell ).type = Heap.Type.FOR_SALE;
		}

	}
	
	protected static ArrayList<Item> generateItems() {

		ArrayList<Item> itemsToSpawn = new ArrayList<>();

		MeleeWeapon w;
		switch (Dungeon.depth) {
		case 6: default:
			if (Random.Int(5) < 4) { w = (MeleeWeapon) Generator.random(Generator.wepTiers[1]); }
	     	else w = new Enfild();
			itemsToSpawn.add( Generator.random(Generator.misTiers[1]).quantity(2).identify() );
            itemsToSpawn.add( new LeatherArmor().inscribe().upgrade(2).identify() );
			if (Dungeon.isChallenged(Challenges.NO_HERBALISM)) itemsToSpawn.add( new StoneOfAugmentation() );
			break;
			
		case 11:
			w = (MeleeWeapon) Generator.random(Generator.wepTiers[2]);
			itemsToSpawn.add(new Ragesawblade().quantity(1).identify());
			itemsToSpawn.add(new PurgatoryKnife().quantity(1).identify());
			itemsToSpawn.add( new MailArmor().inscribe().upgrade(2).identify() );
			break;
			
		case 16:
			w = (MeleeWeapon) Generator.random(Generator.wepTiers[3]);
			itemsToSpawn.add( Generator.random(Generator.misTiers[3]).quantity(2).identify() );
			itemsToSpawn.add( new ScaleArmor().inscribe().upgrade(2).identify() );
			break;

		case 20: case 21:
			w = (MeleeWeapon) Generator.random(Generator.wepTiers[4]);
			itemsToSpawn.add( Generator.random(Generator.misTiers[4]).quantity(2).identify() );
			itemsToSpawn.add( new PlateArmor().inscribe().upgrade(2).identify() );
			itemsToSpawn.add( new Torch() );
			itemsToSpawn.add( new Torch() );
			if (!Dungeon.isChallenged(Challenges.NO_HERBALISM)) itemsToSpawn.add( new Torch() );
			break;

		case 31:
			if (Dungeon.DLC == Dungeon.SARGON) {
				w = (MeleeWeapon) Generator.random(Generator.wepTiers[4]);
				itemsToSpawn.add(Generator.random(Generator.misTiers[4]).quantity(2).identify());
				itemsToSpawn.add(new PlateArmor().inscribe().upgrade(2).identify());
				itemsToSpawn.add(new PotionOfDivineInspiration());
				break;
			}
			else {
				w = (MeleeWeapon) Generator.random(Generator.wepTiers[4]);
				itemsToSpawn.add(Generator.random(Generator.misTiers[4]).quantity(2).identify());
				itemsToSpawn.add(new PlateArmor().inscribe().upgrade(2).identify());
				itemsToSpawn.add(new ScrollOfUpgrade());
				itemsToSpawn.add(new ScrollOfUpgrade());
				break;
			}

			case 36:
				if (Dungeon.DLC == Dungeon.SARGON) {
					w = (MeleeWeapon) Generator.random(Generator.wepTiers[4]);
					itemsToSpawn.add(Generator.random(Generator.misTiers[4]).quantity(2).identify());
					itemsToSpawn.add(new PlateArmor().inscribe().upgrade(2).identify());
					itemsToSpawn.add(new PortableCover());
					itemsToSpawn.add(new PortableCover());
					break;
				}
				w = (MeleeWeapon) Generator.random(Generator.wepTiers[4]);
				itemsToSpawn.add( new Ankh() );
				itemsToSpawn.add( new OathofFire().quantity(4));
				itemsToSpawn.add( new AquaBlast().quantity(6));
				break;
		}
		w.enchant();
		w.cursed = false;
		w.level(2);
		w.levelKnown = true;
		w.identify();
		itemsToSpawn.add(w);
		
		itemsToSpawn.add( TippedDart.randomTipped(2) );

		itemsToSpawn.add( new MerchantsBeacon() );


		if (!Dungeon.isChallenged(Challenges.NO_HERBALISM) || Dungeon.depth != 6)itemsToSpawn.add(ChooseBag(Dungeon.hero.belongings));


		itemsToSpawn.add( new PotionOfHealing() );
		if (!Dungeon.isChallenged(Challenges.NO_HERBALISM)) itemsToSpawn.add( Generator.randomUsingDefaults( Generator.Category.POTION ) );
		if (!Dungeon.isChallenged(Challenges.NO_HERBALISM)) itemsToSpawn.add( Generator.randomUsingDefaults( Generator.Category.POTION ) );

		itemsToSpawn.add( new ScrollOfIdentify() );
		itemsToSpawn.add( new ScrollOfRemoveCurse() );
		if (!Dungeon.isChallenged(Challenges.NO_HERBALISM)) itemsToSpawn.add( new ScrollOfMagicMapping() );

			for (int i = 0; i < 2; i++)
				itemsToSpawn.add(Random.Int(2) == 0 ?
						Generator.randomUsingDefaults(Generator.Category.POTION) :
						Generator.randomUsingDefaults(Generator.Category.SCROLL));



		itemsToSpawn.add( new SmallRation() );
		itemsToSpawn.add( new SmallRation() );
		itemsToSpawn.add( new SmallRation() );

		itemsToSpawn.add( Generator.randomUsingDefaults( Generator.Category.ACCESSORIES ) );
		
		switch (Random.Int(7)){
			case 0:
				itemsToSpawn.add( new Bomb() );
				break;
			case 1:
			case 2:
				if (!Dungeon.isChallenged(Challenges.NO_HERBALISM)) itemsToSpawn.add( new Bomb.DoubleBomb() );
				break;
			case 3:
				itemsToSpawn.add( new Honeypot() );
				break;
			case 4:
				itemsToSpawn.add( new OriginiumShard() );
				break;
			case 5:
				itemsToSpawn.add( new RandomBox());
				break;
			case 6:
				if (!Dungeon.isChallenged(Challenges.NO_HERBALISM)) itemsToSpawn.add( new Bonk());
				break;
		}

		if (!Dungeon.isChallenged(Challenges.NO_HERBALISM)) itemsToSpawn.add( new Ankh() );
		if (!Dungeon.isChallenged(Challenges.NO_HERBALISM)) itemsToSpawn.add( new StoneOfAugmentation() );

		TimekeepersHourglass hourglass = Dungeon.hero.belongings.getItem(TimekeepersHourglass.class);
		if (hourglass != null && hourglass.isIdentified() && !hourglass.cursed){
			int bags = 0;
			//creates the given float percent of the remaining bags to be dropped.
			//this way players who get the hourglass late can still max it, usually.
			switch (Dungeon.depth) {
				case 6:
					bags = (int)Math.ceil(( 5-hourglass.sandBags) * 0.20f ); break;
				case 11:
					bags = (int)Math.ceil(( 5-hourglass.sandBags) * 0.25f ); break;
				case 16:
					bags = (int)Math.ceil(( 5-hourglass.sandBags) * 0.50f ); break;
				case 20: case 21:
					bags = (int)Math.ceil(( 5-hourglass.sandBags) * 0.80f ); break;
			}

			for(int i = 1; i <= bags; i++){
				itemsToSpawn.add( new TimekeepersHourglass.sandBag());
				hourglass.sandBags ++;
			}
		}

		Item rare;
		switch (Random.Int(10)){
			case 0:
				rare = Generator.random( Generator.Category.WAND );
				rare.level( 2 );
				break;
			case 1:
				rare = Generator.random(Generator.Category.RING);
				rare.level( 2 );
				break;
			case 2:
				rare = Generator.random( Generator.Category.ARTIFACT );
				break;
			default:
				rare = new Stylus();
		}
		rare.cursed = false;
		rare.cursedKnown = true;
		rare.levelKnown = true;
		if (!Dungeon.isChallenged(Challenges.NO_HERBALISM)) itemsToSpawn.add( rare );

		//hard limit is 63 items + 1 shopkeeper, as shops can't be bigger than 8x8=64 internally
		if (itemsToSpawn.size() > 63)
			throw new RuntimeException("Shop attempted to carry more than 63 items!");

		//use a new generator here to prevent items in shop stock affecting levelgen RNG (e.g. sandbags)
		Random.pushGenerator(Random.Long());
			Random.shuffle(itemsToSpawn);
		Random.popGenerator();

		return itemsToSpawn;
	}

	protected static Bag ChooseBag(Belongings pack){

		//generate a hashmap of all valid bags.
		HashMap<Bag, Integer> bags = new HashMap<>();
		if (!Dungeon.LimitedDrops.VELVET_POUCH.dropped()) bags.put(new VelvetPouch(), 1);
		if (!Dungeon.LimitedDrops.SCROLL_HOLDER.dropped()) bags.put(new ScrollHolder(), 0);
		if (!Dungeon.LimitedDrops.POTION_BANDOLIER.dropped()) bags.put(new PotionBandolier(), 0);
		if (!Dungeon.LimitedDrops.MAGICAL_HOLSTER.dropped()) bags.put(new MagicalHolster(), 0);
//		if (!Dungeon.LimitedDrops.EQUIPMENTS_BAG.dropped()) bags.put(new EquipmentsBag(), 0);

		if (bags.isEmpty()) return null;

		//count up items in the main bag
		for (Item item : pack.backpack.items) {
			for (Bag bag : bags.keySet()){
				if (bag.canHold(item)){
					bags.put(bag, bags.get(bag)+1);
				}
			}
		}

		//find which bag will result in most inventory savings, drop that.
		Bag bestBag = null;
		for (Bag bag : bags.keySet()){
			if (bestBag == null){
				bestBag = bag;
			} else if (bags.get(bag) > bags.get(bestBag)){
				bestBag = bag;
			}
		}

		if (bestBag instanceof VelvetPouch){
			Dungeon.LimitedDrops.VELVET_POUCH.drop();
		} else if (bestBag instanceof ScrollHolder){
			Dungeon.LimitedDrops.SCROLL_HOLDER.drop();
		} else if (bestBag instanceof PotionBandolier){
			Dungeon.LimitedDrops.POTION_BANDOLIER.drop();
		} else if (bestBag instanceof MagicalHolster){
			Dungeon.LimitedDrops.MAGICAL_HOLSTER.drop();
		}
//		else if (bestBag instanceof EquipmentsBag){
//			Dungeon.LimitedDrops.EQUIPMENTS_BAG.drop();
//		}

		return bestBag;

	}

}
