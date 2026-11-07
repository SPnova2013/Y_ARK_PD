package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith_donut;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CustomeSet;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.Rock_CrabSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ZuzaiSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ImageoverForm extends MeleeWeapon {
    {
        image = ItemSpriteSheet.DUSK;
        hitSound = Assets.Sounds.HIT_DUSK;
        hitSoundPitch = 1f;

        tier = 5;
    }

    @Override
    public int min(int lvl) {
        if (Dungeon.hero.lvl >= 17) {
            return tier + lvl + damagebouns();
        }
        return tier + lvl;
    }

    @Override
    public int max(int lvl) {
        if (Dungeon.hero.lvl >= 17)
            return  5*(tier-1) + damagebouns() +    //20 + bouns + 5
                    lvl*(tier);   //scaling unchanged
        else return  4*(tier-1) +    //16 + 4
                    lvl*(tier-1);   //scaling unchanged
    }

    @Override
    public String desc() {
        if (Dungeon.hero.lvl >= 25) return Messages.get(this, "desc_up2");
        else if (Dungeon.hero.lvl >= 17) return Messages.get(this, "desc_up1");
        else return Messages.get(this, "desc");
    }

    public int damagebouns() {
        int bouns = Statistics.enemiesSlain / 50;
        return Math.min(bouns, 15);
    }

    //写意胜形: the stance lasts this long, and the blade needs a long time to gather it again
    public static final int ABILITY_DURATION = 60;
    public static final int ABILITY_RECHARGE = 100;
    //the stance halves the attack speed, which means twice the delay
    public static final float ABILITY_SPEED_MULT = 2f;
    //and reaches one tile further
    public static final int ABILITY_REACH_BONUS = 1;

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        if (hero.buff(ImagoRecharge.class) != null) {
            GLog.w(Messages.get(this, "ability_cooling"));
            return;
        }

        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, null);

        Buff.affect(hero, WriteForm.class, ABILITY_DURATION);
        //spend the single charge, it comes back on its own after ABILITY_RECHARGE turns
        Buff.affect(hero, ImagoRecharge.class).setDuration(ABILITY_RECHARGE);

        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.SKILL_BASIC);
        Invisibility.dispel();

        afterAbilityUsed(hero);
        hero.spendAndNext(Actor.TICK);
    }

    @Override
    public String abilityInfo() {
        return Messages.get(this, "ability_desc", ABILITY_DURATION, ABILITY_RECHARGE);
    }

    @Override
    public float speedFactor(Char owner) {
        float dly = super.speedFactor(owner);
        if (owner.buff(WriteForm.class) != null) {
            dly *= ABILITY_SPEED_MULT;
        }
        return dly;
    }

    @Override
    public int reachFactor(Char owner) {
        int reach = super.reachFactor(owner);
        if (owner.buff(WriteForm.class) != null) {
            reach += ABILITY_REACH_BONUS;
        }
        return reach;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (attacker.buff(WriteForm.class) != null) {
            summonInstinct(defender);
        }
        return super.proc(attacker, defender, damage);
    }

    //every landed blow brushes a little instinct into being beside its target
    private void summonInstinct(Char target) {
        ArrayList<Integer> spots = new ArrayList<>();
        for (int i : PathFinder.NEIGHBOURS8) {
            int p = target.pos + i;
            if (p < 0 || p >= Dungeon.level.length()) continue;
            //guard against stepping around the edge of a row
            if (Dungeon.level.distance(target.pos, p) != 1) continue;
            if (Actor.findChar(p) != null || !Dungeon.level.passable[p]) continue;
            spots.add(p);
        }
        if (spots.isEmpty()) {
            return;
        }

        LittleInstinct instinct = new LittleInstinct();
        instinct.setState(buffedLvl());
        GameScene.add(instinct);
        ScrollOfTeleportation.appear(instinct, spots.get(Random.Int(spots.size())));
    }

    //the stance itself: slower, longer, and brushing out little instincts
    public static class WriteForm extends FlavourBuff {

        {
            type = buffType.POSITIVE;
            announced = true;
        }

        @Override
        public int icon() {
            return BuffIndicator.WEAPON;
        }

        //the icon dims as the stance runs out
        @Override
        public float iconFadePercent() {
            return Math.max(0, (ABILITY_DURATION - visualcooldown()) / (float) ABILITY_DURATION);
        }
    }

    //the single charge of the stance, refilled over ABILITY_RECHARGE turns
    public static class ImagoRecharge extends Buff {

        {
            type = buffType.NEGATIVE;
            announced = false;
        }

        private float left = 0f;

        public void setDuration(float duration) {
            left = duration;
        }

        public float remaining() {
            return left;
        }

        @Override
        public boolean act() {
            left -= TICK;

            if (left <= 0f) {
                detach();
            } else {
                spend(TICK);
            }
            return true;
        }

        @Override
        public int icon() {
            return BuffIndicator.COOL_TIME;
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", dispTurns(left));
        }

        private static final String LEFT = "left";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(LEFT, left);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            left = bundle.getFloat(LEFT);
        }
    }

    public static class LittleInstinct extends Mob {
        {
            spriteClass = ZuzaiSprite.class;

            defenseSkill = 0;

            immunities.add(Silence.class);
            alignment = Alignment.ALLY;
            WANDERING = new Wandering();
        }

        int level = 0;

        public void setState(int setlvl) {
            HP=HT=50 + setlvl * 10;
            level = setlvl;
        }

        @Override
        protected boolean act() {
            if (this.buff(StoneOfAggression.Aggression.class) == null) {
                Buff.prolong(this, StoneOfAggression.Aggression.class, StoneOfAggression.Aggression.DURATION);}

                HP -= 1;
                if (HP < 1) {
                    this.die(this);
                    return true;
                }
            return super.act();
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

        @Override
        public int damageRoll() {
            return Random.NormalIntRange( 5, 6 + level );
        }

        @Override
        public int attackSkill( Char target ) {
            return 12 + level;
        }

        @Override
        public int drRoll() {
            return Random.NormalIntRange(0, 8 + level);
        }

        private static final String LVL = "level";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(LVL, level);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            level = bundle.getInt(LVL);
            setState(level);
            enemySeen = true;
        }

    }
}
