package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArtifactRecharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK1.LiveStart;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ChaliceOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CustomeSet;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.UnstableSpellbook;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMistress;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfTenacity;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.Mon3terSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.Rock_CrabSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndCatGunCommands;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class CatGun extends MeleeWeapon {
    public static final String AC_ZAP = "ZAP";
    {
        image = ItemSpriteSheet.CATGUN;
        hitSound = Assets.Sounds.ATK_SPIRITBOW;
        hitSoundPitch = 1f;
        defaultAction = AC_ZAP;

        tier = 5;
        RCH = 2;
    }

    @Override
    public int max(int lvl) {
        return  3*(tier+1) +   //18 + 4
                lvl*(tier-1); }

    //the three commands, all of them free actions
    public static final int ARMOR_DURATION = 10;
    public static final float BLOCK_CHANCE = 0.4f;
    public static final int TACTIC_DURATION = 5;
    public static final int MELTDOWN_DURATION = 5;

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        //opening the list costs nothing, a command only counts once it is given
        GameScene.show(new WndCatGunCommands(this, hero, new WndCatGunCommands.Listener() {
            @Override
            public void onSelect(int index) {
                useCommand(hero, index);
            }
        }));
    }
    @Override
    public String statusBottom() {
        if (!isIdentified() || cursed) return null;
        if (isEquipped(Dungeon.hero)
                && Dungeon.hero.buff(Charger.class) != null) {
            Charger buff = Dungeon.hero.buff(Charger.class);
            return String.valueOf(buff.charges);
        } else {
            return super.status();
        }
    }
    @Override
    public String abilityInfo() {
        return Messages.get(this, "ability_desc");
    }

    //the nearest living Mon3tr on this floor, or null
    public static Mon3tr findMon3tr() {
        if (Dungeon.level == null) {
            return null;
        }
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (mob instanceof Mon3tr && mob.isAlive()) {
                return (Mon3tr) mob;
            }
        }
        return null;
    }

    public void useCommand(Hero hero, int index) {
        Mon3tr mon = findMon3tr();

        if (index > 0 && mon == null) {
            GLog.w(Messages.get(this, "need_mon3tr"));
            return;
        }

        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, null);

        switch (index) {
            case 0:
                Buff.prolong(hero, StructuralIntegrity.class, ARMOR_DURATION);
                break;
            case 1:
                Buff.prolong(mon, TacticalCoordination.class, TACTIC_DURATION);
                break;
            default:
                Buff.affect(mon, Meltdown.class).set(MELTDOWN_DURATION);
                break;
        }

        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.SKILL_BASIC);
        Invisibility.dispel();
        updateQuickslot();

        afterAbilityUsed(hero);
        //no spendAndNext here on purpose, a command costs no turn
    }

    //指令：结构加固
    public static class StructuralIntegrity extends FlavourBuff {

        {
            type = buffType.POSITIVE;
            announced = true;
        }

        @Override
        public int icon() {
            return BuffIndicator.ARMOR;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (ARMOR_DURATION - visualcooldown()) / (float) ARMOR_DURATION);
        }
    }

    //指令：战术协同
    public static class TacticalCoordination extends FlavourBuff {

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
            return Math.max(0, (TACTIC_DURATION - visualcooldown()) / (float) TACTIC_DURATION);
        }
    }

    //指令：熔毁
    public static class Meltdown extends Buff {

        private int left = MELTDOWN_DURATION;
        private int turn = 0;
        //kills Mon3tr already had when the meltdown started
        private int startTally = 0;

        {
            type = buffType.POSITIVE;
            announced = true;
        }

        public void set(int duration) {
            left = duration;
            startTally = tallyOfTarget();
        }

        private int tallyOfTarget() {
            return target instanceof Mon3tr ? ((Mon3tr) target).killTally() : 0;
        }

        //+200% on the first turn, 40% less of that bonus each turn after
        public float attackMultiplier() {
            return 1f + 2f * (float) Math.pow(0.6f, turn);
        }

        @Override
        public int icon() {
            return BuffIndicator.BERSERK;
        }

        @Override
        public boolean act() {
            turn++;
            left--;

            if (left <= 0) {
                detach();
                return true;
            }

            spend(TICK);
            return true;
        }

        @Override
        public void detach() {
            //a meltdown that killed nothing burns the wielder instead
            boolean killed = tallyOfTarget() > startTally;

            if (!killed && target != null && target.isAlive()) {
                int loss = Math.max(1, target.HT / 2);
                target.HP -= loss;
                if (target.sprite != null) {
                    target.sprite.showStatus(CharSprite.NEGATIVE, "-%d", loss);
                }
                if (target.HP <= 0) {
                    target.die(this);
                }
            }
            super.detach();
        }

        private static final String LEFT = "left";
        private static final String TURN = "turn";
        private static final String START_TALLY = "startTally";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(LEFT, left);
            bundle.put(TURN, turn);
            bundle.put(START_TALLY, startTally);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            left = bundle.getInt(LEFT);
            turn = bundle.getInt(TURN);
            startTally = bundle.getInt(START_TALLY);
        }
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        SPCharge_cat(Random.IntRange(2,3));
        return super.proc(attacker, defender, damage);
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_ZAP);
        return actions;
    }

    public void SPCharge_cat(int value) {
        charge = Math.min(charge+value, chargeCap);
        updateQuickslot();
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_ZAP)) {
            if (charge >= chargeCap) {
                ArrayList<Integer> respawnPoints = new ArrayList<>();

                for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                    int p = hero.pos + PathFinder.NEIGHBOURS8[i];
                    if (Actor.findChar(p) == null && Dungeon.level.passable[p]) {
                        respawnPoints.add(p);
                    }
                }
                int spawnd = 0;
                while (respawnPoints.size() > 0 && spawnd == 0) {
                    int index = Random.index(respawnPoints);

                    CatGun.Mon3tr tr = new CatGun.Mon3tr();
                    tr.setting(buffedLvl());
                    GameScene.add(tr);
                    ScrollOfTeleportation.appear(tr, respawnPoints.get(index));

                    respawnPoints.remove(index);
                    Sample.INSTANCE.play( Assets.Sounds.SKILL_MON1 );
                    spawnd++;
                }
                charge = 0;
            }
            else if (charge < chargeCap && catsetbouns()) {
                for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                    if (mob instanceof Mon3tr) {
                        int gaincharge;
                        if (mob.HT / 2 > mob.HP) gaincharge = 25;
                        else if (mob.HT / 4 > mob.HP) gaincharge = 15;
                        else gaincharge = 40;

                        mob.die(this);
                        charge = Math.min(charge + gaincharge, chargeCap);
                        updateQuickslot();
                    }
                }
            }
        }
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
    public String desc() {
        String info;
        if (catsetbouns()) {
                info = Messages.get(this, "desc_sp");
                info += "\n\n" + Messages.get(CatGun.class, "setbouns");
                return info;
            }
        info = Messages.get(this, "desc");
        return info;
    }

    public static boolean catsetbouns() {
        if (!(Dungeon.hero.belongings.weapon instanceof CatGun)) return false;

        if (Dungeon.hero.belongings.getItem(RingOfMistress.class) != null && Dungeon.hero.belongings.getItem(ChaliceOfBlood.class) != null
                && Dungeon.hero.belongings.getItem(UnstableSpellbook.class) != null) {
            if (Dungeon.hero.belongings.getItem(RingOfMistress.class).isEquipped(Dungeon.hero) && Dungeon.hero.belongings.getItem(ChaliceOfBlood.class).isEquipped(Dungeon.hero)
                    && Dungeon.hero.belongings.getItem(UnstableSpellbook.class).isEquipped(Dungeon.hero))
                return true;
        }
        return false;
    }


    public static class Mon3tr extends Mob {
        {
            spriteClass = Mon3terSprite.class;
            baseSpeed = 3f;

            state = HUNTING;
            immunities.add(Silence.class);
            alignment = Alignment.ALLY;

            WANDERING = new Wandering();
        }

        private int blinkCooldown = 0;

        @Override
        protected boolean getCloser( int target ) {
            if (fieldOfView[target] && Dungeon.level.distance( pos, target ) > 2 && blinkCooldown <= 0 && target != Dungeon.hero.pos) {

                blink( target );
                spend( -1 / speed() );
                return true;

            } else {

                blinkCooldown--;
                return super.getCloser( target );

            }
        }

        private class Wandering extends Mob.Wandering {

            @Override
            public boolean act( boolean enemyInFOV, boolean justAlerted ) {
                if ( enemyInFOV ) {

                    enemySeen = true;

                    notice();
                    alerted = true;
                    state = HUNTING;
                    target = enemy.pos;

                } else {

                    enemySeen = false;

                    int oldPos = pos;
                    target = Dungeon.hero.pos;
                    //always move towards the hero when wandering
                    if (getCloser( target )) {
                        //moves 2 tiles at a time when returning to the hero
                        if (!Dungeon.level.adjacent(target, pos)){
                            getCloser( target );
                        }
                        spend( 1 / speed() );
                        return moveSprite( oldPos, pos );
                    } else {
                        spend( TICK );
                    }

                }
                return true;
            }

        }

        private void blink( int target ) {

            Ballistica route = new Ballistica( pos, target, Ballistica.PROJECTILE);
            int cell = route.collisionPos;

            //can't occupy the same cell as another char, so move back one.
            if (Actor.findChar( cell ) != null && cell != this.pos)
                cell = route.path.get(route.dist-1);

            if (Dungeon.level.avoid[ cell ]){
                ArrayList<Integer> candidates = new ArrayList<>();
                for (int n : PathFinder.NEIGHBOURS8) {
                    cell = route.collisionPos + n;
                    if (Dungeon.level.passable[cell] && Actor.findChar( cell ) == null) {
                        candidates.add( cell );
                    }
                }
                if (candidates.size() > 0)
                    cell = Random.element(candidates);
                else {
                    blinkCooldown = 1;
                    return;
                }
            }

            ScrollOfTeleportation.appear( this, cell );
            Sample.INSTANCE.play( Assets.Sounds.SKILL_MON2 );

            blinkCooldown = 1;
        }

        @Override
        protected boolean act() {
            flushKills();

            if (this.buff(StoneOfAggression.Aggression.class) == null) {
                Buff.prolong(this, StoneOfAggression.Aggression.class, StoneOfAggression.Aggression.DURATION);}

            CustomeSet.CustomSetBuff setBuff = Dungeon.hero.buff( CustomeSet.CustomSetBuff.class);
            if (isAlive() || HP <= 1) {

                if (setBuff != null) {
                    int adddamage = HT / (50 + setBuff.itemLevel() * 3);
                    if (adddamage < 1) adddamage = 1;
                    if (state == WANDERING && adddamage > 1) HP -= adddamage / 2;
                    else HP -= adddamage;
                } else {
                    int adddamage = HT /50;
                    if (adddamage < 1) adddamage = 1;
                    if (state == WANDERING && adddamage > 1) HP -= adddamage / 2;
                    else  HP -= adddamage;
                }
            }

            if (HP < 1) {
                this.die(this);
                return true;
            }
            return super.act();
        }

        //Mon3tr strikes without a sprite callback, so a death only becomes visible
        //after the fact: remember the last target and count it once it is dead
        private Char lastTarget;
        private int killTally = 0;

        public void flushKills() {
            if (lastTarget != null && !lastTarget.isAlive()) {
                killTally++;
                lastTarget = null;
            }
        }

        public int killTally() {
            flushKills();
            return killTally;
        }

        @Override
        public int attackProc(Char enemy, int damage) {
            flushKills();
            lastTarget = enemy;

            //指令：战术协同 sweeps whatever else stands next to it
            if (buff(TacticalCoordination.class) != null) {
                for (int i : PathFinder.NEIGHBOURS8) {
                    int cell = pos + i;
                    if (cell < 0 || cell >= Dungeon.level.length()) continue;
                    if (Dungeon.level.distance(pos, cell) != 1) continue;

                    Char ch = Actor.findChar(cell);
                    if (ch == null || ch == this || ch == enemy || !ch.isAlive()) continue;
                    if (ch.alignment == Char.Alignment.ALLY) continue;

                    ch.damage(damage, this);
                }
            }

            CustomeSet.CustomSetBuff setBuff = Dungeon.hero.buff( CustomeSet.CustomSetBuff.class);
            if (setBuff != null) {
                int n = 20 + setBuff.itemLevel() * 3;
                HP -= HT/n;
            }
            else HP -= HT/20;

            if (catsetbouns()) {
                damage *= 1.3f;
                Buff.affect(Dungeon.hero, ArtifactRecharge.class).prolong(2).ignoreHornOfPlenty=false;
                Buff.affect(Dungeon.hero, Bless.class, 2f);
            }

            return super.attackProc(enemy, damage);
        }

        @Override
        public void die(Object cause) {
            if (catsetbouns()) {
                for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                    if (Dungeon.level.adjacent(mob.pos, this.pos) && mob.alignment != Char.Alignment.ALLY) {
                        int dmg = this.damageRoll();
                        CellEmitter.get( mob.pos ).burst(BlastParticle.FACTORY, 6 );
                        mob.damage(dmg, this);
                    }
                }
            }
            super.die(cause);
        }

        @Override
        public int damageRoll() {
            int dmg = Random.NormalIntRange( 18 + maxLvl * 4, 24 + maxLvl * 6 );

            if (buff(TacticalCoordination.class) != null) {
                dmg = Math.round(dmg * 1.5f);
            }
            Meltdown melt = buff(Meltdown.class);
            if (melt != null) {
                dmg = Math.round(dmg * melt.attackMultiplier());
            }
            return dmg;
        }

        @Override
        public int attackSkill( Char target ) {
            return 25 + maxLvl * 3;
        }

        @Override
        public int drRoll() {
            int dr = Random.NormalIntRange(maxLvl / 2, 2 + maxLvl);

            if (Dungeon.hero.buff(StructuralIntegrity.class) != null) {
                dr = Math.round(dr * 2f);
            }
            if (buff(Meltdown.class) != null) {
                dr = Math.round(dr * 2.5f);
            }
            return dr;
        }

        //指令：熔毁 turns every blow into true damage
        @Override
        public boolean dealsTrueDamageNow(Char enemy) {
            return buff(Meltdown.class) != null || super.dealsTrueDamageNow(enemy);
        }

        public void setting(int setlvl)
        {
            CustomeSet.CustomSetBuff setBuff = Dungeon.hero.buff( CustomeSet.CustomSetBuff.class);
            int itembuff = 0;
            if (setBuff != null) itembuff = setBuff.itemLevel();
            HP=HT=120 + setlvl * 20;
            defenseSkill = 10 + setlvl * 2;
            maxLvl = setlvl + itembuff / 2;
        }

        private static final String BLINK = "blinkcooldown";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(BLINK, blinkCooldown);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            blinkCooldown = bundle.getInt(BLINK);
            enemySeen = true;
        }
    }

}
