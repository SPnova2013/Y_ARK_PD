package com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK2;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArcaneArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK1.Thoughts;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.Skill;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CursingTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

public class LandingStrike extends Skill {
    public void doSkill() {
        GameScene.selectCell(Slash);
        CursingTrap.curse(curUser);
        int curs = 0;
        if (curUser.belongings.weapon != null) if(curUser.belongings.weapon.cursed) curs++;
        if (curUser.belongings.armor != null) if(curUser.belongings.armor.cursed) curs++;
        if (curUser.belongings.artifact != null) if(curUser.belongings.artifact.cursed) curs++;
        if (curUser.belongings.ring != null) if(curUser.belongings.ring.cursed) curs++;
        if (curUser.belongings.misc != null)  if(curUser.belongings.misc.cursed) curs++;

        Buff.affect(curUser, Bless.class, 150 + curs * 250f);

        CellEmitter.get( curUser.pos ).start( ShadowParticle.CURSE, 0.05f, 10 );
        Sample.INSTANCE.play( Assets.Sounds.SKILL_BASIC );
    }
    protected CellSelector.Listener Slash = new  CellSelector.Listener() {
        @Override
        public void onSelect( Integer target ) {
            if (target == null){
                return;
            }
            // 移除距离限制，只检查是否超出武器攻击范围+1（跳跃距离）
            int maxDistance = hero.belongings.weapon.reachFactor(hero) + 1;
            if (hero.rooted || Dungeon.level.distance(hero.pos, target) > maxDistance){
                GLog.w(Messages.get(LandingStrike.class, "out_of_range"));
                if (hero.rooted) Camera.main.shake( 1, 1f );
                return;
            }
            Char enemy = Actor.findChar(target);
            if (Dungeon.level.heroFOV[target]) {
                if (enemy == null || enemy == hero || hero.isCharmedBy(enemy)) {
                    GLog.w(Messages.get(LandingStrike.class, "not_a_target"));
                    return;
                }
            }

            int lungeCell = -1;
            // 对于相邻目标，可以选择原地或相邻位置
            if (Dungeon.level.distance(hero.pos, target) == 1) {
                // 如果就在目标旁边，可以选择原地攻击
                lungeCell = hero.pos;
            } else {
                // 对于较远目标，寻找合适的跳跃位置
                for (int i : PathFinder.NEIGHBOURS9){
                    if (Dungeon.level.distance(hero.pos+i, target) <= hero.belongings.weapon.reachFactor(hero)
                            && Actor.findChar(hero.pos+i) == null
                            && (Dungeon.level.passable[hero.pos+i] || (Dungeon.level.avoid[hero.pos+i] && hero.flying))){
                        if (lungeCell == -1 || Dungeon.level.trueDistance(hero.pos + i, target) < Dungeon.level.trueDistance(lungeCell, target)){
                            lungeCell = hero.pos + i;
                        }
                    }
                }
            }

            if (lungeCell == -1){
                GLog.w(Messages.get(LandingStrike.class, "out_of_range"));
                return;
            }
            final int dest = lungeCell;

            hero.busy();

            // 如果目标就在当前位置，直接攻击而不跳跃
            if (dest == hero.pos) {
                MeleeWeapon wep = (MeleeWeapon)hero.belongings.weapon;
                if (enemy != null && hero.canAttack(enemy)) {
                    hero.sprite.attack(enemy.pos, new Callback() {
                        @Override
                        public void call() {
                            AttackIndicator.target(enemy);
                            if (hero.attack(enemy, 2f, 1f, Char.INFINITE_ACCURACY)) {
                                Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
                                if (!enemy.isAlive()) {
                                    //wep.onAbilityKill(hero, enemy);
                                }
                            }
                            if (hero.attack(enemy, 2f, 1f, Char.INFINITE_ACCURACY)) {
                                Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
                                if (!enemy.isAlive()) {
                                    //wep.onAbilityKill(hero, enemy);
                                }
                            }
                            Invisibility.dispel();
                            hero.spendAndNext(hero.attackDelay());
                            //wep.afterAbilityUsed(hero);
                        }
                    });
                } else {
                    updateQuickslot();
                    GLog.w(Messages.get(LandingStrike.class, "ability_no_target"));
                    hero.spendAndNext(1/hero.speed());
                }
                return;
            }

            // 执行跳跃攻击
            hero.sprite.jump(hero.pos, dest, 0.5f, 0.2f, new Callback() {
                @Override
                public void call() {
                    if (Dungeon.level.map[hero.pos] == Terrain.OPEN_DOOR) {
                        Door.leave( hero.pos );
                    }
                    hero.pos = dest;
                    Dungeon.level.occupyCell(hero);
                    Dungeon.observe();

                    MeleeWeapon wep = (MeleeWeapon)hero.belongings.weapon;
                    if (enemy != null && hero.canAttack(enemy)) {
                        hero.sprite.attack(enemy.pos, new Callback() {
                            @Override
                            public void call() {
                                AttackIndicator.target(enemy);
                                if (hero.attack(enemy, 2f, 1f, Char.INFINITE_ACCURACY)) {
                                    Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
                                    if (!enemy.isAlive()) {
                                        //wep.onAbilityKill(hero, enemy);
                                    }
                                }
                                if (hero.attack(enemy, 2f, 1f, Char.INFINITE_ACCURACY)) {
                                    Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
                                    if (!enemy.isAlive()) {
                                        //wep.onAbilityKill(hero, enemy);
                                    }
                                }
                                Invisibility.dispel();
                                hero.spendAndNext(hero.attackDelay());
                                //wep.afterAbilityUsed(hero);
                            }
                        });
                    } else {
                        updateQuickslot();
                        GLog.w(Messages.get(LandingStrike.class, "ability_no_target"));
                        hero.spendAndNext(1/hero.speed());
                    }
                }
            });
        }
        @Override
        public String prompt() {
            return Messages.get(Thoughts.class, "prompt");
        }
    };
}