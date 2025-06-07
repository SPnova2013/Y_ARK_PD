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

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.BadgeBanner;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.JessiSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.ArchSkinSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.AstesiaSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.FnovaSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.FrankaSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.FrostLeafSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.GraniSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.HinaSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.KayokoSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.LappySprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.LilithSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.MudrockSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.NeuroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.SchwarzSkinSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.SkadiSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.SpecterSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.SussurroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.TenmaSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.TlipocaSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.TomimiSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.WeedySkinSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.WisadelSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.talrufightSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;

public class WndBadge extends Window {
	
	private static final int WIDTH = 120;
	private static final int MARGIN = 4;
	
	public WndBadge( Badges.Badge badge, boolean unlocked ) {

		super();

		Image icon = BadgeBanner.image(badge.image);
		icon.scale.set(2);
		if (!unlocked) icon.brightness(0.4f);
		add(icon);

		if (!badge.skin) {
			RenderedTextBlock info = PixelScene.renderTextBlock(badge.desc(), 8);
			info.maxWidth(WIDTH - MARGIN * 2);
			info.align(RenderedTextBlock.CENTER_ALIGN);
			PixelScene.align(info);
			if (!unlocked) info.hardlight(0x888888);
			add(info);

			float w = Math.max(icon.width(), info.width()) + MARGIN * 2;

			icon.x = (w - icon.width()) / 2f;
			icon.y = MARGIN;
			PixelScene.align(icon);

			info.setPos((w - info.width()) / 2, icon.y + icon.height() + MARGIN);
			resize((int) w, (int) (info.bottom() + MARGIN));
		} else {
			if (Dungeon.hero.CharSkin == 0) {
				if (unlocked) {
					RenderedTextBlock info = PixelScene.renderTextBlock(badge.Skindesc_change(), 8);
					info.maxWidth(WIDTH - MARGIN * 2);
					info.align(RenderedTextBlock.CENTER_ALIGN);
					PixelScene.align(info);
					add(info);

					float w = Math.max(icon.width(), info.width()) + MARGIN * 2;

					icon.x = (w - icon.width()) / 2f;
					icon.y = MARGIN;
					PixelScene.align(icon);

					info.setPos((w - info.width()) / 2, icon.y + icon.height() + MARGIN);
					resize((int) w, (int) (info.bottom() + MARGIN));

					ChangeSkin(badge);
				} else {
					RenderedTextBlock info = PixelScene.renderTextBlock(badge.Skindesc_lock(), 8);
					info.maxWidth(WIDTH - MARGIN * 2);
					info.align(RenderedTextBlock.CENTER_ALIGN);
					PixelScene.align(info);
					add(info);

					float w = Math.max(icon.width(), info.width()) + MARGIN * 2;

					icon.x = (w - icon.width()) / 2f;
					icon.y = MARGIN;
					PixelScene.align(icon);

					info.setPos((w - info.width()) / 2, icon.y + icon.height() + MARGIN);
					resize((int) w, (int) (info.bottom() + MARGIN));
				}
			} else {
					RenderedTextBlock info = PixelScene.renderTextBlock(badge.Skindesc_Default(), 8);
					info.maxWidth(WIDTH - MARGIN * 2);
					info.align(RenderedTextBlock.CENTER_ALIGN);
					PixelScene.align(info);
					add(info);

					float w = Math.max(icon.width(), info.width()) + MARGIN * 2;

					icon.x = (w - icon.width()) / 2f;
					icon.y = MARGIN;
					PixelScene.align(icon);

					info.setPos((w - info.width()) / 2, icon.y + icon.height() + MARGIN);
					resize((int) w, (int) (info.bottom() + MARGIN));

					ChangeSkin();

			}

			if (unlocked) BadgeBanner.highlight(icon, badge.image);
		}
	}

	public void ChangeSkin() {
		Dungeon.hero.CharSkin = 0;
		Dungeon.hero.CharSkinClass = null;
	}

