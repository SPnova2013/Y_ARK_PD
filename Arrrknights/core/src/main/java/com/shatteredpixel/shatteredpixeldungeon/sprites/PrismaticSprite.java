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

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import static com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite.tiers;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.PrismaticImage;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.PlateArmor;
import com.watabou.noosa.Game;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class PrismaticSprite extends MobSprite {
	
	private static final int FRAME_WIDTH	= 36;
	private static final int FRAME_HEIGHT	= 36;
	
	public PrismaticSprite() {
		super();
		
		texture( Dungeon.hero.heroClass.spritesheet() );
		updateArmor( 0 );
		idle();
	}
	
	@Override
	public void link( Char ch ) {
		super.link( ch );
		updateArmor( ((PrismaticImage)ch).armTier );
	}
	
	public void updateArmor( int tier ) {
		TextureFilm film = new TextureFilm( tiers(), tier, FRAME_WIDTH, FRAME_HEIGHT );

		idle = new Animation( 1, true );
		idle.frames( film, 0, 0, 0 );

		run = new Animation( 20, true );
		run.frames( film, 1, 2, 3, 4, 5, 6, 7, 8  );

		die = new Animation( 20, false );
		die.frames( film, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35 );

		attack = new Animation( 15, false );
		attack.frames( film, 9, 10, 11, 12, 13, 14, 15, 16 );

		if(Dungeon.hero.CharSkin == Hero.HINA) updateHinaSkin();
		if(Dungeon.hero.CharSkin == Hero.NEURO) updateNeuroSkin();
		if(Dungeon.hero.CharSkin == Hero.WISADEL) updateWisadelSkin();
		if(Dungeon.hero.CharSkin == Hero.ES) updateEsSkin();
		idle();
	}
	
	@Override
	public void update() {
		super.update();
		
		if (flashTime <= 0){
			float interval = (Game.timeTotal % 9 ) /3f;
			tint(interval > 2 ? interval - 2 : Math.max(0, 1 - interval),
					interval > 1 ? Math.max(0, 2-interval): interval,
					interval > 2 ? Math.max(0, 3-interval): interval-1, 0.5f);
		}
	}

	private void updateHinaSkin() {
		TextureFilm film = new TextureFilm(texture, 50, 40);
		idle = new MovieClip.Animation( 7, true );
		idle.frames( film, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 45, 46, 47, 48, 49, 50, 51, 52, 53,
				53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 54, 55, 56, 57, 58, 59, 60,
				53, 53, 53, 53, 53, 53, 53, 61, 62, 63);
		run = new MovieClip.Animation( 20, true );
		run.frames( film, 1, 2, 3, 4, 5, 6, 7, 8 );
		//if(Dungeon.hero.belongings.armor instanceof PlateArmor) run.frames(film, 1);
		die = new MovieClip.Animation( 8, false );
		die.frames( film, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39 );
		attack = new MovieClip.Animation( 25, false );
		attack.frames( film, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 );
		Sattack = attack.clone();
		zap = attack.clone();
		operate = new Animation( 8, false );
		operate.frames( film, 40, 41, 40, 41 );
	}
	public void updateNeuroSkin() {
		TextureFilm film = new TextureFilm(texture, 36, 36);
		idle = new MovieClip.Animation( 7, true );
		idle.frames( film, 41);
		specialIdle = new MovieClip.Animation( 7, false );
		specialIdle.frames(film, 41, 42, 43, 44, 45, 46);
		run = new MovieClip.Animation( 20, true );
		run.frames( film, 1, 2, 3, 4, 5, 6, 7, 8 );
		//if(Dungeon.hero.belongings.armor instanceof PlateArmor) run.frames(film, 1);
		die = new MovieClip.Animation( 8, false );
		die.frames( film, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36 );
		attack = new MovieClip.Animation( 25, false );
		attack.frames( film, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 );
		Sattack = attack.clone();
		zap = attack.clone();
		operate = new Animation( 8, false );
		operate.frames( film, 37, 38 );
	}
	public void updateWisadelSkin() {
		texture(Dungeon.hero.heroClass.spritesheet());
		TextureFilm film = new TextureFilm(tiers(), Dungeon.hero.tier(), 36, 36);
		idle = new MovieClip.Animation( 7, true );
		idle.frames( film, 41, 42, 43, 44, 45, 46, 47, 48);
		if(Dungeon.hero.belongings.armor instanceof PlateArmor || Dungeon.hero.SK3 != null){
			idle = new MovieClip.Animation( 7, false );
			idle.frames( film, 41, 42, 43, 44, 45, 46, 47, 48);
		}
		run = new MovieClip.Animation( 20, true );
		run.frames( film, 1, 2, 3, 4, 5, 6, 7, 8 );
		die = new MovieClip.Animation( 8, false );
		die.frames( film, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36 );
		attack = new MovieClip.Animation( 16, false );
		attack.frames( film, 9, 10, 11, 12, 13, 14, 15, 16, 17);
		Sattack = attack.clone();
		zap = attack.clone();
		operate = new Animation( 8, false );
		operate.frames( film, 37, 38, 37, 38 );
	}
	public void updateEsSkin() {
		TextureFilm film = new TextureFilm(texture, 80, 50);
		idle = new MovieClip.Animation( 7, true );
		idle.frames( film, 61,62,63,64,61,62,63,64,61,62,63,64,61,62,63,64,61,62,63,64,66,66,66,66,66);
		run = new MovieClip.Animation( 20, true );
		run.frames( film, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 );
		die = new MovieClip.Animation( 8, false );
		die.frames( film,  50, 51, 52, 53, 54, 55, 56);
		attack = new MovieClip.Animation( 25, false );
		attack.frames( film, 11, 12, 13, 14, 15, 16, 17, 18 );
		Sattack = attack.clone();
		zap = attack.clone();
		operate = new Animation( 8, false );
		operate.frames( film,  57, 58);
		eat = operate.clone();
	}
}
