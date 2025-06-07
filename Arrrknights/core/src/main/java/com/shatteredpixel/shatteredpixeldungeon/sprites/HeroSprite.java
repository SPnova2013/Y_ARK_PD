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

		if(Dungeon.hero.CharSkin == Hero.HINA) updateHinaSkin();//除此之外，还需要在镜像卷轴和虹卫秘卷处增加非典型皮肤的适配
		if(Dungeon.hero.CharSkin == Hero.NEURO) updateNeuroSkin();
		if(Dungeon.hero.CharSkin == Hero.WISADEL) updateWisadelSkin();
		if(Dungeon.hero.CharSkin == Hero.TENMA) updateTenmaSkin();

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
		play( fly );
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
	
	public static Image avatar( HeroClass cl, int armorTier ) {
		
		RectF patch = tiers().get( armorTier );
		Image avatar = new Image( cl.spritesheet() );
		RectF frame = avatar.texture.uvRect( 1, 0, FRAME_WIDTH, FRAME_HEIGHT );
		frame.shift( patch.left, patch.top );
		avatar.frame( frame );
		
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

			// 保存原始回调
			Callback originalCallback = animCallback;

			// 设置特殊攻击后回调
			animCallback = new Callback() {
				@Override
				public void call() {
					// 先恢复原始回调
					animCallback = originalCallback;
					// 再执行原始攻击完成逻辑
					HeroSprite.super.onComplete(attack);
				}
			};

			// 播放特殊攻击后动画
			play(specialAfterAttack);
		} else {
			// 普通情况直接调用父类处理
			super.onComplete(anim);
		}
	}
}
