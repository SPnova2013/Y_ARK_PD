package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BloodOath;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class BladeDemon extends MeleeWeapon {
    public static final String AC_ZAP = "ZAP";
    {
        image = ItemSpriteSheet.BLADE_DEMON;
        hitSound = Assets.Sounds.HIT_STAB;
        hitSoundPitch = 1.16f;

        tier = 4;
        DLY = 0.8f; // 125%
    }

    private boolean swiching = false;

    //歃血: the next landed hit is doubled, and a little blood comes back right away
    public static final float ABILITY_DAMAGE_BONUS = 1f;
    public static final float ABILITY_HEAL_FRACTION = 0.05f;

    //a free action: the oath is sworn, not swung
    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, null);

        Buff.affect(hero, BloodOath.class);

        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.DRINK);
        Invisibility.dispel();

        afterAbilityUsed(hero);
        //no spendAndNext here on purpose, swearing the oath costs no turn
    }

    @Override
    public String abilityInfo() {
        return Messages.get(this, "ability_desc",
                (int) (ABILITY_DAMAGE_BONUS * 100), (int) (ABILITY_HEAL_FRACTION * 100));
    }

    //the oath doubles the next hit
    @Override
    public int damageRoll(Char owner) {
        int dmg = super.damageRoll(owner);
        if (owner.buff(BloodOath.class) != null) {
            dmg = Math.round(dmg * (1f + ABILITY_DAMAGE_BONUS));
        }
        return dmg;
    }

    //and it is spent once that hit lands
    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (attacker.buff(BloodOath.class) != null) {
            Buff.detach(attacker, BloodOath.class);
            int heal = Math.max(1, Math.round(attacker.HT * ABILITY_HEAL_FRACTION));
            heal = Math.min(heal, attacker.HT - attacker.HP);
            if (heal > 0) {
                attacker.HP += heal;
                attacker.sprite.showStatus(CharSprite.POSITIVE, "+%dHP", heal);
            }
        }
        return super.proc(attacker, defender, damage);
    }

    @Override
    public int max(int lvl) {
        return  4*(tier+1) + 3 +    //23 + 5
                lvl*(tier+1);   //scaling unchanged
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
                swiching = false;DLY = 0.8f;
            } else {
                swiching = true;DLY = 1f;
            }

            updateQuickslot();
            curUser.spendAndNext(0.5f);
        }
    }

    public boolean isSwiching() {
        return swiching;
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
    private static final String DLYSAVE = "DLY";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SWICH, swiching);
        bundle.put(DLYSAVE, DLY);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        swiching = bundle.getBoolean(SWICH);
        DLY = bundle.getFloat(DLYSAVE);
    }
}
