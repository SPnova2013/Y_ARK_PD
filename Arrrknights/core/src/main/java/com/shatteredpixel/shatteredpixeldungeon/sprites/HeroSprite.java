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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.PlateArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.SakuraSword;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import com.watabou.utils.RectF;

public class HeroSprite extends CharSprite {
	private static final int FRAME_WIDTH	= 36;
	private static final int FRAME_HEIGHT	= 36;
	
	private static final int RUN_FRAMERATE	= 20;
	
	private static TextureFilm tiers;
	
	private Animation fly;
	private Animation read;
	private Animation BoD;
	private Animation Gawain;

	public HeroSprite() {
		super();

		texture(Dungeon.hero.heroClass.spritesheet());
		updateArmor();

		link( Dungeon.hero );

		if (ch.isAlive())
			idle();
		else
			die();
	}
	
	public void updateArmor() {
		texture(Dungeon.hero.heroClass.spritesheet());
		TextureFilm film = new TextureFilm(tiers(), Dungeon.hero.tier(), 36, 34);
		//if(Dungeon.hero.CharSkin == Hero.HINA) film = new TextureFilm(texture, 50, 40);

		idle = new Animation( 7, true );
		idle.frames( film, 41, 42, 43, 44, 45, 46, 41, 42, 43, 44, 45, 46, 41, 42, 43, 44, 45, 46, 41, 42, 43, 44, 45, 46, 41, 42, 43, 47, 48, 46 );

		run = new Animation( RUN_FRAMERATE, true );
		run.frames( film, 1, 2, 3, 4, 5, 6, 7, 8 );
		
		die = new Animation( 8, false );
		die.frames( film, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36 );
		
		attack = new Animation( 25, false );
		attack.frames( film, 9, 10, 11, 12, 13, 14, 15, 16, 17 );

		Sattack = new Animation(15, false);
		Sattack.frames(film, 9, 11, 14, 16);
		
		zap = attack.clone();
		
		operate = new Animation( 8, false );
		operate.frames( film, 37, 38, 37, 38 );

		eat = operate.clone();
		
		fly = new Animation( 1, true );
		fly.frames( film, 39, 40 );

		read = new Animation( 10, false );
		read.frames( film, 18, 19, 20, 21, 22, 23, 24, 25, 25, 25 );

		specialIdle = idle.clone();

		specialAfterAttack = new Animation( 1, false );
		specialAfterAttack.frames(attack.frames[attack.frames.length-1]);
		BoD = specialAfterAttack.clone();
		Gawain = attack.clone();

		if(Dungeon.hero.CharSkin == Hero.HINA) updateHinaSkin();//除此之外，还需要在镜像卷轴和虹卫秘卷处增加非典型皮肤的适配
		if(Dungeon.hero.CharSkin == Hero.NEURO) updateNeuroSkin();
		if(Dungeon.hero.CharSkin == Hero.WISADEL) updateWisadelSkin();
		if(Dungeon.hero.CharSkin == Hero.TENMA) updateTenmaSkin();
		if(Dungeon.hero.CharSkin == Hero.ES) updateEsSkin();
		if(Dungeon.hero.CharSkin == Hero.NEURO_ANGEL) updateNeuroAngelSkin();
		if(Dungeon.hero.CharSkin == Hero.NEURO_CAT) updateNeuroCatSkin();
		if(Dungeon.hero.CharSkin == Hero.NEURO_JOKER) updateNeuroJokerSkin();
		if(Dungeon.hero.CharSkin == Hero.NEURO_WITCH) updateNeuroWitchSkin();

		if (Dungeon.hero.isAlive())
			idle();
		else
			die();
	}

	public void updateHinaSkin() {
		TextureFilm film = new TextureFilm(texture, 50, 40);
		idle = new MovieClip.Animation( 7, true );
		idle.frames( film, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 45, 46, 47, 48, 49, 50, 51, 52, 53,
				53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 54, 55, 56, 57, 58, 59, 60,
				53, 53, 53, 53, 53, 53, 53, 61, 62, 63);
		run = new MovieClip.Animation( 20, true );
		run.frames( film, 1, 2, 3, 4, 5, 6, 7, 8 );
		die = new MovieClip.Animation( 8, false );
		die.frames( film, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39 );
		attack = new MovieClip.Animation( 25, false );
		attack.frames( film, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 );
		Sattack = attack.clone();
		zap = attack.clone();
		operate = new Animation( 8, false );
		operate.frames( film, 40, 41, 40, 41 );
		eat = operate.clone();
		fly = new Animation( 8, true );
		fly.frames( film, 42, 43, 44 );
		read = new Animation( 10, false );
		read.frames( film, 21, 22, 23, 24, 25, 26, 27, 28 );
	}

