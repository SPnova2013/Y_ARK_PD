package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hallucination;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.Bonk;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CustomeSet;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DroneSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.Glow_dronSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.Rock_CrabSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Echeveria extends MeleeWeapon{
    public static final String AC_SUMON = "SUMON";
    public static float partialcharge_ech = 0;//change from budding
    {
        image = ItemSpriteSheet.ECHEVERIA;
        hitSound = Assets.Sounds.ATK_SPIRITBOW;
        hitSoundPitch = 1.32f;

        tier = 5;
        RCH = 2;

        defaultAction = AC_SUMON;
        usesTargeting = true;
    }

    protected int collisionProperties = Ballistica.STOP_TARGET;

    //澄净闪耀: two drones that pick their own prey
    public static final int ABILITY_SUMMONS = 2;

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        //nothing to point at means nothing to call out
        if (!enemyInSight()) {
            GLog.w(Messages.get(this, "ability_no_target"));
            return;
        }

        //find the room first, so a crowded corner costs nothing
        ArrayList<Integer> points = new ArrayList<>();
        for (int i : PathFinder.NEIGHBOURS8) {
            int cell = hero.pos + i;
            if (cell < 0 || cell >= Dungeon.level.length()) continue;
            if (Dungeon.level.distance(hero.pos, cell) != 1) continue;
            if (Actor.findChar(cell) != null || !Dungeon.level.passable[cell]) continue;
            points.add(cell);
        }
        if (points.isEmpty()) {
            GLog.w(Messages.get(this, "ability_no_space"));
            return;
        }

        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, null);

        for (int k = 0; k < ABILITY_SUMMONS && !points.isEmpty(); k++) {
            int index = Random.index(points);

            PinkdogDrone drone = new PinkdogDrone();
            drone.setting(buffedLvl(), null);
            drone.setAutoTarget(true);
            GameScene.add(drone);
            ScrollOfTeleportation.appear(drone, points.get(index));

            points.remove(index);
        }

        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.ZAP);
        Invisibility.dispel();

        afterAbilityUsed(hero);
        hero.spendAndNext(Actor.TICK);
    }

    @Override
    public String abilityInfo() {
        return Messages.get(this, "ability_desc", ABILITY_SUMMONS);
    }

    //any living, visible enemy at all
    private boolean enemyInSight() {
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (mob.isAlive() && mob.alignment == Char.Alignment.ENEMY
                    && mob.invisible <= 0 && Dungeon.level.heroFOV[mob.pos]) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int max(int lvl) {
        return  3*(tier-1) +            	//12 + 3
                lvl*(tier-2);
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        //for Irene the ability takes the place of the summoning button
        if (hero.heroClass != HeroClass.IRENE) {
            actions.add(AC_SUMON);
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_SUMON)) {
            if (!isEquipped(hero)) return;
            if (charge >= 50) GameScene.selectCell(zapper);
        }
    }

    @Override
    public void SPCharge(int value) {
        if (Dungeon.bossLevel()) value = 2;
        super.SPCharge(value);
    }

    protected static CellSelector.Listener zapper = new CellSelector.Listener() {

        @Override
        public void onSelect(Integer target) {

            if (target != null) {

                final Echeveria ss;
                if (curItem instanceof Echeveria) {
                    ss = (Echeveria) Echeveria.curItem;

                    Ballistica shot = new Ballistica(curUser.pos, target, Ballistica.PROJECTILE);
                    int cell = shot.collisionPos;

                    if (target == curUser.pos || cell == curUser.pos) {
                        return;
                    }

                    curUser.sprite.zap(cell);

                    //attempts to target the cell aimed at if something is there, otherwise targets the collision pos.
                    if (Actor.findChar(target) != null)
                        QuickSlotButton.target(Actor.findChar(target));
                    else
                        QuickSlotButton.target(Actor.findChar(cell));

                    if (ss.tryToZap(curUser, target)) {
                        ss.fx(shot, new Callback() {
                            public void call() {
                                ss.onZap(shot);
                            }
                        });
                    }

                }
            }
        }

        @Override
        public String prompt() {
            return Messages.get(MidnightSword.class, "prompt");
        }
    };

    protected void fx( Ballistica bolt, Callback callback ) {
        MagicMissile.boltFromChar( curUser.sprite.parent,
                MagicMissile.FORCE,
                curUser.sprite,
                bolt.collisionPos,
                callback);
        Sample.INSTANCE.play( Assets.Sounds.ZAP );
    }

    public boolean tryToZap(Hero owner, int target) {
        return true;
    }

    protected void onZap( Ballistica bolt ) {
        ArrayList<Integer> respawnPoints = new ArrayList<>();
        Char ch = Actor.findChar( bolt.collisionPos );
        if (ch instanceof Mob) {
            if (ch.alignment == Char.Alignment.ALLY) return;
            for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                int p = ch.pos + PathFinder.NEIGHBOURS8[i];
                if (Actor.findChar(p) == null && Dungeon.level.passable[p]) {
                    respawnPoints.add(p);
                }
            }
            int spawnd = 0;
            while (respawnPoints.size() > 0 && spawnd == 0) {
                int index = Random.index(respawnPoints);

                Echeveria.PinkdogDrone drone = new Echeveria.PinkdogDrone();
                drone.setting(buffedLvl(), (Mob)ch);
                GameScene.add(drone);
                ScrollOfTeleportation.appear(drone, respawnPoints.get(index));

                charge -=50;
                updateQuickslot();

                respawnPoints.remove(index);
                spawnd++;
            }



        } else {
            Dungeon.level.pressCell(bolt.collisionPos);
        }

        Buff buff = Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);
        if (buff != null) buff.detach();
        buff = Dungeon.hero.buff(Swiftthistle.TimeBubble.class);
        if (buff != null) buff.detach();
        //rabbittime在这里不取消的话会怎么样？

        if (Dungeon.hero.buff(Bonk.BonkBuff.class) != null) Buff.detach(Dungeon.hero, Bonk.BonkBuff.class);

        Invisibility.dispel();

        if (setbouns()) curUser.spendAndNext(0.25f);
        else curUser.spendAndNext(1f);
    }

    @Override
    public String status() {
        //Irene runs the ability, so show its charge rather than the old skill's
        if (Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.IRENE) {
            return super.status();
        }
        if (chargeCap == 100)
            return Messages.format("%d%%", charge);
        return null;
    }

    private boolean setbouns() {
        if (Dungeon.hero.belongings.getItem(RingOfWealth.class) != null) {
            if (Dungeon.hero.belongings.getItem(RingOfWealth.class).isEquipped(Dungeon.hero))
                return true;
        }
        return false;
    }

    @Override
    public String desc() {
        String info = Messages.get(this, "desc");
        if (setbouns()) info += "\n\n" + Messages.get( Echeveria.class, "setbouns");

        return info;
    }


    public static class PinkdogDrone extends Mob {
        {
            HT = HP = 20;
            defenseSkill = 5;

            spriteClass = Glow_dronSprite.class;
            baseSpeed = 2f;

            immunities.add(Silence.class);
            alignment = Alignment.ALLY;
            flying = true;

            state = HUNTING;
        }

        Mob LockTarget;
        int targetId = -1;
        int AttackCount = 1;
        //true for drones summoned by the ability: they pick their own prey
        boolean autoTarget = false;

        public void setAutoTarget(boolean value) {
            autoTarget = value;
        }

        @Override
        public int damageRoll() {
            return Random.IntRange(1+(maxLvl/2), 2+maxLvl);
        }

        @Override
        public int attackSkill(Char target) {
            return INFINITE_ACCURACY;
        }

        public void setting(int setlvl, Mob target)
        {
            maxLvl = setlvl;
            LockTarget = target;
        }

        @Override
        protected boolean act() {
            if (AttackCount > 6) { die(this); return true;}

            HP -= 2;
            if (HP < 1) {
                die(this);
                return true;
            }

            if (targetId != -1){
                Actor ch = Actor.findById(targetId);
                targetId = -1;
                if (ch instanceof Mob){
                    LockTarget = (Mob) ch;
                }}

            if (autoTarget) {
                //keep the current prey only while the hero can still see it
                if (LockTarget == null || !LockTarget.isAlive()
                        || LockTarget.alignment != Alignment.ENEMY
                        || LockTarget.invisible > 0
                        || !Dungeon.level.heroFOV[LockTarget.pos]) {
                    LockTarget = seekTarget();
                }
                if (LockTarget == null) {
                    die(this);
                    return true;
                }

                //fly straight to the prey instead of walking, distance is no object
                if (!Dungeon.level.adjacent(pos, LockTarget.pos)) {
                    appearNextTo(LockTarget);
                }

                enemy = LockTarget;
                return super.act();
            }

            if (LockTarget != null && LockTarget.isAlive()) enemy = LockTarget;
            else {
                die(this);
                return true;
            }
            return super.act();
        }

        //nearest enemy the hero can see. distance is ignored on purpose:
        //the drone flies to its prey instead of walking there
        private Mob seekTarget() {
            if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()) {
                fieldOfView = new boolean[Dungeon.level.length()];
            }
            Dungeon.level.updateFieldOfView(this, fieldOfView);

            Mob best = null;
            int bestDist = Integer.MAX_VALUE;

            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                if (mob == this || !mob.isAlive()) continue;
                if (mob.alignment != Alignment.ENEMY) continue;
                if (!Dungeon.hero.fieldOfView[mob.pos]) continue;

                int d = Dungeon.level.distance(pos, mob.pos);
                if (d < bestDist) {
                    bestDist = d;
                    best = mob;
                }
            }

            return best;
        }

        //appear on a free tile right beside the prey, then refresh our own sight
        private boolean appearNextTo(Char target) {
            if (target == null || target.pos < 0) return false;

            ArrayList<Integer> points = new ArrayList<>();
            for (int i : PathFinder.NEIGHBOURS8) {
                int cell = target.pos + i;
                if (cell < 0 || cell >= Dungeon.level.length()) continue;
                if (Dungeon.level.distance(target.pos, cell) != 1) continue;
                if (Actor.findChar(cell) != null || !Dungeon.level.passable[cell]) continue;
                points.add(cell);
            }
            if (points.isEmpty()) return false;

            ScrollOfTeleportation.appear(this, Random.element(points));

            if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()) {
                fieldOfView = new boolean[Dungeon.level.length()];
            }
            Dungeon.level.updateFieldOfView(this, fieldOfView);
            return true;
        }

        @Override
        protected boolean canAttack(Char enemy) {
            return this.fieldOfView[enemy.pos] && Dungeon.level.distance(this.pos, enemy.pos) <= 4;
        }

        @Override
        public boolean attack(Char enemy) {
            if (enemy == null) return false;

            boolean visibleFight = Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[enemy.pos];

            if (enemy.isInvulnerable(getClass())) {

                if (visibleFight) {
                    enemy.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "invulnerable"));

                    Sample.INSTANCE.play(Assets.Sounds.HIT_PARRY, 1f, Random.Float(0.96f, 1.05f));
                }

                return false;
            }
            else if (hit( this, enemy, true )) {

                int dmg = damageRoll() * AttackCount;
                enemy.damage( dmg, this );
                enemy.sprite.flash();

                ArrayList<Integer> respawnPoints = new ArrayList<>();
                for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                    int p = enemy.pos + PathFinder.NEIGHBOURS8[i];
                    if (Actor.findChar(p) == null && Dungeon.level.passable[p]) {
                        respawnPoints.add(p);
                    }
                }
                int spawnd = 0;
                while (respawnPoints.size() > 0 && spawnd == 0) {
                    int index = Random.index(respawnPoints);
                    ScrollOfTeleportation.appear(this, respawnPoints.get(index));
                    respawnPoints.remove(index);
                    spawnd++;
                }

                AttackCount++;

                if (Dungeon.level.heroFOV[pos]) Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC, 1f, Random.Float(0.96f, 1.05f));
            } else {
                enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
            }
            return true;
        }



        public static final String TARGET = "targetId";
        public static final String ATTACK = "AttackCount";
        public static final String AUTO = "autoTarget";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(ATTACK, AttackCount);
            bundle.put(AUTO, autoTarget);
            if (LockTarget != null) bundle.put(TARGET, LockTarget.id());
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            if (bundle.contains( TARGET )){
            targetId = bundle.getInt(TARGET);}
            AttackCount = bundle.getInt(ATTACK);
            autoTarget = bundle.getBoolean(AUTO);
            enemySeen = true;
        }
    }
}
