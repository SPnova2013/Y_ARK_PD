package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.audio.Sample;

public class M1887 extends MeleeWeapon {

    //the journey lasts ABILITY_DURATION_BASE + weaponLevel/2 turns
    public static final int ABILITY_DURATION_BASE = 3;
    //attack speed bonus while it lasts, so the delay is halved at 1f
    public static final float ABILITY_SPEED_BONUS = 1f;

    {
        image = ItemSpriteSheet.M1887;
        hitSound = Assets.Sounds.HIT_SHOTGUN;
        hitSoundPitch = 1f;

        tier = 4;
    }

    @Override
    public int max(int lvl) {
        return  4*(tier-1) - 2 +    //10 + 3 (one target = x2)
                lvl*(tier-1);
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, null);

        Buff.affect(hero, FinalJourney.class, ABILITY_DURATION_BASE + buffedLvl()/2);

        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.SKILL_BASIC);
        Invisibility.dispel();

        afterAbilityUsed(hero);
        hero.spendAndNext(Actor.TICK);
    }

    @Override
    public String abilityInfo() {
        return Messages.get(this, "ability_desc", ABILITY_DURATION_BASE + buffedLvl()/2);
    }

    //attack speed is a delay, so a 100% speed bonus halves it
    @Override
    public float speedFactor(Char owner) {
        float dly = super.speedFactor(owner);
        if (owner.buff(FinalJourney.class) != null) {
            dly *= 1f / (1f + ABILITY_SPEED_BONUS);
        }
        return dly;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        int target = 1;
        if (attacker instanceof Hero) {
            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                if (Dungeon.level.adjacent(mob.pos, defender.pos) && mob.alignment != Char.Alignment.ALLY) {
                    mob.damage(Dungeon.hero.damageRoll() - Math.max(defender.drRoll(), defender.drRoll()), this);
                    target++;
                }
            }
        }

        if (target == 1) damage *= 2;

        //final journey: every shot lands a second time
        if (attacker.buff(FinalJourney.class) != null && defender.isAlive()) {
            defender.damage(attacker.damageRoll() - defender.drRoll(), attacker);
        }

        return super.proc(attacker, defender, damage);
    }

    //the last ride: faster trigger, and every shot hits twice
    public static class FinalJourney extends FlavourBuff {

        {
            type = buffType.POSITIVE;
            announced = true;
        }

        @Override
        public int icon() {
            return BuffIndicator.HASTE;
        }
    }
}