	public void updateNeuroSkin() {
		texture(Dungeon.hero.heroClass.spritesheet());
		TextureFilm film = new TextureFilm(texture, 36, 36);
		idle = new MovieClip.Animation( 7, false );
		idle.frames( film, 41, 41, 41 ,41, 41, 41, 41, 41 ,41, 41, 41, 41 ,41, 41, 41, 41 ,41, 41, 41, 41 ,41, 41, 41, 41 ,41, 41, 42, 43, 44, 45, 46, 45, 46, 45, 46, 45, 46, 45, 46, 45, 46);
		specialIdle = new MovieClip.Animation( 7, true );
		specialIdle.frames(film, 45, 46);
		run = new MovieClip.Animation( 20, true );
		run.frames( film, 1, 2, 3, 4, 5, 6, 7, 8 );
		die = new MovieClip.Animation( 8, false );
		die.frames( film, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36 );
		attack = new MovieClip.Animation( 25, false );
		attack.frames( film, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 );
		Sattack = attack.clone();
		zap = attack.clone();
		operate = new Animation( 8, false );
		operate.frames( film, 37, 38, 37, 38 );
		eat = new Animation( 8, false );
		eat.frames( film, 47, 48, 49, 50, 51, 52, 53);
		fly = new Animation( 1, true );
		fly.frames( film, 39, 39, 40, 40 );
		read = new Animation( 10, false );
		read.frames( film, 20, 21, 22, 23, 24, 25 );
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
		eat = operate.clone();
		fly = new Animation( 1, true );
		fly.frames( film, 39, 40 );
		read = new Animation( 10, false );
		read.frames( film, 18, 19, 20, 21, 22, 23, 24, 25 );
	}
	public void updateTenmaSkin() {
		TextureFilm film = new TextureFilm(texture, 36, 36);
		idle = new MovieClip.Animation( 7, true );
		idle.frames( film, 41, 42, 43, 44, 45, 46, 47, 48);
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
		eat = operate.clone();
		fly = new Animation( 1, true );
		fly.frames( film, 39, 40 );
		read = new Animation( 10, false );
		read.frames( film, 18, 19, 20, 21, 22, 23, 24, 25 );

		specialAfterAttack = new MovieClip.Animation( 16, false );
		specialAfterAttack.frames( film, 49, 50, 51, 52, 53);

		BoD = new MovieClip.Animation( 1, false );
		BoD.frames( film, 54, 55);
	}
	public void updateEsSkin() {
		TextureFilm film = new TextureFilm(texture, 80, 50);
		idle = new MovieClip.Animation( 5, true );
		idle.frames( film, 61,62,63,64,61,62,63,64,61,62,63,64,61,62,63,64,61,62,63,64,65,65,65,65);
		run = new MovieClip.Animation( 20, true );
		run.frames( film, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 );
		die = new MovieClip.Animation( 8, false );
		die.frames( film,  50, 51, 52, 53, 54, 55, 56);
		attack = new MovieClip.Animation( 18, false );
		attack.frames( film, 11, 12, 13, 14, 15, 16, 17, 18 );
		Sattack = attack.clone();
		zap = attack.clone();
		operate = new Animation( 8, false );
		operate.frames( film,  57, 58);
		eat = operate.clone();
		fly = new Animation( 8, true );
		fly.frames( film,  59, 60);
		read = new Animation( 15, false );
		read.frames( film,  19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49);

		Gawain = new Animation(30, false);
		Gawain.frames(film, 66,67,68,69,70,71,72,73,74,75,76,77,78,79,80);
	}

