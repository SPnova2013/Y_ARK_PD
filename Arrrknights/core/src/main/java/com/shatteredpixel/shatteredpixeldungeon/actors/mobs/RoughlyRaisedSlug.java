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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.Bug_ASprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RoughlyRaisedSlugSprite;
import com.watabou.noosa.MovieClip;

public class RoughlyRaisedSlug extends Slug {
    private Mob curMob;
    {
        spriteClass = RoughlyRaisedSlugSprite.class;

        HP = HT = 15;
        hthpIncRate = 90;
        EXP = 2;
        loot = new MysteryMeat();
        lootChance = 1;
        WANDERING = new RoughlyRaisedSlug.Wandering();
        FLEEING = new RoughlyRaisedSlug.Fleeing();
        properties.add(Property.INFECTED);
        curMob = this;
    }

    @Override
    public void damage(int dmg, Object src) {
        super.damage(dmg, src);
        if (state != FLEEING && HP * 2 < HT) state = FLEEING;
        ((RoughlyRaisedSlugSprite)curMob.sprite).switchToEscapeSprite();
    }

    @Override
    public float speed() {
        if (HP * 2 < HT) return (4 * super.speed()) / 3;
        else return super.speed();
    }

    private class Wandering extends Mob.Wandering {

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            super.act(enemyInFOV, justAlerted);

            //if an enemy is just noticed and the thief posses an item, run, don't fight.
            if (state == HUNTING && HP * 2 < HT) {
                state = FLEEING;
                ((RoughlyRaisedSlugSprite)curMob.sprite).switchToEscapeSprite();
            }

            return true;
        }
    }

    private class Fleeing extends Mob.Fleeing {
        @Override
        protected void nowhereToRun() {
            if (buff(Terror.class) == null && buff(Corruption.class) == null) {
                if (enemySeen) {
                    sprite.showStatus(CharSprite.NEGATIVE, Messages.get(Mob.class, "rage"));
                    state = HUNTING;
                } else if (HP * 2 < HT
                        && !Dungeon.level.heroFOV[pos]
                        && Dungeon.level.distance(Dungeon.hero.pos, pos) >= 6) {

                    int count = 32;
                    int newPos;
                    do {
                        newPos = Dungeon.level.randomRespawnCell(RoughlyRaisedSlug.this);
                        if (count-- <= 0) {
                            break;
                        }
                    } while (newPos == -1 || Dungeon.level.heroFOV[newPos] || Dungeon.level.distance(newPos, pos) < (count / 3));

                    if (newPos != -1) {

                        if (Dungeon.level.heroFOV[pos])
                            CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 6);
                        pos = newPos;
                        sprite.place(pos);
                        sprite.visible = Dungeon.level.heroFOV[pos];
                        if (Dungeon.level.heroFOV[pos])
                            CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 6);

                    }
                    destroy();
                    sprite.killAndErase();
                    Dungeon.level.mobs.remove(RoughlyRaisedSlug.this);
                } else {
                    state = WANDERING;
                }
            } else {
                super.nowhereToRun();
            }
        }
    }
}


