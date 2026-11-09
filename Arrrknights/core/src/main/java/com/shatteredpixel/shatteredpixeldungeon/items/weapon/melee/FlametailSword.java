package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChenCombo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Combo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.audio.Sample;

public class FlametailSword extends MeleeWeapon {

    //焰心: how long the flame burns, and how much it grants
    public static final int ABILITY_DURATION = 5;
    public static final float ABILITY_SPEED_BONUS = 1f;
    public static final float ABILITY_EVASION_BONUS = 5f;

    {
        image = ItemSpriteSheet.FLAMETAIL;
        hitSound = Assets.Sounds.HIT_SPEAR;
        hitSoundPitch = 0.9f;

        tier = 4;
    }

    //a free action: the heart simply catches fire
    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, null);

        Buff.prolong(hero, FlameHeart.class, ABILITY_DURATION);

        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.BURNING);
        Invisibility.dispel();

        afterAbilityUsed(hero);
        //no spendAndNext here on purpose, lighting the heart costs no turn
    }

    @Override
    public String abilityInfo() {
        return Messages.get(this, "ability_desc", ABILITY_DURATION,
                (int) (ABILITY_SPEED_BONUS * 100), (int) (ABILITY_EVASION_BONUS * 100));
    }

    //the flame doubles the swing speed, so the delay is halved
    @Override
    public float speedFactor(Char owner) {
        float dly = super.speedFactor(owner);
        if (owner.buff(FlameHeart.class) != null) {
            dly *= 1f / (1f + ABILITY_SPEED_BONUS);
        }
        return dly;
    }

    //焰心: the flame wraps the body, so evasion is raised while it burns
    public static class FlameHeart extends FlavourBuff {

        {
            type = buffType.POSITIVE;
            announced = true;
        }

        @Override
        public int icon() {
            return BuffIndicator.HASTE;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (ABILITY_DURATION - visualcooldown()) / (float) ABILITY_DURATION);
        }
    }

    @Override
    public int max(int lvl) {
        return  4*(tier) +   //16 + 4, 조건부 2타
                lvl*(tier);   //scaling unchanged
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        boolean da = false;
        if (attacker.buff(FlametaillBuff.class) != null) {
            Buff.detach(attacker, FlametaillBuff.class);
            da = true;
        }
        if (da) {
                attacker.attack(defender);
                defender.sprite.bloodBurstA( defender.sprite.center(), 4 );
                defender.sprite.flash();
                if (attacker instanceof Hero && Dungeon.hero.subClassSet.contains(HeroSubClass.GLADIATOR)) {
                    Buff.affect(attacker, Combo.class).bounshit(defender);
                }
                else if (attacker instanceof Hero && Dungeon.hero.heroClass == HeroClass.CHEN) {
                    Buff.affect(attacker, ChenCombo.class).bounshit(defender);
                }
                damage *= 1.5f;
        }
        return super.proc(attacker, defender, damage);
    }

    public static class FlametaillBuff extends Buff {
    }
}