	public void updateNeuroAngelSkin() {
		texture(Dungeon.hero.heroClass.spritesheet());
		TextureFilm film = new TextureFilm(texture, 36, 36);
		idle = new MovieClip.Animation( 7, false );
		idle.frames( film, 0);
		run = new MovieClip.Animation( 20, true );
		run.frames( film, 25,26,27,28,29,30,31,32 );
		die = new MovieClip.Animation( 8, false );
		die.frames( film, 33,34,35,36,37,38,39,40,41,42,43);
		attack = new MovieClip.Animation( 25, false );
		attack.frames( film, 44,45,46,47,48,49,50,51,52);
		Sattack = attack.clone();
		zap = attack.clone();
		operate = new Animation( 8, false );
		operate.frames( film, 10,11,12,13,14,15,16,17);
		eat = new Animation( 8, false );
		eat.frames( film, 18,19,20,21,22,23,24);
		fly = new Animation( 1, true );
		fly.frames( film, 8,9 );
		read = new Animation( 10, false );
		read.frames( film, 0,1,2,3,4,5,6,7);
	}
	public void updateNeuroCatSkin() {
		texture(Dungeon.hero.heroClass.spritesheet());
		TextureFilm film = new TextureFilm(texture, 36, 36);
		idle = new MovieClip.Animation( 7, false );
		idle.frames( film, 0, 0, 0 ,0, 0, 0, 0, 0 ,0, 0, 0, 0 ,0, 0, 0, 0 ,0, 0, 0, 0 ,0, 0, 0, 0 ,0, 0, 1, 2, 3, 4, 5, 6, 7, 6, 7,6,7,6,7);
		specialIdle = new MovieClip.Animation( 7, true );
		specialIdle.frames(film, 6, 7);
		run = new MovieClip.Animation( 20, true );
		run.frames( film, 25,26,27,28,29,30,31,32 );
		die = new MovieClip.Animation( 8, false );
		die.frames( film, 33,34,35,36,37,38,39,40,41,42,43);
		attack = new MovieClip.Animation( 25, false );
		attack.frames( film, 44,45,46,47,48,49,50,51,52);
		Sattack = attack.clone();
		zap = attack.clone();
		operate = new Animation( 8, false );
		operate.frames( film, 10,11,12,13,14,15,16,17);
		eat = new Animation( 8, false );
		eat.frames( film, 18,19,20,21,22,23,24);
		fly = new Animation( 1, true );
		fly.frames( film, 8,9 );
		read = new Animation( 10, false );
		read = operate.clone();
	}
	public void updateNeuroJokerSkin() {
		texture(Dungeon.hero.heroClass.spritesheet());
		TextureFilm film = new TextureFilm(texture, 36, 36);
		idle = new MovieClip.Animation( 7, false );
		idle.frames( film, 0);
		run = new MovieClip.Animation( 20, true );
		run.frames( film, 25,26,27,28,29,30,31,32 );
		die = new MovieClip.Animation( 8, false );
		die.frames( film, 33,34,35,36,37,38,39,40,41,42,43);
		attack = new MovieClip.Animation( 25, false );
		attack.frames( film, 44,45,46,47,48,49,50,51,52);
		Sattack = attack.clone();
		zap = attack.clone();
		operate = new Animation( 8, false );
		operate.frames( film, 10,11,12,13,14,15,16,17);
		eat = new Animation( 8, false );
		eat.frames( film, 18,19,20,21,22,23,24);
		fly = new Animation( 1, true );
		fly.frames( film, 8,9 );
		read = new Animation( 10, false );
		read.frames( film, 0,1,2,3,4,5,6,7);
	}
	public void updateNeuroWitchSkin() {
		texture(Dungeon.hero.heroClass.spritesheet());
		TextureFilm film = new TextureFilm(texture, 36, 36);
		idle = new MovieClip.Animation( 7, false );
		idle.frames( film, 0);
		run = new MovieClip.Animation( 20, true );
		run.frames( film, 25,26,27,28,29,30,31,32 );
		die = new MovieClip.Animation( 8, false );
		die.frames( film, 33,34,35,36,37,38,39,40,41,42,43);
		attack = new MovieClip.Animation( 25, false );
		attack.frames( film, 44,45,46,47,48,49,50,51,52);
		Sattack = attack.clone();
		zap = attack.clone();
		operate = new Animation( 8, false );
		operate.frames( film, 10,11,12,13,14,15,16,17);
		eat = new Animation( 8, false );
		eat.frames( film, 18,19,20,21,22,23,24);
		fly = new Animation( 1, true );
		fly.frames( film, 8,9 );
		read = new Animation( 10, false );
		read.frames( film, 0,1,2,3,4,5,6,7);
	}

	@Override
	public void place( int p ) {
		super.place( p );
		Camera.main.panTo(center(), 5f);
	}

	@Override
	public void move( int from, int to ) {
		super.move( from, to );
		if (ch != null && ch.flying) {
			play( fly );
		}
		Camera.main.panFollow(this, 20f);
	}

	@Override
	public void idle() {
		super.idle();
		if (ch != null && ch.flying) {
			play( fly );
		}
	}

