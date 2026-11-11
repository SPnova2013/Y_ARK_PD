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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import static com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent.PROFICIENCY;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfFuror;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

import javax.crypto.Mac;

public class Enfild extends MeleeWeapon {

    {
        image = ItemSpriteSheet.ENFILD;
        hitSound = Assets.Sounds.SKILL_BEEP;
        hitSoundPitch = 0.9f;

        tier = 2;
        DLY = 1.5f; //0.67x speed
        RCH = 50;    //extra reach
    }

    @Override
    public int min(int lvl) { return  9 + buffedLvl() * 2 + Maccessories +
            ((Dungeon.hero.hasTalent(Talent.PROFICIENCY)&& Maccessories > 2) ? Maccessories : 0); }

    @Override
    public int max(int lvl) {return  9 + buffedLvl() * 2 + 2* Maccessories +
            ((Dungeon.hero.hasTalent(Talent.PROFICIENCY)&& Maccessories > 1) ? Maccessories : 0); }
    @Override
    public int STRReq(int lvl){
        int strreq=STRReq(tier, lvl);//change from budding
        strreq += Maccessories;
        return strreq;//change from budding
    }
    @Override
    public float speedFactor( Char owner ) {
        float delay = super.speedFactor( owner );
        if(Dungeon.hero.hasTalent(PROFICIENCY)){
            delay *= 1/(1.1f+Math.pow(Dungeon.hero.pointsInTalent(PROFICIENCY),2)*0.1f);
        }
        return delay;
    }

    //阻挡弹: every hit pins whatever it touches in place
    public static final int ABILITY_DURATION = 10;
    public static final float ABILITY_ROOTS = 1f;

    //a free action: the round is simply loaded
    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, null);

        Buff.prolong(hero, BlockingRound.class, ABILITY_DURATION);

        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
        Invisibility.dispel();

        afterAbilityUsed(hero);
        //no spendAndNext here on purpose, loading a round costs no turn
    }

    @Override
    public String abilityInfo() {
        return Messages.get(this, "ability_desc", ABILITY_DURATION, (int) ABILITY_ROOTS);
    }

    //阻挡弹: the loaded round itself
    public static class BlockingRound extends FlavourBuff {

        {
            type = buffType.POSITIVE;
            announced = true;
        }

        @Override
        public int icon() {
            return BuffIndicator.WEAPON;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (ABILITY_DURATION - visualcooldown()) / (float) ABILITY_DURATION);
        }
    }

    @Override
    public int value() { return super.value() + 60; }

    @Override
    public boolean isUpgradable() { return true; }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (charge >= chargeCap) {
            damage *= 1.3f;
            charge = 0;
        }
        else SPCharge(20);

        //a blocking round roots whatever it hits
        if (attacker.buff(BlockingRound.class) != null && defender.isAlive()) {
            Buff.affect(defender, Roots.class, ABILITY_ROOTS);
        }

        return super.proc(attacker, defender, damage);
    }

    @Override
    public String status() {

        //if the artifact isn't IDed, or is cursed, don't display anything
        if (!isIdentified() || cursed) {
            return null;
        }
        //display as percent
        if (chargeCap == 100)
            return Messages.format("%d%%", charge);


        //otherwise, if there's no charge, return null.
        return null;
    }
    @Override
    public String statusBottom() {

        if (!isIdentified() || cursed || Dungeon.hero == null) {
            return null;
        }
        Charger charger = Dungeon.hero.buff(Charger.class);
        if (charger == null) {
            return null;
        }
        return Integer.toString(Math.min(charger.charges, charger.chargeCap()));
    }
}
