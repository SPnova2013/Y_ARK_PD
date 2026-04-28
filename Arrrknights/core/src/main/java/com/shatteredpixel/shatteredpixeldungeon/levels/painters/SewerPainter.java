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

package com.shatteredpixel.shatteredpixeldungeon.levels.painters;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.RlyehText;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.EntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.ExitRoom;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class SewerPainter extends RegularPainter {
	
	@Override
	protected void decorate(Level level, ArrayList<Room> rooms) {
		if(Dungeon.depth == 1){
			for (Room r : rooms) {
				if (r instanceof ExitRoom) {
					RlyehText.spawn(level, r);
					break;
				}
			}
		}
		
		int[] map = level.map;
		int w = level.width();
		int l = level.length();
		
		for (int i=0; i < w; i++) {
			if (map[i] == Terrain.WALL &&
					map[i + w] == Terrain.WATER &&
					Random.Int( 4 ) == 0) {
				
				map[i] = Terrain.WALL_DECO;
			}
		}
		
		for (int i=w; i < l - w; i++) {
			if (map[i] == Terrain.WALL &&
					map[i - w] == Terrain.WALL &&
					map[i + w] == Terrain.WATER &&
					Random.Int( 2 ) == 0) {
				
				map[i] = Terrain.WALL_DECO;
			}
		}
		
		for (int i=w + 1; i < l - w - 1; i++) {
			if (map[i] == Terrain.EMPTY) {
				
				int count =
						(map[i + 1] == Terrain.WALL ? 1 : 0) +
								(map[i - 1] == Terrain.WALL ? 1 : 0) +
								(map[i + w] == Terrain.WALL ? 1 : 0) +
								(map[i - w] == Terrain.WALL ? 1 : 0);
				
				if (Random.Int( 16 ) < count * count) {
					map[i] = Terrain.EMPTY_DECO;
				}
			}
		}
	}
}