	@Override
	public void jump( int from, int to, Callback callback ) {
		super.jump( from, to, callback );
		if (Dungeon.hero != null && Dungeon.hero.CharSkin == Hero.ES) {
			play(Gawain);
		} else play( fly );
	}

	@Override
	public void jump( int from, int to, float height, float duration , Callback callback) {
		super.jump(from, to, height, duration, callback);
		if (Dungeon.hero != null && Dungeon.hero.CharSkin == Hero.ES) {
			play(Gawain);
		} else play( fly );
	}

	public void BoD() {
		play( BoD );
	}

	public void read() {
		animCallback = new Callback() {
			@Override
			public void call() {
				idle();
				ch.onOperateComplete();
			}
		};
		play( read );
	}

	@Override
	public void bloodBurstA(PointF from, int damage) {
		//Does nothing.

		/*
		 * This is both for visual clarity, and also for content ratings regarding violence
		 * towards human characters. The heroes are the only human or human-like characters which
		 * participate in combat, so removing all blood associated with them is a simple way to
		 * reduce the violence rating of the game.
		 */
	}

	@Override
	public void update() {
		sleeping = ch.isAlive() && ((Hero)ch).resting;
		
		super.update();
	}
	
	public void sprint( float speed ) {
		run.delay = 1f / speed / RUN_FRAMERATE;
	}
	
	public static TextureFilm tiers() {
		if (tiers == null) {
			SmartTexture texture = TextureCache.get( Assets.Sprites.AMIYA );
			tiers = new TextureFilm( texture, texture.width, FRAME_HEIGHT );
		}
		
		return tiers;
	}
	public static PointF getAvatarFrameSize(HeroClass cl, int skinId) {
		switch (skinId) {
			case Hero.HINA:
				return new PointF(50, 40);
			case Hero.ES:
				return new PointF(80, 60);
			case Hero.NEURO:
			case Hero.WISADEL:
			case Hero.TENMA:
			case Hero.NEURO_ANGEL:
			case Hero.NEURO_CAT:
			case Hero.NEURO_JOKER:
			case Hero.NEURO_WITCH:
				return new PointF(36, 36);
			default:
				return new PointF(36, 36);
		}
	}
	public static Image avatar(HeroClass cl, int armorTier) {
		return avatar(cl, armorTier, 0);
	}
	public static Image avatar(HeroClass cl, int armorTier, int skinId) {
		PointF frameSize = getAvatarFrameSize(cl, skinId);
		int frameWidth = (int)frameSize.x;
		int frameHeight = (int)frameSize.y;

		RectF patch = tiers().get(armorTier);
		Image avatar = new Image(cl.spritesheet());

		RectF frame = avatar.texture.uvRect(1, 0, frameWidth, frameHeight);
		frame.shift(patch.left, patch.top);
		avatar.frame(frame);

		return avatar;
	}

	public static Image avatar_de( HeroClass cl, int armorTier ) {

		RectF patch = tiers().get( armorTier );
		Image avatar = new Image( cl.spritesheet_de() );
		RectF frame = avatar.texture.uvRect( 1, 0, FRAME_WIDTH, FRAME_HEIGHT );
		frame.shift( patch.left, patch.top );
		avatar.frame( frame );

		return avatar;
	}

	@Override
	public void onComplete(Animation anim) {
		if (anim == attack &&
				Dungeon.hero.CharSkin != 0 &&
				Dungeon.hero.CharSkinClass.isAllowSpecialAfterAttack()) {

			super.onComplete(anim);
			playSpecialAfterAttack();

		} else {
			super.onComplete(anim);
		}
	}
	private void playSpecialAfterAttack() {
		final Callback originalCallback = animCallback;
		super.isMoving = true;
		animCallback = new Callback() {
			@Override
			public void call() {
				animCallback = originalCallback;
				synchronized (HeroSprite.this) {
					isMoving = false;
					HeroSprite.this.notify();
				}
				idle();
			}
		};
		if(Dungeon.hero.getEnemy() != null
				&& !Dungeon.hero.getEnemy().isAlive()
				&& Dungeon.hero.CharSkin == Hero.TENMA
				&& Dungeon.hero.belongings.weapon instanceof SakuraSword
				&& (((SakuraSword)Dungeon.hero.belongings.weapon).isExMode() || ((SakuraSword)Dungeon.hero.belongings.weapon).charge == 0)) play(BoD);
		else play(specialAfterAttack);
	}
}
