package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class ChenSword extends MeleeWeapon {
    {
        image = ItemSpriteSheet.SHORTSWORD;
        hitSound = Assets.Sounds.HIT_SWORD;
        hitSoundPitch = 1.1f;

        tier = 1;

        bones = false;
    }

    //鞘击: the same opening move Chen's first combo step makes
    public static final float ABILITY_PARALYSIS = 1f;

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    public String abilityInfo() {
        return Messages.get(this, "ability_desc", (int) ABILITY_PARALYSIS);
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
                    strike(hero, enemy);
                }
            });
        } else {
            strike(hero, enemy);
        }
    }

    //the draw itself: one cut, then the target is pinned
    private void strike(final Hero hero, final Char enemy) {
        hero.sprite.attack(enemy.pos, new Callback() {
            @Override
            public void call() {
                AttackIndicator.target(enemy);

                if (hero.attack(enemy, 1f, 0f, Char.INFINITE_ACCURACY)) {
                    Sample.INSTANCE.play(Assets.Sounds.HIT_DUALSTRIKE, 1.25f, 1.33f);
                }
                Invisibility.dispel();

                if (enemy.isAlive()) {
                    Buff.affect(enemy, Paralysis.class, ABILITY_PARALYSIS);
                    enemy.sprite.centerEmitter().burst(Speck.factory(Speck.LIGHT), 4);
                }

                hero.spendAndNext(hero.attackDelay());
                afterAbilityUsed(hero);
            }
        });
    }
}
