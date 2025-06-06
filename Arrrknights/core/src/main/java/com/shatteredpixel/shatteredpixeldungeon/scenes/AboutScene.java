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

import com.shatteredpixel.shatteredpixeldungeon.TomorrowRogueNight;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.DeviceCompat;

public class AboutScene extends PixelScene {

	@Override
	public void create() {
		super.create();

		final float colWidth = 120;
		final float fullWidth = colWidth * (landscape() ? 2 : 1);

		int w = Camera.main.width;
		int h = Camera.main.height;

		ScrollPane list = new ScrollPane( new Component() );
		add( list );

		Component content = list.content();
		content.clear();


		//*** Shattered Pixel Dungeon Credits ***

		String shpxLink = "https://ShatteredPixel.com";
		//tracking codes, so that the website knows where this pageview came from
		shpxLink += "?utm_source=shatteredpd";
		shpxLink += "&utm_medium=about_page";
		shpxLink += "&utm_campaign=ingame_link";

		CreditsBlock shpx = new CreditsBlock(true, Window.SHPX_COLOR,
				"Shattered Pixel Dungeon",
				Icons.SHPX.get(),
				"Developed by: _Evan Debenham_\nBased on Pixel Dungeon's open source",
				"ShatteredPixel.com",
				shpxLink);
		shpx.setRect((w - fullWidth)/2f, 6, 120, 0);
		content.add(shpx);

		CreditsBlock alex = new CreditsBlock(false, Window.SHPX_COLOR,
				"Hero Art & Design:",
				Icons.ALEKS.get(),
				"Aleksandar Komitov",
				"alekskomitov.com",
				"https://www.alekskomitov.com");
		alex.setSize(colWidth/2f, 0);
		if (landscape()){
			alex.setPos(shpx.right(), shpx.top() + (shpx.height() - alex.height())/2f);
		} else {
			alex.setPos(w/2f - colWidth/2f, shpx.bottom()+5);
		}
		content.add(alex);

		CreditsBlock charlie = new CreditsBlock(false, Window.SHPX_COLOR,
				"Sound Effects:",
				Icons.CHARLIE.get(),
				"Charlie",
				"s9menine.itch.io",
				"https://s9menine.itch.io");
		charlie.setRect(alex.right(), alex.top(), colWidth/2f, 0);
		content.add(charlie);


		CreditsBlock yarkpd = new CreditsBlock(true, Window.Y_ARKPD_COLOR,
				"Yesterday's RogueNights",
				Icons.ARKPD.get(),
				"Developed by: _R'lyeh Text_\nBased on _Tomorrow's RogueNights_ by Namsek\nand _ARK PD_ by Budding's open source",
				"",
				null);
		if (landscape()){
			yarkpd.setRect((w - fullWidth)/2f, shpx.bottom() + 8, 120, 0);
		} else {
			yarkpd.setRect((w - fullWidth)/2f, charlie.bottom() + 8, 120, 0);
		}
		content.add(yarkpd);

		CreditsBlock jinkeloid = new CreditsBlock(false, Window.Y_ARKPD_COLOR,
				"Code Assistance:",
				Icons.JINKELOID.get(),
				"Jinkeloid",
				"GitHub",
				"https://github.com/CatAzreal");
		jinkeloid.setSize(colWidth/2f, 0);
		if (landscape()){
			jinkeloid.setPos(yarkpd.right() + 10, yarkpd.top() + (yarkpd.height() - jinkeloid.height())/2f);
		} else {
			jinkeloid.setPos(w/2f - colWidth/2f, yarkpd.bottom() + 8);
		}
		content.add(jinkeloid);

		CreditsBlock sabai = new CreditsBlock(false, Window.Y_ARKPD_COLOR,
				"Art Assistance:",
				Icons.SABAI.get(),
				"SabaiMomoi",
				"GitHub",
				"https://github.com/SabaiMomoi");
		sabai.setRect(jinkeloid.right() + 5, jinkeloid.top(), colWidth/2f, 0);
		content.add(sabai);

		addLine(yarkpd.top() - 2, content);

		CreditsBlock rogu = new CreditsBlock(true, Window.ARKPD_COLOR,
				"Tomorrow's RogueNights",
				Icons.ARKPD.get(),
				"Developed by: _Team Rosemar_\nBased on Shattered Pixel Dungeon's open source",
				"",
				null);
		if (landscape()) rogu.setRect((w - fullWidth)/2f, yarkpd.bottom() + 8, 120, 0);
		else rogu.setRect((w - fullWidth)/2f, sabai.bottom() + 8, 120, 0);
		content.add(rogu);

		CreditsBlock nam = new CreditsBlock(false, Window.ARKPD_COLOR,
				"Hero Art & Design:",
				Icons.NAMSEK.get(),
				"NamSek",
				"pixiv.net",
				"https://www.pixiv.net/users/14086167");
		nam.setSize(colWidth/2f, 0);
		if (landscape()){
			nam.setPos(rogu.right() + 10, rogu.top() + (rogu.height() - nam.height())/2f);
		} else {
			nam.setPos(w/2f - colWidth/2f, rogu.bottom() + 8);
		}
		content.add(nam);

		CreditsBlock mizq = new CreditsBlock(false, Window.SHPX_COLOR,
				"Programming:",
				Icons.MIZQ.get(),
				"mizq4482",
				"blog",
				"https://mizq4482.tistory.com/");
		mizq.setRect(nam.right() + 5, nam.top(), colWidth/2f, 0);
		content.add(mizq);

		addLine(rogu.top() - 2, content);



		final int HYPER_COLOR = 0xFFFFFF;
		CreditsBlock hyper = new CreditsBlock(true, HYPER_COLOR,
				"Hypergryph",
				Icons.HYPER.get(),
				"Graphic & Sound ip used\n",
				"hypergryph.com",
				"https://ak.hypergryph.com/");
		if (landscape()){
			hyper.setRect(shpx.left(), rogu.bottom() + 8, colWidth, 0);
		} else {
			hyper.setRect(shpx.left(), mizq.bottom() + 8, colWidth, 0);
		}
		content.add(hyper);

		addLine(hyper.top() - 4, content);

		//*** libGDX Credits ***

		final int GDX_COLOR = 0xE44D3C;
		CreditsBlock gdx = new CreditsBlock(true,
				GDX_COLOR,
				null,
				Icons.LIBGDX.get(),
				"ShatteredPD is powered by _libGDX_!",
				"libGDX.com",
				"https://libGDX.com/");
		if (landscape()){
			gdx.setRect(hyper.left(), hyper.bottom() + 8, colWidth, 0);
		} else {
			gdx.setRect(hyper.left(), hyper.bottom() + 8, colWidth, 0);
		}
		content.add(gdx);

		addLine(gdx.top() - 4, content);

		//blocks the rays from the LibGDX icon going above the line
		ColorBlock blocker = new ColorBlock(w, 8, 0xFF000000);
		blocker.y = gdx.top() - 12;
		content.addToBack(blocker);
		content.sendToBack(gdx);

		CreditsBlock arcnor = new CreditsBlock(false, GDX_COLOR,
				"Pixel Dungeon GDX:",
				Icons.ARCNOR.get(),
				"Edu García",
				"twitter.com/arcnor",
				"https://twitter.com/arcnor");
		arcnor.setSize(colWidth/2f, 0);
		if (landscape()){
			arcnor.setPos(gdx.right(), gdx.top() + (gdx.height() - arcnor.height())/2f);
		} else {
			arcnor.setPos(alex.left(), gdx.bottom()+5);
		}
		content.add(arcnor);

		CreditsBlock purigro = new CreditsBlock(false, GDX_COLOR,
				"Shattered GDX Help:",
				Icons.PURIGRO.get(),
				"Kevin MacMartin",
				"github.com/prurigro",
				"https://github.com/prurigro/");
		purigro.setRect(arcnor.right()+2, arcnor.top(), colWidth/2f, 0);
		content.add(purigro);

		//*** Pixel Dungeon Credits ***

		final int WATA_COLOR = 0x55AAFF;
		CreditsBlock wata = new CreditsBlock(true, WATA_COLOR,
				"Pixel Dungeon",
				Icons.WATA.get(),
				"Developed by: _Watabou_\nInspired by Brian Walker's Brogue",
				"pixeldungeon.watabou.ru",
				"http://pixeldungeon.watabou.ru");
		if (landscape()){
			wata.setRect(shpx.left(), purigro.bottom() + 8, colWidth, 0);
		} else {
			wata.setRect(shpx.left(), purigro.bottom() + 8, colWidth, 0);
		}
		content.add(wata);

		addLine(wata.top() - 4, content);

		CreditsBlock cube = new CreditsBlock(false, WATA_COLOR,
				"Music:",
				Icons.CUBE_CODE.get(),
				"Cube Code",
				null,
				null);
		cube.setSize(colWidth/2f, 0);
		if (landscape()){
			cube.setPos(wata.right(), wata.top() + (wata.height() - cube.height())/2f);
		} else {
			cube.setPos(wata.left(), wata.bottom()+5);
		}
		content.add(cube);


		//*** Transifex Credits ***

		CreditsBlock transifex = new CreditsBlock(true,
				Window.TITLE_COLOR,
				null,
				null,
				"",
				"",
				"");
		transifex.setRect((Camera.main.width - colWidth)/2f, wata.bottom() +4 , colWidth, 0);
		content.add(transifex);

		addLine(transifex.top() - 4, content);



		transifex.setRect(transifex.left()-10, transifex.bottom() + 8, colWidth+20, 0);
		content.add(transifex);

		content.setSize( fullWidth, transifex.bottom()+10 );

		list.setRect( 0, 0, w, h );
		list.scrollTo(0, 0);

		ExitButton btnExit = new ExitButton();
		btnExit.setPos( Camera.main.width - btnExit.width(), 0 );
		add( btnExit );

		fadeIn();
	}
	
