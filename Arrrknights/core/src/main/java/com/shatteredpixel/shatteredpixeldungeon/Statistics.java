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

package com.shatteredpixel.shatteredpixeldungeon;

import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.Collections;

public class Statistics {
	
	public static int goldCollected;
	public static int deepestFloor;
	public static int enemiesSlain;
	public static int foodEaten;
	public static int potionsCooked;
	public static int piranhasKilled;
	public static int ankhsUsed;
	
	//used for hero unlock badges
	public static int upgradesUsed;
	public static int sneakAttacks;
	public static int thrownAssists;

	public static int spawnersAlive;
	public static int coreAlive;
	
	public static float duration;
	
	public static boolean qualifiedForNoKilling = false;
	public static boolean completedWithNoKilling = false;
	public static boolean ver0_3_2firstrun = false;
	
	public static boolean amuletObtained = false;

	public static ArrayList<Class> drawn = new ArrayList<>();
	
	public static void reset() {
		
		goldCollected	= 0;
		deepestFloor	= 0;
		enemiesSlain	= 0;
		foodEaten		= 0;
		potionsCooked	= 0;
		piranhasKilled	= 0;
		ankhsUsed		= 0;
		
		upgradesUsed    = 0;
		sneakAttacks    = 0;
		thrownAssists   = 0;

		spawnersAlive   = 0;
		coreAlive   = 0;
		
		duration	= 0;
		
		qualifiedForNoKilling = false;
		
		amuletObtained = false;

		drawn.clear();
	}
	
	private static final String GOLD		= "score";
	private static final String DEEPEST		= "maxDepth";
	private static final String SLAIN		= "enemiesSlain";
	private static final String FOOD		= "foodEaten";
	private static final String ALCHEMY		= "potionsCooked";
	private static final String PIRANHAS	= "priranhas";
	private static final String ANKHS		= "ankhsUsed";
	
	private static final String UPGRADES	= "upgradesUsed";
	private static final String SNEAKS		= "sneakAttacks";
	private static final String THROWN		= "thrownAssists";

	private static final String SPAWNERS	= "spawnersAlive";
	private static final String CORE	= "coreAlive";
	
	private static final String DURATION	= "duration";

	private static final String NO_KILLING_QUALIFIED	= "qualifiedForNoKilling";
	
	private static final String AMULET		= "amuletObtained";

	private static final String FIRSTRUN		= "ver0_3_2firstrun";
	private static final String DRAWN = "drawn";
	
	public static void storeInBundle( Bundle bundle ) {
		bundle.put( GOLD,		goldCollected );
		bundle.put( DEEPEST,	deepestFloor );
		bundle.put( SLAIN,		enemiesSlain );
		bundle.put( FOOD,		foodEaten );
		bundle.put( ALCHEMY,	potionsCooked );
		bundle.put( PIRANHAS,	piranhasKilled );
		bundle.put( ANKHS,		ankhsUsed );
		
		bundle.put( UPGRADES,   upgradesUsed );
		bundle.put( SNEAKS,		sneakAttacks );
		bundle.put( THROWN,		thrownAssists );

		bundle.put( SPAWNERS,	spawnersAlive );
		bundle.put( CORE,	coreAlive );
		
		bundle.put( DURATION,	duration );

		bundle.put(NO_KILLING_QUALIFIED, qualifiedForNoKilling);
		
		bundle.put( AMULET,		amuletObtained );

		bundle.put( FIRSTRUN,		ver0_3_2firstrun );
		bundle.put(DRAWN, 		drawn.toArray(new Class[Statistics.drawn.size()]));
	}
	
	public static void restoreFromBundle( Bundle bundle ) {
		goldCollected	= bundle.getInt( GOLD );
		deepestFloor	= bundle.getInt( DEEPEST );
		enemiesSlain	= bundle.getInt( SLAIN );
		foodEaten		= bundle.getInt( FOOD );
		potionsCooked	= bundle.getInt( ALCHEMY );
		piranhasKilled	= bundle.getInt( PIRANHAS );
		ankhsUsed		= bundle.getInt( ANKHS );
		
		upgradesUsed    = bundle.getInt( UPGRADES );
		sneakAttacks    = bundle.getInt( SNEAKS );
		thrownAssists   = bundle.getInt( THROWN );

		spawnersAlive   = bundle.getInt( SPAWNERS );
		coreAlive   = bundle.getInt( CORE );
		
		duration		= bundle.getFloat( DURATION );

		qualifiedForNoKilling = bundle.getBoolean( NO_KILLING_QUALIFIED );
		
		amuletObtained	= bundle.getBoolean( AMULET );
		ver0_3_2firstrun	= bundle.getBoolean( FIRSTRUN );

		drawn.clear();
		Collections.addAll(Statistics.drawn , bundle.getClassArray(DRAWN));
	}
	
	public static void preview( GamesInProgress.Info info, Bundle bundle ){
		info.goldCollected  = bundle.getInt( GOLD );
		info.maxDepth       = bundle.getInt( DEEPEST );
	}

}
