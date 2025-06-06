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

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import static com.shatteredpixel.shatteredpixeldungeon.Challenges.TEST;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.TomorrowRogueNight;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.Updates;
import com.shatteredpixel.shatteredpixeldungeon.ui.GameLog;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndStory;
import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.NoosaScript;
import com.watabou.noosa.NoosaScriptNoLighting;
import com.watabou.noosa.SkinnedBlock;
import com.watabou.utils.DeviceCompat;

import java.io.FileNotFoundException;
import java.io.IOException;

public class InterlevelScene extends PixelScene {
	
	//slow fade on entering a new region
	private static final float SLOW_FADE = 1f; //.33 in, 1.33 steady, .33 out, 2 seconds total
	//norm fade when loading, falling, returning, or descending to a new floor
	private static final float NORM_FADE = 0.67f; //.33 in, .67 steady, .33 out, 1.33 seconds total
	//fast fade when ascending, or descending to a floor you've been on
	private static final float FAST_FADE = 0.50f; //.33 in, .33 steady, .33 out, 1 second total
	
	private static float fadeTime;
	
	public enum Mode {
		DESCEND, ASCEND, CONTINUE, RESURRECT, RETURN, FALL, RESET, NONE, DESCEND_27, ASCEND_27,
	}
	public static Mode mode;
	
	public static int returnDepth;
	public static int returnPos;
	
	public static boolean noStory = false;

	public static boolean fallIntoPit;
	
	private enum Phase {
		FADE_IN, STATIC, FADE_OUT
	}
	private Phase phase;
	private float timeLeft;
	
	private RenderedTextBlock message;
	
	private static Thread thread;
	private static Exception error = null;
	private float waitingTime;
	
