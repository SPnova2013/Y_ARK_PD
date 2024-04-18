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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.TomorrowRogueNight;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.NewGameItem.Closure_FoodBox;
import com.shatteredpixel.shatteredpixeldungeon.items.NewGameItem.Closure_HealingBox;
import com.shatteredpixel.shatteredpixeldungeon.items.NewGameItem.Closure_IdentifyBox;
import com.shatteredpixel.shatteredpixeldungeon.items.NewGameItem.Closure_PotionBox;
import com.shatteredpixel.shatteredpixeldungeon.items.NewGameItem.Closure_RingBox;
import com.shatteredpixel.shatteredpixeldungeon.items.NewGameItem.Closure_ScrollBox;
import com.shatteredpixel.shatteredpixeldungeon.items.NewGameItem.Closure_TransBox;
import com.shatteredpixel.shatteredpixeldungeon.items.NewGameItem.Closure_WandBox;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.MasterThievesArmband;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.watabou.utils.Random;

import java.io.IOException;

public class WndTradeItem extends WndInfoItem {

	private static final float GAP		= 2;
	private static final int BTN_HEIGHT	= 16;

	private WndBag owner;

	//selling
	public WndTradeItem( final Item item, WndBag owner ) {

		super(item);

		this.owner = owner;

		float pos = height;

		if (item.quantity() == 1) {

			RedButton btnSell = new RedButton( Messages.get(this, "sell", item.value()) ) {
				@Override
				protected void onClick() {
					sell( item );
					hide();
				}
			};
			btnSell.setRect( 0, pos + GAP, width, BTN_HEIGHT );
			add( btnSell );

			pos = btnSell.bottom();

		} else {

			int priceAll= item.value();
			RedButton btnSell1 = new RedButton( Messages.get(this, "sell_1", priceAll / item.quantity()) ) {
				@Override
				protected void onClick() {
					sellOne( item );
					hide();
				}
			};
			btnSell1.setRect( 0, pos + GAP, width, BTN_HEIGHT );
			add( btnSell1 );
			RedButton btnSellAll = new RedButton( Messages.get(this, "sell_all", priceAll ) ) {
				@Override
				protected void onClick() {
					sell( item );
					hide();
				}
			};
			btnSellAll.setRect( 0, btnSell1.bottom() + 1, width, BTN_HEIGHT );
			add( btnSellAll );

			pos = btnSellAll.bottom();

		}

		resize( width, (int)pos );
	}

	//buying
	public WndTradeItem( final Heap heap ) {

		super(heap);

		Item item = heap.peek();

		float pos = height;

		final int price = Shopkeeper.sellPrice(item);

		RedButton btnBuy = new RedButton(Messages.get(this, "buy", price)) {
			@Override
			protected void onClick() {
				hide();
				buy(heap);
			}
		};
		btnBuy.setRect(0, pos + GAP, width, BTN_HEIGHT);
		btnBuy.enable(price <= Dungeon.gold);
		add(btnBuy);

		pos = btnBuy.bottom();

		final MasterThievesArmband.Thievery thievery = Dungeon.hero.buff(MasterThievesArmband.Thievery.class);
		if (thievery != null && !thievery.isCursed()) {
			final float chance = thievery.stealChance(price);
			RedButton btnSteal = new RedButton(Messages.get(this, "steal", Math.min(100, (int) (chance * 100)))) {
				@Override
				protected void onClick() {
					if (thievery.steal(price)) {
						Hero hero = Dungeon.hero;
						Item item = heap.pickUp();
						hide();

						if (!item.doPickUp(hero)) {
							Dungeon.level.drop(item, heap.pos).sprite.drop();
						}
					} else {
						for (Mob mob : Dungeon.level.mobs) {
							if (mob instanceof Shopkeeper) {
								mob.yell(Messages.get(mob, "thief"));
								((Shopkeeper) mob).flee();
								break;
							}
						}
						hide();
					}
				}
			};
			btnSteal.setRect(0, pos + 1, width, BTN_HEIGHT);
			add(btnSteal);

			pos = btnSteal.bottom();

		} else if (Dungeon.depth == 20) {
			RedButton btnSteal = new RedButton(Messages.get(this, "steal", 33)) {
			@Override
			protected void onClick() {
				if (Random.Int(2) == 0) {
					Hero hero = Dungeon.hero;
					Item item = heap.pickUp();
					hide();

					if (!item.doPickUp(hero)) {
						Dungeon.level.drop(item, heap.pos).sprite.drop();
					}
				} else {
					for (Mob mob : Dungeon.level.mobs) {
						if (mob instanceof Shopkeeper) {
							mob.yell(Messages.get(mob, "thief"));
							((Shopkeeper) mob).flee();
							break;
						}
					}
					hide();
				}
			}
		};
		btnSteal.setRect(0, pos + 1, width, BTN_HEIGHT);
		add(btnSteal);

		pos = btnSteal.bottom();
	}

		resize(width, (int) pos);
	}

