package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.audio.Sample;

public class Niansword extends MeleeWeapon implements MeleeWeapon.BlockingWeapon {

    //the seal lasts ABILITY_DURATION_BASE + weaponLevel turns
    public static final int ABILITY_DURATION_BASE = 5;
    //defense is raised by this fraction, so the block becomes 230% of its normal value
    public static final float ABILITY_DEFENSE_BONUS = 1.3f;

    {
        image = ItemSpriteSheet.NIANSWORD;
        hitSound = Assets.Sounds.HIT_RINGOUT;
        hitSoundPitch = 1f;

        tier = 5;
    }


    @Override
    public int max(int lvl) {
        return  Math.round(2.5f*(tier+1)) +
                lvl*(tier-1);
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        return super.proc(attacker, defender, damage);
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, null);

        Buff.affect(hero, BronzeSeal.class, ABILITY_DURATION_BASE + buffedLvl());

        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.SKILL_BASIC);
        Invisibility.dispel();

        afterAbilityUsed(hero);
        hero.spendAndNext(Actor.TICK);
    }

    @Override
    public String abilityInfo() {
        return Messages.get(this, "ability_desc", ABILITY_DURATION_BASE + buffedLvl());
    }

    @Override
    public int defenseFactor( Char owner ) {
        //the seal multiplies the hero's whole guard instead of this slot, see Hero#drRoll
        return DRMax();
    }

    public int DRMax(){
        return DRMax(buffedLvl());
    }

    public int DRMax(int lvl){
        return 2 + lvl;
    }

    public String statsInfo(){
        if (isIdentified()){
            return Messages.get(this, "stats_desc", 2+buffedLvl(), 3+3*buffedLvl());
        } else {
            return Messages.get(this, "typical_stats_desc", 2,3);
        }

    }

    //a bronze seal pressed onto the blade: heavier guard, and the bite comes back as spell damage
    public static class BronzeSeal extends FlavourBuff {

        {
            type = buffType.POSITIVE;
            announced = true;
        }

        @Override
        public int icon() {
            return BuffIndicator.ARMOR;
        }
    }
}
