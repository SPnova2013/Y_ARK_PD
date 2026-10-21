package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class SHISHIOH extends MeleeWeapon {

    //fraction of the hero's current health paid to use the ability
    public static final float ABILITY_HP_COST = 0.5f;
    //ability duration is ABILITY_DURATION_BASE + weaponLevel/2 turns
    public static final int ABILITY_DURATION_BASE = 5;
    //damage multiplier while the ability is active
    public static final float ABILITY_DAMAGE_BONUS = 1f;

    {
        image = ItemSpriteSheet.SHISHIOH;
        hitSound = Assets.Sounds.HIT_KNIFE;
        hitSoundPitch = 1.22f;

        tier = 3;
        ACC = 1.2f;

        bones = false;
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        hero.belongings.abilityWeapon = this;

        //pay with half of the hero's current health, this cost can never be lethal
        int cost = Math.round(hero.HP * ABILITY_HP_COST);
        cost = Math.min(cost, hero.HP - 1);
        if (cost > 0) {
            hero.HP -= cost;
            hero.sprite.showStatus(CharSprite.NEGATIVE, Integer.toString(cost));
        }

        beforeAbilityUsed(hero, null);

        Buff.affect(hero, LandingSlash.class, ABILITY_DURATION_BASE + buffedLvl()/2);

        CellEmitter.get(hero.pos).burst(Speck.factory(Speck.LIGHT), 8);
        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
        Invisibility.dispel();

        afterAbilityUsed(hero);
        hero.spendAndNext(Actor.TICK);
    }

    @Override
    public String abilityInfo() {
        return Messages.get(this, "ability_desc", ABILITY_DURATION_BASE + buffedLvl()/2);
    }

    @Override
    public int damageRoll(Char owner) {
        int dmg = super.damageRoll(owner);
        if (owner.buff(LandingSlash.class) != null) {
            dmg *= 1 + ABILITY_DAMAGE_BONUS;
        }
        return dmg;
    }

    //while LandingSlash is up the weapon's damage is treated as spell damage, see Char#damageTypeOf
    @Override
    public boolean dealsSpellDamage(Char owner) {
        return owner.buff(LandingSlash.class) != null;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        int thisHP = attacker.HP;
        int thisHT = attacker.HT;
        if (thisHP <= thisHT/4) {
            damage *= 1.5f;

            if (Random.Int(2) < 1) {
                int healAmt = Math.round(damage * 0.1f);
                healAmt = Math.min(healAmt, attacker.HT - attacker.HP);

                if (healAmt > 0 && attacker.isAlive()) {

                    attacker.HP += healAmt;
                    attacker.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 1);
                    attacker.sprite.showStatus(CharSprite.POSITIVE, Integer.toString(healAmt));

                }
            }
        }
        else if (thisHP <= thisHT / 2){
            damage *= 1.3f;
            if (Random.Int(4) < 1) {
                int healAmt = Math.round(damage * 0.1f);
                healAmt = Math.min(healAmt, attacker.HT - attacker.HP);

                if (healAmt > 0 && attacker.isAlive()) {

                    attacker.HP += healAmt;
                    attacker.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 1);
                    attacker.sprite.showStatus(CharSprite.POSITIVE, Integer.toString(healAmt));

                }
            }
        }
        else if (thisHP <= thisHT - thisHT/4) {
            damage *= 1.1f;
        }
        return super.proc(attacker, defender, damage);
    }

    //lifted stance after a landing slash: damage is doubled and counts as spell damage
    public static class LandingSlash extends FlavourBuff {

        {
            type = buffType.POSITIVE;
            announced = true;
        }

        @Override
        public int icon() {
            return BuffIndicator.FURY;
        }
    }
}
