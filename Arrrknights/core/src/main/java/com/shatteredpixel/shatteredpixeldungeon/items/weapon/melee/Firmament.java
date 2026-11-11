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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChenCombo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Combo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Talu_BlackSnake;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogDzewa;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ScaleArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ChaliceOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.chimera.Shadow;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Firmament extends MeleeWeapon {

    {
        image = ItemSpriteSheet.FIRMAMENT;
        hitSound = Assets.Sounds.HIT_DUALSTRIKE;
        hitSoundPitch = 1.1f;

        tier = 2;
    }
    private int baseComboCount = 1;
    private int currentComboCount = 1;

    public int getBaseComboCount() {
        if (hasChimera(Shadow.class)) return baseComboCount + 2;
        else return baseComboCount;
    }
    public int getCurrentComboCount() {
        return currentComboCount;
    }
    public void setCurrentComboCount(int value) {
        currentComboCount = value;
    }

    //拔刀: leap in front of the target, cut it once, then let the slash run down the line.
    //exactly Chen's second combo move.
    public static final int ABILITY_RANGE = 3;
    public static final float ABILITY_DAMAGE_MULT = 1.5f;

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        if (target == null) {
            return;
        }

        final Char enemy = Actor.findChar(target);
        if (enemy == null || enemy == hero || !Dungeon.level.heroFOV[target] || hero.isCharmedBy(enemy)) {
            GLog.w(Messages.get(this, "ability_no_target"));
            return;
        }

        //work out the landing spot first, so a bad target costs nothing
        final int leapPos;
        if (hero.canAttack(enemy)) {
            leapPos = -1;
        } else {
            Ballistica path = new Ballistica(hero.pos, enemy.pos, Ballistica.PROJECTILE);
            if (path.collisionPos != enemy.pos) {
                GLog.w(Messages.get(this, "ability_no_target"));
                return;
            }
            leapPos = path.path.get(path.dist - 1);
            if (!Dungeon.level.passable[leapPos]) {
                GLog.w(Messages.get(this, "ability_no_target"));
                return;
            }
        }

        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, null);
        hero.busy();

        if (leapPos >= 0) {
            hero.sprite.jump(hero.pos, leapPos, new Callback() {
                @Override
                public void call() {
                    hero.move(leapPos);
                    Dungeon.level.occupyCell(hero);
                    Dungeon.observe();
                    GameScene.updateFog();
                    drawAndSlash(hero, enemy);
                }
            });
        } else {
            drawAndSlash(hero, enemy);
        }
    }

    //the draw itself, then the ray
    private void drawAndSlash(final Hero hero, final Char enemy) {
        hero.sprite.attack(enemy.pos, new Callback() {
            @Override
            public void call() {
                AttackIndicator.target(enemy);

                //one full hit
                if (hero.attack(enemy, 1f, 0f, Char.INFINITE_ACCURACY)) {
                    Sample.INSTANCE.play(Assets.Sounds.HIT_SLASH);
                }
                Invisibility.dispel();

                //then the slash carries on past it
                slashLine(hero, enemy.pos);

                hero.spendAndNext(hero.attackDelay());
                afterAbilityUsed(hero);
            }
        });
    }

    //the death ray, from the hero through the target and a little further
    private void slashLine(Hero hero, int target) {
        int dist = ABILITY_RANGE;
        if (hero.hasTalent(Talent.UP_EX2)) dist += hero.pointsInTalent(Talent.UP_EX2);

        Ballistica beam = new Ballistica(hero.pos, target, Ballistica.WONT_STOP);
        int maxDistance = Math.min(dist, beam.dist);
        int cell = beam.path.get(Math.min(beam.dist, maxDistance));
        hero.sprite.parent.add(new Beam.DeathRay(hero.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(cell)));

        boolean terrainAffected = false;
        ArrayList<Char> chars = new ArrayList<>();

        for (int c : beam.subPath(1, maxDistance)) {
            Char ch = Actor.findChar(c);
            if (ch != null && ch != hero && ch.isAlive()) {
                chars.add(ch);
            }

            if (Dungeon.level.flamable[c]) {
                Dungeon.level.destroy(c);
                GameScene.updateMap(c);
                terrainAffected = true;
            }

            CellEmitter.center(c).burst(PurpleParticle.BURST, Random.IntRange(1, 2));
        }

        if (terrainAffected) {
            Dungeon.observe();
        }

        float mult = ABILITY_DAMAGE_MULT;
        if (hero.hasTalent(Talent.UP_SP2)) {
            mult += hero.pointsInTalent(Talent.UP_SP2) * 0.2f;
        }
        int dmg = Math.round(hero.damageRoll() * mult);

        for (Char ch : chars) {
            ch.damage(dmg, hero);
            ch.sprite.centerEmitter().burst(PurpleParticle.BURST, Random.IntRange(1, 2));
            ch.sprite.flash();
        }

        Sample.INSTANCE.play(Assets.Sounds.HIT_SLASH);
    }

    @Override
    public String abilityInfo() {
        return Messages.get(this, "ability_desc", ABILITY_RANGE, (int) (ABILITY_DAMAGE_MULT * 100));
    }

    @Override
    public int max(int lvl) {
        if (Dungeon.hero.lvl >= 21)
            return  4*(tier+1) +    //12 + 3. 공식상 2회 타격
                    lvl*(tier+1);   //scaling unchanged
        else
        return  2+(tier+4) +    //7 + 2. 공식상 2회 타격
                lvl*(tier);   //scaling unchanged
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (Dungeon.hero.belongings.getItem(RingOfForce.class) != null && Dungeon.hero.belongings.getItem(ScaleArmor.class) != null) {
            if (Dungeon.hero.belongings.getItem(RingOfForce.class).isEquipped(Dungeon.hero) && Dungeon.hero.belongings.getItem(ScaleArmor.class).isEquipped(Dungeon.hero)) {
                damage *= 1.15f;
            }}

        int curretComboCount = getCurrentComboCount();
        if (curretComboCount > 0) {
            setCurrentComboCount(curretComboCount - 1);
            if (!attacker.attack(defender)) {
                setCurrentComboCount(getBaseComboCount());}
            else {
                defender.sprite.bloodBurstA( defender.sprite.center(), 4 );
                defender.sprite.flash();
                if (attacker instanceof Hero && Dungeon.hero.subClassSet.contains(HeroSubClass.GLADIATOR)) {
                    Buff.affect(attacker, Combo.class).bounshit(defender);
                }
                else if (attacker instanceof Hero && Dungeon.hero.heroClass == HeroClass.CHEN) {
                    Buff.affect(attacker, ChenCombo.class).bounshit(defender);
                }
            }
        }
        else setCurrentComboCount(getBaseComboCount());

        return super.proc(attacker, defender, damage);
    }


    @Override
    public String desc() {
        String info = Messages.get(this, "desc");
        if (Dungeon.hero.belongings.getItem(RingOfForce.class) != null && Dungeon.hero.belongings.getItem(ScaleArmor.class) != null) {
            if (Dungeon.hero.belongings.getItem(RingOfForce.class).isEquipped(Dungeon.hero) && Dungeon.hero.belongings.getItem(ScaleArmor.class).isEquipped(Dungeon.hero))
                info += "\n\n" + Messages.get( Firmament.class, "setbouns");}

        return info;
    }
}

