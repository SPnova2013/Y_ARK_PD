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

import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.WelcomeScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.ActorLogger;
import com.shatteredpixel.shatteredpixeldungeon.utils.ItemLogger;
import com.shatteredpixel.shatteredpixeldungeon.utils.Logger;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.PlatformSupport;

import java.util.ArrayList;
import java.util.List;

import javax.swing.plaf.synth.SynthTextAreaUI;

public class TomorrowRogueNight extends Game {

	//variable constants for specific older versions of shattered, used for data conversion
	//versions older than v0.7.5e are no longer supported, and data from them is ignored
	public static final int v0_7_5e = 382;

	public static final int v0_8_0b = 414;
	public static final int v0_8_1a = 422;
	public static final int v0_8_2d = 463;

	public static final int v0_9_0b  = 489;
	public static final int v0_9_1d  = 511;
	public static final int v0_9_2   = 519;
	public static final int v0_1_2   = 533;
	public static final int v0_1_2b   = 534;
	public static ActorLogger actorLogger;
	public static ItemLogger itemLogger;
	public static List<String> logList = new java.util.ArrayList<>();

	public TomorrowRogueNight(PlatformSupport platform ) {
		super( sceneClass == null ? WelcomeScene.class : sceneClass, platform );
		//v0.8.0
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ArmoredBrute.class,
				"com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Shielded");
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM100.class,
				"com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Shaman");
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental.FireElemental.class,
				"com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental");
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental.NewbornFireElemental.class,
				"com.shatteredpixel.shatteredpixeldungeon.actors.mobs.NewbornElemental");
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.shatteredpixeldungeon.actors.mobs.OldDM300.class,
				"com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM300");
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.shatteredpixeldungeon.levels.OldCavesBossLevel.class,
				"com.shatteredpixel.shatteredpixeldungeon.levels.CavesBossLevel" );
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.shatteredpixeldungeon.levels.OldCityBossLevel.class,
				"com.shatteredpixel.shatteredpixeldungeon.levels.CityBossLevel" );
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.shatteredpixeldungeon.levels.OldHallsBossLevel.class,
				"com.shatteredpixel.shatteredpixeldungeon.levels.HallsBossLevel" );
		
	}
	
	@Override
	public void create() {
		super.create();

		updateSystemUI();
		SPDAction.loadBindings();
		Music.INSTANCE.enable( SPDSettings.music() );
		Music.INSTANCE.volume( SPDSettings.musicVol()*SPDSettings.musicVol()/100f );
		Sample.INSTANCE.enable( SPDSettings.soundFx() );
		Sample.INSTANCE.volume( SPDSettings.SFXVol()*SPDSettings.SFXVol()/100f );

		Sample.INSTANCE.load( Assets.Sounds.all );
		
	}

	public static void initializeLoggers(int slot) {
		String actorLogFileName = "slot" + slot + "_actor_log.csv";
		String itemLogFileName = "slot" + slot + "_item_log.csv";

		DeviceCompat.log("","Initializing loggers: " + actorLogFileName);
		actorLogger = new ActorLogger(500, actorLogFileName);
		if (!logList.contains(actorLogFileName)) logList.add(actorLogFileName);
		itemLogger = new ItemLogger(500, itemLogFileName);
		if (!logList.contains(itemLogFileName)) logList.add(itemLogFileName);
	}

	public static void switchNoFade(Class<? extends PixelScene> c){
		switchNoFade(c, null);
	}

	public static void switchNoFade(Class<? extends PixelScene> c, SceneChangeCallback callback) {
		PixelScene.noFade = true;
		switchScene( c, callback );
	}
	
	public static void seamlessResetScene(SceneChangeCallback callback) {
		if (scene() instanceof PixelScene){
			((PixelScene) scene()).saveWindows();
			switchNoFade((Class<? extends PixelScene>) sceneClass, callback );
		} else {
			resetScene();
		}
	}
	
	public static void seamlessResetScene(){
		seamlessResetScene(null);
	}
	
	@Override
	protected void switchScene() {
		super.switchScene();
		if (scene instanceof PixelScene){
			((PixelScene) scene).restoreWindows();
		}
	}
	
	@Override
	public void resize( int width, int height ) {
		if (width == 0 || height == 0){
			return;
		}

		if (scene instanceof PixelScene &&
				(height != Game.height || width != Game.width)) {
			PixelScene.noFade = true;
			((PixelScene) scene).saveWindows();
		}

		super.resize( width, height );

		updateDisplaySize();

	}
	
	@Override
	public void destroy(){
		super.destroy();
		GameScene.endActorThread();
	}
	
	public void updateDisplaySize(){
		platform.updateDisplaySize();
	}

	public static void updateSystemUI() {
		platform.updateSystemUI();
	}
}