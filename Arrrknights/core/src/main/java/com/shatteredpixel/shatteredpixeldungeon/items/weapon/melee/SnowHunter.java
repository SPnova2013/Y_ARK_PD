package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Chains;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.EtherealChains;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class SnowHunter extends MeleeWeapon{
    public static final String AC_ZAP = "ZAP";
    {
        image = ItemSpriteSheet.CLIFF;
        hitSound = Assets.Sounds.HIT_WHIP;
        hitSoundPitch = 0.87f;

        defaultAction = AC_ZAP;

        tier = 4;
        RCH = 5;    //extra reach
    }

    private boolean swiching = false;

    //set while the weapon ability is resolving, its hit must not trigger the usual pull/push effect
    private boolean abilityAttack = false;

    public static final float ABILITY_PARALYSIS = 3f;

    @Override
    public int max(int lvl) {
            return  4*(tier) +            	//16 + 4
                lvl*(tier);
    }


    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_ZAP);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_ZAP)) {
            if (swiching == true) {
                swiching = false;
            } else {
                swiching = true;
            }

            updateQuickslot();
        }
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        //+(4+2*lvl) damage on top of a regular hit, roughly +25% base damage
        int dmgBoost = augment.damageFactor(4 + Math.round(2f*buffedLvl()));
        pullAbility(hero, target, 1f, dmgBoost);
    }

    @Override
    public String abilityInfo() {
        int dmgBoost = levelKnown ? 4 + Math.round(2f*buffedLvl()) : 4;
        if (levelKnown){
            return Messages.get(this, "ability_desc", augment.damageFactor(min()+dmgBoost), augment.damageFactor(max()+dmgBoost));
        } else {
            return Messages.get(this, "typical_ability_desc", min(0)+dmgBoost, max(0)+dmgBoost);
        }
    }

    //hits a target the weapon can reach, then yanks it right in front of the hero and paralyses it
    private void pullAbility(Hero hero, Integer target, float dmgMulti, int dmgBoost) {
        if (target == null) {
            return;
        }

        final Char enemy = Actor.findChar(target);
        if (enemy == null || enemy == hero || hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target]) {
            GLog.w(Messages.get(this, "ability_no_target"));
            return;
        }

        hero.belongings.abilityWeapon = this;
        if (!hero.canAttack(enemy)) {
            GLog.w(Messages.get(this, "ability_target_range"));
            hero.belongings.abilityWeapon = null;
            return;
        }
        hero.belongings.abilityWeapon = null;

        final boolean alreadyAdjacent = Dungeon.level.adjacent(hero.pos, enemy.pos);
        final int pulledPos = pullDestination(hero, enemy);

        hero.sprite.attack(enemy.pos, new Callback() {
            @Override
            public void call() {
                beforeAbilityUsed(hero, enemy);
                AttackIndicator.target(enemy);

                abilityAttack = true;
                boolean hit = hero.attack(enemy, dmgMulti, dmgBoost, Char.INFINITE_ACCURACY);
                abilityAttack = false;

                if (hit) {
                    Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
                    if (enemy.isAlive()) {
                        if (pulledPos != -1) {
                            dragEnemy(hero, enemy, pulledPos);
                        } else if (!alreadyAdjacent) {
                            GLog.w(Messages.get(this, "cant_pull"));
                        }
                        Buff.affect(enemy, Paralysis.class, ABILITY_PARALYSIS);
                    } else {
                        MeleeWeapon.onAbilityKill(hero, enemy);
                    }
                }

                Invisibility.dispel();
                hero.spendAndNext(hero.attackDelay());
                afterAbilityUsed(hero);
            }
        });
    }

    //finds the cell along the chain that is closest to the hero, -1 if the target cannot be dragged
    private int pullDestination(Hero hero, Char enemy) {

        if (enemy.properties().contains(Char.Property.IMMOVABLE)) {
            return -1;
        }

        Ballistica chain = new Ballistica(hero.pos, enemy.pos, Ballistica.STOP_TARGET);
        for (int i : chain.subPath(1, chain.dist)) {
            //prefer the earliest point on the path, so the target ends up right in front of the hero
            if (!Dungeon.level.solid[i]
                    && Actor.findChar(i) == null
                    && (!Char.hasProp(enemy, Char.Property.LARGE) || Dungeon.level.openSpace[i])) {
                return i;
            }
        }

        return -1;
    }

    //same visuals as the ethereal chains, the target is dragged along the chain
    private void dragEnemy(final Hero hero, final Char enemy, final int pulledPos) {

        //the chain is purely a visual, it must not delay the actual move: while the hero
        //waits for input the actor loop is paused on the hero, so a pushing added from a
        //render-thread callback would only resolve on the player's next action
        hero.sprite.parent.add(new Chains(hero.sprite.center(), enemy.sprite.center(), null));
        Sample.INSTANCE.play(Assets.Sounds.CHAINS);

        Actor.add(new Pushing(enemy, enemy.pos, pulledPos, new Callback() {
            @Override
            public void call() {
                enemy.pos = pulledPos;
                Dungeon.level.occupyCell(enemy);
                Dungeon.observe();
                GameScene.updateFog();
            }
        }));
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (abilityAttack) return super.proc(attacker, defender, damage);

        if (!swiching) {
            Ballistica trajectory = new Ballistica(attacker.pos, defender.pos, Ballistica.STOP_TARGET);
            trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
            moveChar(attacker, trajectory, 1, defender.pos, false, false); // 자신이 이동효과
        }
        else if (swiching) {
            final Ballistica chain = new Ballistica(defender.pos, attacker.pos, Ballistica.STOP_TARGET);
            if (Actor.findChar( chain.collisionPos ) != null) chainEnemy(chain,defender);
        }

        return super.proc(attacker, defender, damage);
    }

    private void moveChar(final Char ch, final Ballistica trajectory, int power, int enemypos,
                                 boolean closeDoors, boolean collideDmg){
        if (ch.properties().contains(Char.Property.BOSS)) {
            power /= 2;
        }

        int dist = Math.min(trajectory.dist, power);

        boolean collided = dist == trajectory.dist;

        if (dist == 0 || ch.properties().contains(Char.Property.IMMOVABLE)) return;

        //large characters cannot be moved into non-open space
        if (Char.hasProp(ch, Char.Property.LARGE)) {
            for (int i = 1; i <= dist; i++) {
                if (!Dungeon.level.openSpace[trajectory.path.get(i)]){
                    dist = i-1;
                    collided = true;
                    break;
                }
            }
        }

        if (Actor.findChar(trajectory.path.get(dist)) != null){
            dist--;
            collided = true;
        }

        if (dist < 0) return;

        final int newPos = trajectory.path.get(dist);

        if (newPos == enemypos) return;

        final int finalDist = dist;
        final boolean finalCollided = collided && collideDmg;
        final int initialpos = ch.pos;

        Actor.addDelayed(new Pushing(ch, ch.pos, newPos, new Callback() {
            public void call() {
                if (initialpos != ch.pos) {
                    //something caused movement before pushing resolved, cancel to be safe.
                    ch.sprite.place(ch.pos);
                    return;
                }
                int oldPos = ch.pos;
                ch.pos = newPos;
                if (finalCollided && ch.isAlive()) {
                    ch.damage(Random.NormalIntRange(finalDist, 2*finalDist), this);
                    Paralysis.prolong(ch, Paralysis.class, 1 + finalDist/2f);
                }
                if (closeDoors && Dungeon.level.map[oldPos] == Terrain.OPEN_DOOR){
                    Door.leave(oldPos);
                }
                Dungeon.level.occupyCell(ch);
                if (ch == Dungeon.hero){
                    //FIXME currently no logic here if the throw effect kills the hero
                    Dungeon.observe();
                }
            }
        }), -1);
    }

    public static void chainEnemy( Ballistica chain, final Char enemy ){

        if (enemy.properties().contains(Char.Property.IMMOVABLE)) {
            return;
        }

        int bestPos = -1;
        for (int i : chain.subPath(1, chain.dist)){
            //prefer to the earliest point on the path
            if (!Dungeon.level.solid[i]
                    && Actor.findChar(i) == null
                    && (!Char.hasProp(enemy, Char.Property.LARGE) || Dungeon.level.openSpace[i])){
                bestPos = i;
                break;
            }
        }

        if (bestPos == -1) {
            return;
        }

        final int pulledPos = bestPos;

        Actor.add(new Pushing(enemy, enemy.pos, pulledPos, new Callback() {
            public void call() {
                enemy.sprite.move(enemy.pos, pulledPos);
                enemy.pos = pulledPos;
                Dungeon.level.occupyCell(enemy);
                Dungeon.observe();
                GameScene.updateFog();
            }
        }));

    }

    @Override
    public String desc() {
        if (swiching) return Messages.get(this, "desc_mode");
        else return Messages.get(this, "desc");
    }

    @Override
    public String status() {
        if (Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.IRENE) {
            return super.status();
        }
        if (this.isIdentified()) {
            if (swiching) return "EX";
            else return "NM";
        }
        else return null;}

    private static final String SWICH = "swiching";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SWICH, swiching);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        swiching = bundle.getBoolean(SWICH);
    }

}