	public WndTradeItem( final Heap heap, boolean chack ) {

		super(heap);

		Item item = heap.peek();

		float pos = height;

		final int price = item.value();

		RedButton btnBuy = new RedButton(Messages.get(this, "buy_sp", price)) {
			@Override
			protected void onClick() {
				hide();
				buy_sp(heap);
			}
		};
		btnBuy.setRect(0, pos + GAP, width, BTN_HEIGHT);
		btnBuy.enable(price <= SPDSettings.getSpecialcoin());
		add(btnBuy);

		pos = btnBuy.bottom();

		resize(width, (int) pos);
	}
	
	@Override
	public void hide() {
		
		super.hide();
		
		if (owner != null) {
			owner.hide();
			Shopkeeper.sell();
		}
	}
	
	private void sell( Item item ) {
		
		Hero hero = Dungeon.hero;
		
		if (item.isEquipped( hero ) && !((EquipableItem)item).doUnequip( hero, false )) {
			return;
		}
		item.detachAll( hero.belongings.backpack );

		//selling items in the sell interface doesn't spend time
		hero.spend(-hero.cooldown());

		new Gold( item.value() ).doPickUp( hero );
	}
	
	private void sellOne( Item item ) {
		
		if (item.quantity() <= 1) {
			sell( item );
		} else {
			
			Hero hero = Dungeon.hero;
			
			item = item.detach( hero.belongings.backpack );

			//selling items in the sell interface doesn't spend time
			hero.spend(-hero.cooldown());

			new Gold( item.value() ).doPickUp( hero );
		}
	}
	
	private void buy( Heap heap ) {
		
		Item item = heap.pickUp();
		if (item == null) return;
		
		int price = Shopkeeper.sellPrice( item );
		Dungeon.gold -= price;
		
		if (!item.doPickUp( Dungeon.hero )) {
			Dungeon.level.drop( item, heap.pos ).sprite.drop();
		}
	}

	private void buy_sp( Heap heap ) {

		Item item = heap.pickUp();
		if (item == null) return;

		int price = item.value();
		SPDSettings.addSpecialcoin(price * -1);

		if (!item.doPickUp( Dungeon.hero )) {
			Dungeon.level.drop( item, heap.pos ).sprite.drop();
		}

		if (item instanceof Closure_FoodBox) {
			Dungeon.buyFoodbox = true; }
		else if (item instanceof Closure_PotionBox) {
			Dungeon.buyPotionbox = true; }
		else if (item instanceof Closure_ScrollBox) {
			Dungeon.buyScrollbox = true; }
		else if (item instanceof Closure_IdentifyBox) {
			Dungeon.buyIdentifybox = true; }
		else if (item instanceof Closure_HealingBox) {
			Dungeon.buyHealbox = true; }
		else if (item instanceof Closure_WandBox) {
			Dungeon.buyWandbox = true; }
		else if (item instanceof Closure_TransBox) {
			Dungeon.buyTransbox = true; }
		else if (item instanceof Closure_RingBox) {
			Dungeon.buyRingbox = true; }

		try {
			Dungeon.saveAll();
		} catch (IOException e) {
			TomorrowRogueNight.reportException(e);
		}
	}
}
