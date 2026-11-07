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
    public int abilityChargeCap() {
        return ABILITY_RECHARGE;
    }

    @Override
    public boolean abilityEffectActive(Char owner) {
        //the road holds its charge back for as long as it lasts
        return owner.buff(LongRoad.class) != null;
    }

    @Override
    protected int baseChargeUse(Hero hero, Char target) {
        //one whole pool per use, and the pool is the cooldown
        return ABILITY_RECHARGE;
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {

        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, null);

        Buff.affect(hero, LongRoad.class, ABILITY_DURATION);

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

        return MeleeWeapon.abilityChargeStatus();
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

    public static class JourneyBuff_ice extends Buff{}
    public static class JourneyBuff_fire extends Buff{}
    public static class JourneyBuff_heavy extends Buff{}
}