	@Override
	public void create() {
		//Checking logger slot everytime interLevelScene is loaded is a bit redundant but somehow necessary
		TomorrowRogueNight.initializeLoggers(GamesInProgress.curSlot);
		super.create();
		
		String loadingAsset;
		int loadingDepth;
		final float scrollSpeed;
		fadeTime = NORM_FADE;
		switch (mode){
			default:
				loadingDepth = Dungeon.depth;
				scrollSpeed = 0;
				break;
			case CONTINUE:
				loadingDepth = GamesInProgress.check(GamesInProgress.curSlot).depth;
				scrollSpeed = 5;
				break;
			case DESCEND:
				if (Dungeon.hero == null){
					loadingDepth = 1;
					fadeTime = SLOW_FADE;
				} else {
					loadingDepth = Dungeon.depth+1;
					if (!(Statistics.deepestFloor < loadingDepth)) {
						fadeTime = FAST_FADE;
					} else if (loadingDepth == 6 || loadingDepth == 11
							|| loadingDepth == 16 || loadingDepth == 21) {
						fadeTime = SLOW_FADE;
					}
				}
				scrollSpeed = 5;
				break;
			case DESCEND_27:
				loadingDepth = Dungeon.depth+26;
				scrollSpeed = 5;
				break;
			case FALL:
				loadingDepth = Dungeon.depth+1;
				scrollSpeed = 50;
				break;
			case ASCEND:
				fadeTime = FAST_FADE;
				loadingDepth = Dungeon.depth-1;
				scrollSpeed = -5;
				break;
			case ASCEND_27:
				fadeTime = FAST_FADE;
				loadingDepth = 1;
				scrollSpeed = -5;
				break;
			case RETURN:
				loadingDepth = returnDepth;
				scrollSpeed = returnDepth > Dungeon.depth ? 15 : -15;
				break;
		}
		if (loadingDepth <= 26) Dungeon.DLC = 0;//清空选择的DLC，尝试避免地图生成问题
		if (loadingDepth <= 5)          loadingAsset = Assets.Interfaces.LOADING_SEWERS;
		else if (loadingDepth <= 10)    loadingAsset = Assets.Interfaces.LOADING_PRISON;
		else if (loadingDepth <= 15)    loadingAsset = Assets.Interfaces.LOADING_CAVES;
		else if (loadingDepth <= 20)    loadingAsset = Assets.Interfaces.LOADING_CITY;
		else if (loadingDepth <= 26)    loadingAsset = Assets.Interfaces.LOADING_HALLS;
		else if (loadingDepth <= 30)    loadingAsset = Assets.Interfaces.LOADING_RHODOS;
		else if (loadingDepth <= 35)    loadingAsset = Assets.Interfaces.LOADING_SIESTA;
		else                            loadingAsset = Assets.Interfaces.SHADOW;
		
		//slow down transition when displaying an install prompt
		if (Updates.isInstallable()){
			fadeTime += 0.5f; //adds 1 second total
		//speed up transition when debugging
		} else if (DeviceCompat.isDebug() || Dungeon.isChallenged(TEST)){
			fadeTime = 0f;
		}
		
		SkinnedBlock bg = new SkinnedBlock(Camera.main.width, Camera.main.height, loadingAsset ){
			@Override
			protected NoosaScript script() {
				return NoosaScriptNoLighting.get();
			}
			
			@Override
			public void draw() {
				Blending.disable();
				super.draw();
				Blending.enable();
			}
			
			@Override
			public void update() {
				super.update();
				offset(0, Game.elapsed * scrollSpeed);
			}
		};
		bg.scale(4, 4);
		bg.autoAdjust = true;
		add(bg);
		
		Image im = new Image(TextureCache.createGradient(0xAA000000, 0xBB000000, 0xCC000000, 0xDD000000, 0xFF000000)){
			@Override
			public void update() {
				super.update();
				if (phase == Phase.FADE_IN)         aa = Math.max( 0, (timeLeft - (fadeTime - 0.333f)));
				else if (phase == Phase.FADE_OUT)   aa = Math.max( 0, (0.333f - timeLeft));
				else                                aa = 0;
			}
		};
		im.angle = 90;
		im.x = Camera.main.width;
		im.scale.x = Camera.main.height/5f;
		im.scale.y = Camera.main.width;
		add(im);

		String text = Messages.get(Mode.class, mode.name());
		
		message = PixelScene.renderTextBlock( text, 9 );
		message.setPos(
				(Camera.main.width - message.width()) / 2,
				(Camera.main.height - message.height()) / 2
		);
		align(message);
		add( message );

		if (Updates.isInstallable()){
			StyledButton install = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "install")){
				@Override
				public void update() {
					super.update();
					float p = timeLeft / fadeTime;
					if (phase == Phase.FADE_IN)         alpha(1 - p);
					else if (phase == Phase.FADE_OUT)   alpha(p);
					else                                alpha(1);
				}

				@Override
				protected void onClick() {
					super.onClick();
					Updates.launchInstall();
				}
			};
			install.icon(Icons.get(Icons.CHANGES));
			install.textColor(Window.ARKPD_COLOR);
			install.setSize(install.reqWidth()+5, 20);
			install.setPos((Camera.main.width - install.width())/2, (Camera.main.height - message.bottom())/3 + message.bottom());
			add(install);
		}
		
		phase = Phase.FADE_IN;
		timeLeft = fadeTime;
		
		if (thread == null) {
			thread = new Thread() {
				@Override
				public void run() {
					
					try {

						if (Dungeon.hero != null){
							Dungeon.hero.spendToWhole();
						}
						Actor.fixTime();

						switch (mode) {
							case DESCEND:
								descend();
								break;
							case DESCEND_27:
								descend_27();
								break;
							case ASCEND:
								ascend();
								break;
							case ASCEND_27:
								ascend_27();
								break;
							case CONTINUE:
								restore();
								break;
							case RESURRECT:
								resurrect();
								break;
							case RETURN:
								returnTo();
								break;
							case FALL:
								fall();
								break;
							case RESET:
								reset();
								break;
						}
						
					} catch (Exception e) {
						
						error = e;
						
					}
					
					if (phase == Phase.STATIC && error == null) {
						phase = Phase.FADE_OUT;
						timeLeft = fadeTime;
					}
				}
			};
			thread.start();
		}
		waitingTime = 0f;
	}
	
	@Override
	public void update() {
		super.update();

		waitingTime += Game.elapsed;
		
		float p = timeLeft / fadeTime;
		
		switch (phase) {
		
		case FADE_IN:
			message.alpha( 1 - p );
			if ((timeLeft -= Game.elapsed) <= 0) {
				if (!thread.isAlive() && error == null) {
					phase = Phase.FADE_OUT;
					timeLeft = fadeTime;
				} else {
					phase = Phase.STATIC;
				}
			}
			break;
			
		case FADE_OUT:
			message.alpha( p );
			
			if ((timeLeft -= Game.elapsed) <= 0) {
				Game.switchScene( GameScene.class );
				thread = null;
				error = null;
			}
			break;
			
		case STATIC:
			if (error != null) {
				String errorMsg;
				if (error instanceof FileNotFoundException)     errorMsg = Messages.get(this, "file_not_found");
				else if (error instanceof IOException)          errorMsg = Messages.get(this, "io_error");
				else if (error.getMessage() != null &&
						error.getMessage().equals("old save")) errorMsg = Messages.get(this, "io_error");

				else throw new RuntimeException("fatal error occured while moving between floors. " +
							"Seed:" + Dungeon.seed + " depth:" + Dungeon.depth, error);

				add( new WndError( errorMsg ) {
					public void onBackPressed() {
						super.onBackPressed();
						Game.switchScene( StartScene.class );
					}
				} );
				thread = null;
				error = null;
			} else if (thread != null && (int)waitingTime == 10){
				waitingTime = 11f;
				String s = "";
				for (StackTraceElement t : thread.getStackTrace()){
					s += "\n";
					s += t.toString();
				}
				TomorrowRogueNight.reportException(
						new RuntimeException("waited more than 10 seconds on levelgen. " +
								"Seed:" + Dungeon.seed + " depth:" + Dungeon.depth + " trace:" +
								s)
				);
			}
			break;
		}
	}

	private void descend() throws IOException {

		if (Dungeon.hero == null) {
			Mob.clearHeldAllies();
			Dungeon.init();
			if (noStory) {
				Dungeon.chapters.add( WndStory.ID_SEWERS );
				noStory = false;
			}
			GameLog.wipe();
		} else {
			Mob.holdAllies( Dungeon.level );
			Dungeon.saveAll();
		}

		Level level;
		if (Dungeon.depth >= Statistics.deepestFloor) {
			level = Dungeon.newLevel();
		} else {
			Dungeon.depth++;
			level = Dungeon.loadLevel( GamesInProgress.curSlot );
		}
		Dungeon.switchLevel( level, level.entrance );
	}

	private void descend_27() throws IOException {
		Mob.holdAllies( Dungeon.level );
		Dungeon.saveAll();

		Level level;
		Dungeon.depth=26;
		level = Dungeon.newLevel();
		Dungeon.switchLevel( level, level.entrance );
	}
	
	private void fall() throws IOException {
		
		Mob.holdAllies( Dungeon.level );
		
		Buff.affect( Dungeon.hero, Chasm.Falling.class );
		Dungeon.saveAll();

		Level level;
		if (Dungeon.depth >= Statistics.deepestFloor) {
			level = Dungeon.newLevel();
		} else {
			Dungeon.depth++;
			level = Dungeon.loadLevel( GamesInProgress.curSlot );
		}
		Dungeon.switchLevel( level, level.fallCell( fallIntoPit ));
	}
	
	private void ascend() throws IOException {

		Mob.holdAllies( Dungeon.level );

		Dungeon.saveAll();
		Dungeon.depth--;
		Level level = Dungeon.loadLevel( GamesInProgress.curSlot );
		Dungeon.switchLevel( level, level.exit );
	}

	private void ascend_27() throws IOException {
		Mob.holdAllies( Dungeon.level );

		Dungeon.depth = 1;
		Level level = Dungeon.loadLevel( GamesInProgress.curSlot );
		Dungeon.switchLevel( level, level.entrance );
	}

	
	private void returnTo() throws IOException {
		
		Mob.holdAllies( Dungeon.level );

		Dungeon.saveAll();
		Dungeon.depth = returnDepth;
		Level level = Dungeon.loadLevel( GamesInProgress.curSlot );
		Dungeon.switchLevel( level, returnPos );
	}
	
	private void restore() throws IOException {
		
		Mob.clearHeldAllies();

		GameLog.wipe();

		Dungeon.loadGame( GamesInProgress.curSlot );
		if (Dungeon.depth == -1) {
			Dungeon.depth = Statistics.deepestFloor;
			Dungeon.switchLevel( Dungeon.loadLevel( GamesInProgress.curSlot ), -1 );
		} else {
			Level level = Dungeon.loadLevel( GamesInProgress.curSlot );
			Dungeon.switchLevel( level, Dungeon.hero.pos );
		}
	}
	
	private void resurrect() throws IOException {
		
		Mob.holdAllies( Dungeon.level );
		
		if (Dungeon.level.locked) {
			Dungeon.hero.resurrect( Dungeon.depth );
			Dungeon.depth--;
			Level level = Dungeon.newLevel();
			Dungeon.switchLevel( level, level.entrance );
		} else {
			Dungeon.hero.resurrect( -1 );
			Dungeon.resetLevel();
		}
	}

	private void reset() throws IOException {
		
		Mob.holdAllies( Dungeon.level );

		SpecialRoom.resetPitRoom(Dungeon.depth+1);

		Dungeon.depth--;
		Level level = Dungeon.newLevel();
		Dungeon.switchLevel( level, level.entrance );
	}
	
	@Override
	protected void onBackPressed() {
		//Do nothing
	}
}