	@Override
	protected void onBackPressed() {
		TomorrowRogueNight.switchScene(TitleScene.class);
	}

	private void addLine( float y, Group content ){
		ColorBlock line = new ColorBlock(Camera.main.width, 1, 0xFF333333);
		line.y = y;
		content.add(line);
	}

	private static class CreditsBlock extends Component {

		boolean large;
		RenderedTextBlock title;
		Image avatar;
		Flare flare;
		RenderedTextBlock body;

		RenderedTextBlock link;
		ColorBlock linkUnderline;
		PointerArea linkButton;

		//many elements can be null, but body is assumed to have content.
		private CreditsBlock(boolean large, int highlight, String title, Image avatar, String body, String linkText, String linkUrl){
			super();

			this.large = large;

			if (title != null) {
				this.title = PixelScene.renderTextBlock(title, large ? 8 : 6);
				if (highlight != -1) this.title.hardlight(highlight);
				add(this.title);
			}

			if (avatar != null){
				this.avatar = avatar;
				add(this.avatar);
			}

			if (large && highlight != -1 && this.avatar != null){
				this.flare = new Flare( 7, 24 ).color( highlight, true ).show(this.avatar, 0);
				this.flare.angularSpeed = 20;
			}

			this.body = PixelScene.renderTextBlock(body, 6);
			if (highlight != -1) this.body.setHightlighting(true, highlight);
			if (large) this.body.align(RenderedTextBlock.CENTER_ALIGN);
			add(this.body);

			if (linkText != null && linkUrl != null){

				int color = 0xFFFFFFFF;
				if (highlight != -1) color = 0xFF000000 | highlight;
				this.linkUnderline = new ColorBlock(1, 1, color);
				add(this.linkUnderline);

				this.link = PixelScene.renderTextBlock(linkText, 6);
				if (highlight != -1) this.link.hardlight(highlight);
				add(this.link);

				linkButton = new PointerArea(0, 0, 0, 0){
					@Override
					protected void onClick( PointerEvent event ) {
						TomorrowRogueNight.platform.openURI(linkUrl);
					}
				};
				add(linkButton);
			}

		}

