package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class LoneJourney extends MeleeWeapon {
    public static final String AC_SP = "SP";

    //the lone road lasts this long. the ability has a single charge: once spent it
    //takes ABILITY_RECHARGE turns to come back, then it is usable again
    public static final int ABILITY_DURATION = 40;
    public static final int ABILITY_RECHARGE = 40;
    //attack speed and the magic damage that rides on each hit
    public static final float ABILITY_SPEED_MULT = 3f;

    {
        image = ItemSpriteSheet.LONE;
        hitSound = Assets.Sounds.HIT_WHIP;
        hitSoundPitch = 1f;

        defaultAction = AC_SP;

        tier = 5;
        RCH = 3;
    }

    //Essentially it's a tier 4 weapon, with tier 3 base max damage, and tier 5 scaling.
    //equal to tier 4 in damage at +5

    @Override
    public int max(int lvl) {
        return 4 * (tier) +                    // 20 + 4
                lvl * (tier - 1);
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        if (hero.buff(LongRoadRecharge.class) != null) {
            GLog.w(Messages.get(this, "ability_cooling"));
            return;
        }

        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, null);

        Buff.affect(hero, LongRoad.class, ABILITY_DURATION);
        //spend the single charge, it comes back on its own after ABILITY_RECHARGE turns
        Buff.affect(hero, LongRoadRecharge.class).setDuration(ABILITY_RECHARGE);
        updateQuickslot();

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
        if (owner.buff(LongRoad.class) != null) {
            //three times the attack speed means a third of the delay
            dly /= ABILITY_SPEED_MULT;
        }
        return dly;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (attacker.buff(JourneyBuff_ice.class) != null) {
            Buff.detach(attacker, JourneyBuff_ice.class);
            Buff.affect(defender, Roots.class, 1f);
        }
        else if (attacker.buff(JourneyBuff_fire.class) != null) {
            Buff.detach(attacker, JourneyBuff_fire.class);
            damage *= 1.3f;
            Buff.affect(defender, Burning.class).reignite(defender, 3f);
        }
        else if (attacker.buff(JourneyBuff_heavy.class) != null) {
            Buff.detach(attacker, JourneyBuff_heavy.class);
            Buff.affect(defender, Silence.class, 2f);
        }

        WandOfMagicMissile womm = new WandOfMagicMissile();
        int magic = defender.drRoll();
        //half of the target's guard normally, all of it while walking the lone road
        if (attacker.buff(LongRoad.class) == null) {
            magic /= 2;
        }
        defender.damage(magic, womm);//这样应该能算是魔法伤害吧大概

        SPCharge(20);
        updateQuickslot();
        return super.proc(attacker, defender, damage);
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_SP);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_SP) && this.isEquipped(hero) && charge >= chargeCap) {//change from budding
            GameScene.show(
                    new WndOptions(Messages.get(this, "name"),
                            Messages.get(this, "wnddesc"),
                            Messages.get(this, "special1"),
                            Messages.get(this, "special2"),
                            Messages.get(this, "special3")) {

                        @Override
                        protected void onSelect(int index) {
                            if (index == 0) {
                                Buff.affect(hero, JourneyBuff_ice.class);
                                charge = 0;
                                updateQuickslot();
                            }
                            else if (index == 1) {
                                Buff.affect(hero, JourneyBuff_fire.class);
                                charge = 0;
                                updateQuickslot();
                            }
                            else if (index == 2) {
                                Buff.affect(hero, JourneyBuff_heavy.class);
                                charge = 0;
                                updateQuickslot();
                            }
                        }
                    });
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

    //bottom-left of the slot: whether the single charge is ready, or how long until it is
    @Override
    public String statusBottom() {

        //if the weapon isn't IDed, or is cursed, don't display anything
        if (!isIdentified() || cursed || Dungeon.hero == null) {
            return null;
        }

        LongRoadRecharge recharge = Dungeon.hero.buff(LongRoadRecharge.class);
        if (recharge != null) {
            return Integer.toString(Math.max(1, Math.round(recharge.remaining())));
        }
        return "OK";
    }

    //the lone road: faster strides, and the magic on each hit bites deeper
    public static class LongRoad extends FlavourBuff {

        {
            type = buffType.POSITIVE;
            announced = true;
        }

        @Override
        public int icon() {
            return BuffIndicator.HASTE;
        }

        //the icon dims as the lone road runs out, roughly showing how long is left
        @Override
        public float iconFadePercent() {
            return Math.max(0, (ABILITY_DURATION - visualcooldown()) / (float) ABILITY_DURATION);
        }
    }

    //the ability's single charge, spent on use and refilled over ABILITY_RECHARGE turns.
    //while it is attached the ability cannot be used, otherwise it is ready
    public static class LongRoadRecharge extends Buff {

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

            //nudge the quickslot afterwards, so the slot text shows the new value and
            //clears on the very turn the charge comes back
            Item.updateQuickslot();
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

    public static class JourneyBuff_ice extends Buff{}
    public static class JourneyBuff_fire extends Buff{}
    public static class JourneyBuff_heavy extends Buff{}
}