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

package com.shatteredpixel.shatteredpixeldungeon.levels;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.ROR;
import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.level;

import com.badlogic.gdx.utils.Pool;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Bones;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GoldenMimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.food.SmallRation;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.GuidePage;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.GoldenKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLevitation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfBlink;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.Builder;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.FigureEightBuilder;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.LoopBuilder;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection.ConnectionRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.MiniShopRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.LACNET2Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MagicGloemRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.PitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.PoolRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.PursuerRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.RoseRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.ShopRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.StatueRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.VaultRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.WeakFloorRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.BeachRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.EntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.ExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.FloodingRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.FloodingRoom2;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.GavialStanardRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.Vocan_1Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.Vocan_2Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.Vocan_3Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BlazingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BurningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ChillingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ExplosiveTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FrostTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WornDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public abstract class RegularLevel extends Level {
	
	protected ArrayList<Room> rooms;
	protected ArrayList<Room.Door> doors = new ArrayList<>();
	protected Builder builder;
	
	protected Room roomEntrance;
	protected Room roomExit;
	
	@Override
	protected boolean build() {
		
		builder = builder();
		
		ArrayList<Room> initRooms = initRooms();
		Random.shuffle(initRooms);
		
		do {
			for (Room r : initRooms){
				r.neigbours.clear();
				r.connected.clear();
			}
			rooms = builder.build((ArrayList<Room>)initRooms.clone());
		} while (rooms == null);
		
		return painter().paint(this, rooms);
		
	}
	
	protected ArrayList<Room> initRooms() {
		ArrayList<Room> initRooms = new ArrayList<>();
		initRooms.add ( roomEntrance = new EntranceRoom());
		initRooms.add( roomExit = new ExitRoom());

		//force max standard rooms and multiple by 1.5x for large levels
		int standards = standardRooms(feeling == Feeling.LARGE);
		if (feeling == Feeling.LARGE){
			standards = (int)Math.ceil(standards * 1.5f);
		}
		for (int i = 0; i < standards; i++) {
			StandardRoom s;
			do {
				s = StandardRoom.createRoom();
			} while (!s.setSizeCat( standards-i ));
			i += s.sizeCat.roomValue-1;
			initRooms.add(s);
		}
		
		if (Dungeon.shopOnLevel())
			initRooms.add(new ShopRoom());
		else if (Random.Int(80) == 0) {
			initRooms.add(new MiniShopRoom());
		}

		if (Dungeon.depth == 1) initRooms.add(new VaultRoom());
		if (Dungeon.depth == 13) initRooms.add(new RoseRoom());
		if (Dungeon.depth == 14) initRooms.add(new MagicGloemRoom());
		if (Dungeon.depth == 24) initRooms.add(new PursuerRoom());

		if (Dungeon.DLC == Dungeon.SARGON) {
			if (Dungeon.depth == 33) initRooms.add(new LACNET2Room());
			initRooms.add(new GavialStanardRoom());
			initRooms.add(new GavialStanardRoom());
		}
		else if (Dungeon.depth > 30){
			if (Dungeon.depth != 35 & Dungeon.depth != 40){
				if (Dungeon.depth < 35) {
					initRooms.add(new FloodingRoom());
					initRooms.add(new FloodingRoom2());
					initRooms.add(new BeachRoom());
				}
				else {
					initRooms.add(new Vocan_1Room());
					initRooms.add(new Vocan_2Room());
					initRooms.add(new Vocan_3Room());
				}
			}
		}

		//force max special rooms and add one more for large levels
		int specials = specialRooms(feeling == Feeling.LARGE);
		if (feeling == Feeling.LARGE){
			specials++;
		}
		SpecialRoom.initForFloor();
		for (int i = 0; i < specials; i++) {
			SpecialRoom s = SpecialRoom.createRoom();
			if (s instanceof PitRoom) specials++;
			if(!(s instanceof VaultRoom && Dungeon.depth==1)) initRooms.add(s);//取消一层双水晶
		}
		
		int secrets = SecretRoom.secretsForFloor(Dungeon.depth);
		//one additional secret for secret levels
		if (feeling == Feeling.SECRETS) secrets++;
		for (int i = 0; i < secrets; i++) {
			initRooms.add(SecretRoom.createRoom());
		}
		
		return initRooms;
	}
	
	protected int standardRooms(boolean forceMax){
		return 0;
	}
	
	protected int specialRooms(boolean forceMax){
		return 0;
	}
	
	protected Builder builder(){
		if (Random.Int(2) == 0){
			return new LoopBuilder()
					.setLoopShape( 2 ,
							Random.Float(0f, 0.65f),
							Random.Float(0f, 0.50f));
		} else {
			return new FigureEightBuilder()
					.setLoopShape( 2 ,
							Random.Float(0.3f, 0.8f),
							0f);
		}

	}
	
	protected abstract Painter painter();
	
	protected int nTraps() {
		return Random.NormalIntRange( 2, 3 + (Dungeon.depth/5) );
	}
	
	protected Class<?>[] trapClasses(){
		return new Class<?>[]{WornDartTrap.class};
	}

	protected float[] trapChances() {
		return new float[]{1};
	}
	
	@Override
	public int nMobs() {
		if (Dungeon.depth <= 1) return 0;

		int mobs = 3 + Dungeon.depth % 5 + Random.Int(3);
		if (feeling == Feeling.LARGE){
			mobs = (int)Math.ceil(mobs * 1.33f);
		}
		return mobs;
	}
	
	@Override
	protected void createMobs() {
		//on floor 1, 8 pre-set mobs are created so the player can get level 2.
		int mobsToSpawn = Dungeon.depth == 1 ? 8 : nMobs();

		ArrayList<Room> stdRooms = new ArrayList<>();
		for (Room room : rooms) {
			if (room instanceof StandardRoom && room != roomEntrance) {
				for (int i = 0; i < ((StandardRoom) room).sizeCat.roomValue; i++) {
					stdRooms.add(room);
				}
			}
		}
		Random.shuffle(stdRooms);
		Iterator<Room> stdRoomIter = stdRooms.iterator();

		while (mobsToSpawn > 0) {
			Mob mob = createMob();
			Room roomToSpawn;
			
			if (!stdRoomIter.hasNext()) {
				stdRoomIter = stdRooms.iterator();
			}
			roomToSpawn = stdRoomIter.next();

			int tries = 30;
			do {
				mob.pos = pointToCell(roomToSpawn.random());
				tries--;
			} while (tries >= 0 && (findMob(mob.pos) != null || !passable[mob.pos] || solid[mob.pos] || mob.pos == exit
					|| (!openSpace[mob.pos] && mob.properties().contains(Char.Property.LARGE))));

			if (tries >= 0) {
				mobsToSpawn--;
				mobs.add(mob);

				//add a second mob to this room
				if (mobsToSpawn > 0 && Random.Int(4) == 0){
					mob = createMob();

					tries = 30;
					do {
						mob.pos = pointToCell(roomToSpawn.random());
						tries--;
					} while (tries >= 0 && (findMob(mob.pos) != null || !passable[mob.pos] || solid[mob.pos] || mob.pos == exit
							|| (!openSpace[mob.pos] && mob.properties().contains(Char.Property.LARGE))));

					if (tries >= 0) {
						mobsToSpawn--;
						mobs.add(mob);
					}
				}
			}
		}

		for (Mob m : mobs){
			if (map[m.pos] == Terrain.HIGH_GRASS || map[m.pos] == Terrain.FURROWED_GRASS) {
				map[m.pos] = Terrain.GRASS;
				losBlocking[m.pos] = false;
			}
		}

	}

	@Override
	public int randomRespawnCell( Char ch ) {
		int count = 0;
		int cell = -1;

		while (true) {

			if (++count > 30) {
				return -1;
			}

			Room room = randomRoom( StandardRoom.class );
			if (room == null || room == roomEntrance) {
				continue;
			}

			cell = pointToCell(room.random(1));
			if (!heroFOV[cell]
					&& Actor.findChar( cell ) == null
					&& passable[cell]
					&& !solid[cell]
					&& (!Char.hasProp(ch, Char.Property.LARGE) || openSpace[cell])
					&& room.canPlaceCharacter(cellToPoint(cell), this)
					&& cell != exit) {
				return cell;
			}

		}
	}
	
	@Override
	public int randomDestination( Char ch ) {
		
		int count = 0;
		int cell = -1;
		
		while (true) {
			
			if (++count > 30) {
				return -1;
			}
			
			Room room = Random.element( rooms );
			if (room == null) {
				continue;
			}
			
			cell = pointToCell(room.random());
			if (passable[cell] && (!Char.hasProp(ch, Char.Property.LARGE) || openSpace[cell])) {
				return cell;
			}
			
		}
	}
	
	@Override
	protected void createItems() {
		
		// drops 3/4/5 items 60%/30%/10% of the time
		int nItems = 3 + Random.chances(new float[]{6, 3, 1});

		if (feeling == Feeling.LARGE){
			nItems += 2;
		}
		
		for (int i=0; i < nItems; i++) {

			Item toDrop = Generator.random();
			if (toDrop == null) continue;

			int cell = randomDropCell();
			if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
				map[cell] = Terrain.GRASS;
				losBlocking[cell] = false;
			}

			Heap.Type type = null;
			switch (Random.Int( 20 )) {
			case 0:
				type = Heap.Type.SKELETON;
				break;
			case 1:
			case 2:
			case 3:
			case 4:
				type = Heap.Type.CHEST;
				break;
			case 5:
				if (Dungeon.depth > 1 && findMob(cell) == null){
					mobs.add(Mimic.spawnAt(cell, toDrop));
					continue;
				}
				type = Heap.Type.CHEST;
				break;
			default:
				type = Heap.Type.HEAP;
				break;
			}

			if ((toDrop instanceof Artifact && Random.Int(2) == 0) ||
					(toDrop.isUpgradable() && Random.Int(4 - toDrop.level()) == 0)){

				if (Dungeon.depth > 1 && Random.Int(10) == 0 && findMob(cell) == null){
					mobs.add(Mimic.spawnAt(cell, toDrop, GoldenMimic.class));
				} else {
					Heap dropped = drop(toDrop, cell);
					if (heaps.get(cell) == dropped) {
						dropped.type = Heap.Type.LOCKED_CHEST;
						addItemToSpawn(new GoldenKey(Dungeon.depth));
					}
				}
			} else {
				Heap dropped = drop( toDrop, cell );
				dropped.type = type;
				if (type == Heap.Type.SKELETON){
					dropped.setHauntedIfCursed();
				}
			}
			
		}

		for (Item item : itemsToSpawn) {
			if(Dungeon.DLC == ROR && Dungeon.depth > 30 &&
					(item instanceof IronKey || item instanceof PotionOfLiquidFlame || item instanceof StoneOfBlast)
			){ item = Random.Int(2)==0? new PotionOfLevitation(): new StoneOfBlink();}
			int cell = randomDropCell();
			drop( item, cell ).type = Heap.Type.HEAP;
			if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
				map[cell] = Terrain.GRASS;
				losBlocking[cell] = false;
			}
		}

		//use a separate generator for this to prevent held items, meta progress, and talents from affecting levelgen
		Random.pushGenerator( Dungeon.seedCurDepth() );

		Item item = Bones.get();
		if (item != null) {
			int cell = randomDropCell();
			if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
				map[cell] = Terrain.GRASS;
				losBlocking[cell] = false;
			}
			drop( item, cell ).setHauntedIfCursed().type = Heap.Type.REMAINS;
		}

		DriedRose rose = Dungeon.hero.belongings.getItem( DriedRose.class );
		if (rose != null && rose.isIdentified() && !rose.cursed){
			//aim to drop 1 petal every 2 floors
			int petalsNeeded = (int) Math.ceil((float)((Dungeon.depth / 2) - rose.droppedPetals) / 3);

			for (int i=1; i <= petalsNeeded; i++) {
				//the player may miss a single petal and still max their rose.
				if (rose.droppedPetals < 11) {
					item = new DriedRose.Petal();
					int cell = randomDropCell();
					drop( item, cell ).type = Heap.Type.HEAP;
					if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
						map[cell] = Terrain.GRASS;
						losBlocking[cell] = false;
					}
					rose.droppedPetals++;
				}
			}
		}

		//cached rations try to drop in a special room on floors 2/3/4/6/7/8, to a max of 4/6
		if (Dungeon.hero.hasTalent(Talent.CACHED_RATIONS)){
			Talent.CachedRationsDropped dropped = Buff.affect(Dungeon.hero, Talent.CachedRationsDropped.class);
			if (dropped.count() < 2 + 2*Dungeon.hero.pointsInTalent(Talent.CACHED_RATIONS)){
				int cell;
				int tries = 100;
				do {
					cell = randomDropCell(SpecialRoom.class);
				} while (tries-- > 0 && (room(cell) instanceof SecretRoom || room(cell) instanceof ShopRoom));
				if (!(room(cell) instanceof SecretRoom || room(cell) instanceof ShopRoom) && cell != -1) {
					if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
						map[cell] = Terrain.GRASS;
						losBlocking[cell] = false;
					}
					drop(new SmallRation(), cell).type = Heap.Type.CHEST;
					dropped.countUp(1);
				}
			}
		}

		//guide pages
		Collection<String> allPages = Document.ADVENTURERS_GUIDE.pages();
		ArrayList<String> missingPages = new ArrayList<>();
		for ( String page : allPages){
			if (!Document.ADVENTURERS_GUIDE.hasPage(page)){
				missingPages.add(page);
			}
		}

		//a total of 8 pages drop randomly, 2 pages are specially dropped
		missingPages.remove(Document.GUIDE_INTRO_PAGE);
		missingPages.remove(Document.GUIDE_SEARCH_PAGE);

		//chance to find a page scales with pages missing and depth
		float dropChance = (missingPages.size() + Dungeon.depth - 1) / (float)(allPages.size() - 2);
		if (!missingPages.isEmpty() && Random.Float() < dropChance){
			GuidePage p = new GuidePage();
			p.page(missingPages.get(0));
			int cell = randomDropCell();
			if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
				map[cell] = Terrain.GRASS;
				losBlocking[cell] = false;
			}
			drop( p, cell );
		}

		Random.popGenerator();

	}

	@Override
	protected void collectMazeDoors() {
		doors.clear();
		for(Room r : rooms){
			for(Room ar : r.connected.keySet()){
				if(r.connected.get(ar)!=null){
					doors.add(r.connected.get(ar));
				}
			}
		}
	}
	@Override
	public void removeRORWalls(){
		if((!(Dungeon.DLC == ROR)) || !(Dungeon.depth > 30)) return;
		for(Room r : rooms) {
			Painter.replaceWithChances(this, r, Terrain.EMPTY, Terrain.STATUE, 0.1f);
			Painter.replaceWithChances(this, r, Terrain.WALL, Terrain.STATUE, 0.05f);
			Painter.replaceWithChances(this, r, Terrain.WALL, Terrain.EMPTY, 0.4f);
			Painter.replace( this, r, Terrain.WALL , Terrain.CHASM );
			Painter.replace( this, r, Terrain.WALL_DECO , Terrain.STATUE );
			Painter.replace( this, r, Terrain.LOCKED_DOOR , Terrain.STATUE );
			Painter.replace( this, r, Terrain.BOOKSHELF , Terrain.STATUE );
			Painter.replace( this, r, Terrain.SECRET_DOOR , Terrain.STATUE );
			Painter.replaceWithChances( this, r, Terrain.STATUE , Terrain.WALL, 0.75f );
			Painter.replace( this, r, Terrain.DOOR , Terrain.EMPTY );
			if(r instanceof ConnectionRoom) Painter.replaceWithChances(this, r, Terrain.CHASM, Terrain.EMPTY, 0.75f);
		}
	}
	
	public ArrayList<Room> rooms() {
		return new ArrayList<>(rooms);
	}
	
	//FIXME pit rooms shouldn't be problematic enough to warrant this
	public boolean hasPitRoom(){
		for (Room r : rooms) {
			if (r instanceof PitRoom) {
				return true;
			}
		}
		return false;
	}
	
	protected Room randomRoom( Class<?extends Room> type ) {
		Random.shuffle( rooms );
		for (Room r : rooms) {
			if (type.isInstance(r)) {
				return r;
			}
		}
		return null;
	}
	
	public Room room( int pos ) {
		for (Room room : rooms) {
			if (room.inside( cellToPoint(pos) )) {
				return room;
			}
		}
		
		return null;
	}

	protected int randomDropCell(){
		return randomDropCell(StandardRoom.class);
	}
	
	protected int randomDropCell( Class<?extends Room> roomType ) {
		int tries = 100;
		while (tries-- > 0) {
			Room room = randomRoom( roomType );
			if (room == null){
				return -1;
			}
			if (room != roomEntrance) {
				int pos = pointToCell(room.random());
				if (passable[pos] && !solid[pos]
						&& pos != exit
						&& heaps.get(pos) == null
						&& findMob(pos) == null) {
					
					Trap t = traps.get(pos);
					
					//items cannot spawn on traps which destroy items
					if (t == null ||
							! (t instanceof BurningTrap || t instanceof BlazingTrap
							|| t instanceof ChillingTrap || t instanceof FrostTrap
							|| t instanceof ExplosiveTrap || t instanceof DisintegrationTrap)) {
						
						return pos;
					}
				}
			}
		}
		return -1;
	}
	
	@Override
	public int fallCell( boolean fallIntoPit ) {
		if (fallIntoPit) {
			for (Room room : rooms) {
				if (room instanceof PitRoom) {
					int result;
					do {
						result = pointToCell(room.random());
					} while (traps.get(result) != null
							|| findMob(result) != null
							|| heaps.get(result) != null);
					return result;
				}
			}
		}
		
		return super.fallCell( false );
	}

	private boolean notAgain = false;
	@Override
	public void occupyCell( Char ch ){
		if ((map[ch.pos] == Terrain.DOOR || map[ch.pos] == Terrain.OPEN_DOOR) && ch instanceof Hero && !notAgain){
			if(feeling == Feeling.MAZE){
				if(Random.Int(10)==0){
					teleportMazeFeeling((Hero) ch);
				}
			}
		}
		super.occupyCell(ch);
	}
	public void teleportMazeFeeling( Hero hero ) {
		boolean failed = false;
		if (Dungeon.bossLevel() || (Dungeon.depth >= 27 && Dungeon.depth <= 30)){
			return;
		}
		int count = 20;
		int pos;
		do {
			int rnd = Random.Int(doors.size());
			pos = pointToCell(doors.get(rnd));
			if (count-- <= 0) {
				failed = true;
				break;
			}
		} while (!(map[pos] == Terrain.DOOR || map[pos] == Terrain.OPEN_DOOR) || pos == Dungeon.hero.pos);
		if (pos != -1 && !failed){
			notAgain = true;
			hero.sprite.interruptMotion();
			hero.interrupt();
			Dungeon.hero.pos = pos;
			Dungeon.level.occupyCell(Dungeon.hero);
			Dungeon.observe();
			Dungeon.hero.checkVisibleMobs();
			Dungeon.hero.sprite.place( Dungeon.hero.pos );
			Dungeon.hero.sprite.turnTo( Dungeon.hero.pos, pos);
			Camera.main.panTo(hero.sprite.center(), 100f);
			notAgain = false;
			Dungeon.observe();
			GameScene.updateFog();
			Camera.main.shake(2, 0.35f);
		}
	}
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( "rooms", rooms );
		bundle.put( "doors", doors );
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		
		rooms = new ArrayList<>( (Collection<Room>) ((Collection<?>) bundle.getCollection( "rooms" )) );
		for (Room r : rooms) {
			r.onLevelLoad( this );
			if (r instanceof EntranceRoom ){
				roomEntrance = r;
			} else if (r instanceof ExitRoom ){
				roomExit = r;
			}
		}
		doors = new ArrayList<>( (Collection<Room.Door>) ((Collection<?>) bundle.getCollection( "doors" )) );
	}
	
}