		@Override
		protected void layout() {
			super.layout();

			float topY = top();

			if (title != null){
				title.maxWidth((int)width());
				title.setPos( x + (width() - title.width())/2f, topY);
				topY += title.height() + (large ? 2 : 1);
			}

			if (large){

				if (avatar != null){
					avatar.x = x + (width()-avatar.width())/2f;
					avatar.y = topY;
					PixelScene.align(avatar);
					if (flare != null){
						flare.point(avatar.center());
					}
					topY = avatar.y + avatar.height() + 2;
				}

				body.maxWidth((int)width());
				body.setPos( x + (width() - body.width())/2f, topY);
				topY += body.height() + 2;

			} else {

				if (avatar != null){
					avatar.x = x;
					body.maxWidth((int)(width() - avatar.width - 1));

					if (avatar.height() > body.height()){
						avatar.y = topY;
						body.setPos( avatar.x + avatar.width() + 1, topY + (avatar.height() - body.height())/2f);
						topY += avatar.height() + 1;
					} else {
						avatar.y = topY + (body.height() - avatar.height())/2f;
						PixelScene.align(avatar);
						body.setPos( avatar.x + avatar.width() + 1, topY);
						topY += body.height() + 2;
					}

				} else {
					topY += 1;
					body.maxWidth((int)width());
					body.setPos( x, topY);
					topY += body.height()+2;
				}

			}

			if (link != null){
				if (large) topY += 1;
				link.maxWidth((int)width());
				link.setPos( x + (width() - link.width())/2f, topY);
				topY += link.height() + 2;

				linkButton.x = link.left()-1;
				linkButton.y = link.top()-1;
				linkButton.width = link.width()+2;
				linkButton.height = link.height()+2;

				linkUnderline.size(link.width(), PixelScene.align(0.49f));
				linkUnderline.x = link.left();
				linkUnderline.y = link.bottom()+1;

			}

			topY -= 2;

			height = Math.max(height, topY - top());
		}
	}
}