	public void ChangeSkin( Badges.Badge badge) {
		switch (badge) {
			case SKIN_TALU:
				Dungeon.hero.CharSkin = Hero.TALULAH;
				Dungeon.hero.CharSkinClass = new talrufightSprite();
				break;
			case SKIN_NOVA:
				Dungeon.hero.CharSkin = Hero.F_NOVA;
				Dungeon.hero.CharSkinClass = new FnovaSprite();
				break;
			case SKIN_BABOSKADI:
				Dungeon.hero.CharSkin = Hero.SKADI;
				Dungeon.hero.CharSkinClass = new SkadiSprite();
				break;
			case SKIN_SUSUU:
				Dungeon.hero.CharSkin = Hero.SSR;
				Dungeon.hero.CharSkinClass = new SussurroSprite();
				break;
			case SKIN_GRN:
				Dungeon.hero.CharSkin = Hero.GRANI;
				Dungeon.hero.CharSkinClass = new GraniSprite();
				break;
			case SKIN_JESSI:
				Dungeon.hero.CharSkin = Hero.JESSI;
				Dungeon.hero.CharSkinClass = new JessiSprite();
				break;
			case SKIN_LAPPY:
				Dungeon.hero.CharSkin = Hero.LAPPY;
				Dungeon.hero.CharSkinClass = new LappySprite();
				break;
			case SKIN_LEAF:
				Dungeon.hero.CharSkin = Hero.LEAF;
				Dungeon.hero.CharSkinClass = new FrostLeafSprite();
				break;
			case SKIN_MUDROCK:
				Dungeon.hero.CharSkin = Hero.MUDROCK;
				Dungeon.hero.CharSkinClass = new MudrockSprite();
				break;
			case SKIN_ASTESIA:
				Dungeon.hero.CharSkin = Hero.AST;
				Dungeon.hero.CharSkinClass = new AstesiaSprite();
				break;
			case SKIN_SPECTER:
				Dungeon.hero.CharSkin = Hero.SPT;
				Dungeon.hero.CharSkinClass = new SpecterSprite();
				break;
			case SKIN_SCHWARZ:
				Dungeon.hero.CharSkin = Hero.SCHWARZ;
				Dungeon.hero.CharSkinClass = new SchwarzSkinSprite();
				break;
			case SKIN_ARCH:
				Dungeon.hero.CharSkin = Hero.ARCH;
				Dungeon.hero.CharSkinClass = new ArchSkinSprite();
				break;
			case SKIN_TOMIMI:
				Dungeon.hero.CharSkin = Hero.TOMIMI;
				Dungeon.hero.CharSkinClass = new TomimiSprite();
				break;
			case SKIN_FRANKA:
				Dungeon.hero.CharSkin = Hero.FRANKA;
				Dungeon.hero.CharSkinClass = new FrankaSprite();
				break;
			case SKIN_WEEDY:
				Dungeon.hero.CharSkin = Hero.WEEDY;
				Dungeon.hero.CharSkinClass = new WeedySkinSprite();
				break;
			case SKIN_LILITH:
				Dungeon.hero.CharSkin = Hero.LILITH;
				Dungeon.hero.CharSkinClass = new LilithSprite();
				break;
			case SKIN_KAYOKO:
				Dungeon.hero.CharSkin = Hero.KAYOKO;
				Dungeon.hero.CharSkinClass = new KayokoSprite();
				break;
			case SKIN_HINA:
				Dungeon.hero.CharSkin = Hero.HINA;
				Dungeon.hero.CharSkinClass = new HinaSprite();
				break;
			case SKIN_TLIPOCA:
				Dungeon.hero.CharSkin = Hero.TLIPOCA;
				Dungeon.hero.CharSkinClass = new TlipocaSprite();
				break;
			case SKIN_NEURO:
				Dungeon.hero.CharSkin = Hero.NEURO;
				Dungeon.hero.CharSkinClass = new NeuroSprite();
				break;
			case SKIN_WISADEL:
				Dungeon.hero.CharSkin = Hero.WISADEL;
				Dungeon.hero.CharSkinClass = new WisadelSprite();
				break;
			case SKIN_TENMA:
				Dungeon.hero.CharSkin = Hero.TENMA;
				Dungeon.hero.CharSkinClass = new TenmaSprite();
				break;
		}
	}
}
