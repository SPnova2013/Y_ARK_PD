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

import static com.shatteredpixel.shatteredpixeldungeon.TomorrowRogueNight.logList;

import com.badlogic.gdx.files.FileHandle;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Base64;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.TomorrowRogueNight;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.StartScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.JsonCompress;
import com.watabou.noosa.Game;
import com.watabou.noosa.ui.Button;
import com.watabou.utils.Bundle;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.FileUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class WndGameInProgress extends Window {
	
	private static final int WIDTH    = 120;
	public static final String SEP = "::SPD::";
	//todo: turns out that encoding with base64 has many complications, one thing being that java's base64 require api 26(Android 8+)
	//todo: the current base64 is an auto-suggestion from Android Studio, which I can't seem to make it work in a short period of time, I trust you can handle the rest, GL!
	private static final boolean ENCODE_SAVE = false;
	
	private int GAP	  = 6;
	
	private float pos;

	public WndGameInProgress(final int slot){
		//logger info must be initialized for share/clear function to access
		TomorrowRogueNight.initializeLoggers(slot);

		final GamesInProgress.Info info = GamesInProgress.check(slot);

		StringBuilder sb = new StringBuilder();
		String className = null;
		if (info.subClass != null && !info.subClass.isEmpty()){
			for(HeroSubClass sc : info.subClass){
				sb.append(sc.title()).append(" ");
			}
			className = sb.toString();
		} else {
			className = info.heroClass.title();
		}
		
		IconTitle title = new IconTitle();
		title.icon( HeroSprite.avatar_de(info.heroClass, info.armorTier) );
		title.label((Messages.get(this, "title", info.level, className)).toUpperCase(Locale.KOREAN));
		title.color(Window.TITLE_COLOR);
		title.setRect( 0, 0, WIDTH, 0 );
		add(title);
		
		//manually produces debug information about a run, mainly useful for levelgen errors
		Button debug = new Button(){
			@Override
			protected boolean onLongClick() {
				try {
					Bundle bundle = FileUtils.bundleFromFile(GamesInProgress.gameFile(slot));
					TomorrowRogueNight.scene().addToFront(new WndMessage("_Debug Info:_\n\n" +
							"Version: " + Game.version + " (" + Game.versionCode + ")\n" +
							"Seed: " + bundle.getLong("seed") + "\n" +
							"Challenge Mask: " + info.challenges + "\n" +
							"SPChallenge Mask: " + info.spchallenges
							));
				} catch (IOException ignored) { }
				return true;
			}
		};
		debug.setRect(0, 0, title.imIcon.width(), title.imIcon.height);
		add(debug);

		RedButton shareLogsButton = new RedButton("Share Logs") {
			@Override
			protected void onClick() {
				shareSlotLogs(info.slot);
			}
		};
		shareLogsButton.setRect(0, pos, WIDTH, 18);
		//add(shareLogsButton);
		pos += shareLogsButton.height() + GAP;

		RedButton deleteLogsButton = new RedButton("Delete Logs") {
			@Override
			protected void onClick() {
				deleteSlotLogs(info.slot);
			}
		};
		deleteLogsButton.setRect(0, pos, WIDTH, 18);
		//add(deleteLogsButton);
		pos += deleteLogsButton.height() + GAP;

		if (info.challenges > 0 || info.spchallenges > 0) GAP -= 2;
		
		pos = title.bottom() + GAP;
		
		if (info.challenges > 0 && info.spchallenges > 0) {
			RedButton btnChallenges = new RedButton( Messages.get(this, "challenges") ) {
				@Override
				protected void onClick() {
					Game.scene().add( new WndChallenges( info.challenges, false ) );
				}
			};
			btnChallenges.icon(Icons.get(Icons.CHALLENGE_ON));
			float btnW = btnChallenges.reqWidth() + 2;
			btnChallenges.setRect( WIDTH/2f - btnW, pos, btnW , 18 );
			add( btnChallenges );
			RedButton btnChallenges2 = new RedButton( Messages.get(this, "spchallenges") ) {
				@Override
				protected void onClick() {
					Game.scene().add( new WndSPChallenges( info.spchallenges, false ) );
				}
			};
			btnChallenges2.icon(Icons.get(Icons.TALENT));
			btnW = btnChallenges2.reqWidth() + 2;
			btnChallenges2.setRect( WIDTH/2, pos, btnW , 18 );
			add( btnChallenges2 );

			pos = btnChallenges2.bottom() + GAP;
		}
		else if(info.challenges > 0){
			RedButton btnChallenges = new RedButton( Messages.get(this, "challenges") ) {
				@Override
				protected void onClick() {
					Game.scene().add( new WndChallenges( info.challenges, false ) );
				}
			};
			btnChallenges.icon(Icons.get(Icons.CHALLENGE_ON));
			float btnW = btnChallenges.reqWidth() + 2;
			btnChallenges.setRect( (WIDTH - btnW)/2, pos, btnW , 18 );
			add( btnChallenges );

			pos = btnChallenges.bottom() + GAP;
		}else if (info.spchallenges > 0) {
			RedButton btnChallenges = new RedButton( Messages.get(this, "spchallenges") ) {
				@Override
				protected void onClick() {
					Game.scene().add( new WndSPChallenges( info.spchallenges, false ) );
				}
			};
			btnChallenges.icon(Icons.get(Icons.TALENT));
			float btnW = btnChallenges.reqWidth() + 2;
			btnChallenges.setRect( (WIDTH - btnW)/2, pos, btnW , 18 );
			add( btnChallenges );

			pos = btnChallenges.bottom() + GAP;
		}

		pos += GAP;
		
		statSlot( Messages.get(this, "str"), info.str );
		if (info.shld > 0) statSlot( Messages.get(this, "health"), info.hp + "+" + info.shld + "/" + info.ht );
		else statSlot( Messages.get(this, "health"), (info.hp) + "/" + info.ht );
		statSlot( Messages.get(this, "exp"), info.exp + "/" + Hero.maxExp(info.level) );
		
		pos += GAP;
		statSlot( Messages.get(this, "gold"), info.goldCollected );
		statSlot( Messages.get(this, "depth"), info.maxDepth );
		
		pos += GAP;
		
		RedButton cont = new RedButton(Messages.get(this, "continue")){
			@Override
			protected void onClick() {
				super.onClick();
				
				GamesInProgress.curSlot = slot;
				
				Dungeon.hero = null;
				ActionIndicator.action = null;
				InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
				TomorrowRogueNight.switchScene(InterlevelScene.class);
			}
		};
		
		RedButton erase = new RedButton( Messages.get(this, "erase")){
			@Override
			protected void onClick() {
				super.onClick();
				
				TomorrowRogueNight.scene().add(new WndOptions(
						Messages.get(WndGameInProgress.class, "erase_warn_title"),
						Messages.get(WndGameInProgress.class, "erase_warn_body"),
						Messages.get(WndGameInProgress.class, "erase_warn_yes"),
						Messages.get(WndGameInProgress.class, "erase_warn_no") ) {
					@Override
					protected void onSelect( int index ) {
						if (index == 0) {
							FileUtils.deleteDir(GamesInProgress.gameFolder(slot));
							deleteSlotLogs(slot);
							GamesInProgress.setUnknown(slot);
							TomorrowRogueNight.switchNoFade(StartScene.class);
						}
					}
				} );
			}
		};

		cont.icon(Icons.get(Icons.ENTER));
		cont.setRect(0, pos, WIDTH/2 -1, 20);
		add(cont);

		erase.icon(Icons.get(Icons.CLOSE));
		erase.setRect(WIDTH/2 + 1, pos, WIDTH/2 - 1, 20);
		add(erase);

		pos += erase.height() + GAP;
		RedButton exportBtn = new RedButton(Messages.get(WndGameInProgress.class, "copy_save")) {
			@Override protected void onClick() {

				try {
					StringBuilder sb = new StringBuilder();

					// main file
					Bundle main = FileUtils.bundleFromFile(GamesInProgress.gameFile(slot));
					String mainJson = JsonCompress.compressJson(FileUtils.bundleToString(main, ENCODE_SAVE));
					sb.append("G=").append(
//							mainJson);
							Base64.encodeBase64String(mainJson.getBytes(StandardCharsets.UTF_8)));

					// depth files
					for (int d = 1; d <= info.maxDepth; d++) {
						String path = GamesInProgress.depthFile(slot, d);
						if (!FileUtils.fileExists(path)) continue;

						Bundle depth = FileUtils.bundleFromFile(path);
						String depthJson = JsonCompress.compressJson(FileUtils.bundleToString(depth, ENCODE_SAVE));

						sb.append(SEP)
								.append("D").append(d).append("=")
//								.append(depthJson);
								.append(Base64.encodeBase64String(depthJson.getBytes(StandardCharsets.UTF_8)));
					}
					Game.platform.shareTextContent(sb.toString(), "save"+slot+"_depth"+info.maxDepth+".txt");
//					com.badlogic.gdx.Gdx.app.getClipboard().setContents(sb.toString());
					Game.scene().add(new WndMessage("存档导出成功"));

				} catch (Exception e) {
					Game.scene().add(new WndMessage("存档导出失败:"+e));
				}
			}
		};
		exportBtn.setRect(2*WIDTH/3f, 0, WIDTH/3f, 8);
		add(exportBtn);

		resize(WIDTH, (int)cont.bottom()+1);
	}

	private void shareSlotLogs(int slot) {
		List<String> slotLogs = new ArrayList<>();
		for (String log : logList) {
			if (log.startsWith("slot" + slot)) {
				DeviceCompat.log("","Adding log: " + log);
				slotLogs.add(log);
			}
		}
		String outputFileName = "slot" + slot + "_logs.zip";

		Game.platform.shareZipFiles(slotLogs, outputFileName);
	}

	public void deleteSlotLogs(int slot) {
		Iterator<String> it = logList.iterator();
		while (it.hasNext()) {
			String log = it.next();
			if (log.startsWith("slot" + slot)) {
				DeviceCompat.log("","Deleting log: " + log);
				FileHandle logFileHandle = FileUtils.getFileHandle(log);
				if (logFileHandle.exists()) {
					logFileHandle.delete();
				}
				it.remove();
			}
		}
	}

	private void statSlot( String label, String value ) {
		
		RenderedTextBlock txt = PixelScene.renderTextBlock( label, 8 );
		txt.setPos(0, pos);
		add( txt );
		
		txt = PixelScene.renderTextBlock( value, 8 );
		txt.setPos(WIDTH * 0.6f, pos);
		PixelScene.align(txt);
		add( txt );
		
		pos += GAP + txt.height();
	}
	
	private void statSlot( String label, int value ) {
		statSlot( label, Integer.toString( value ) );
	}
}
