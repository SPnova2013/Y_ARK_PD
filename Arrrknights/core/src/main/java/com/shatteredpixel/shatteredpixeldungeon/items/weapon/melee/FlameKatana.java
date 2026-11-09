package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BloodOath;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class FlameKatana extends MeleeWeapon {
    {
        image = ItemSpriteSheet.FIRE_KATANA;
        hitSound = Assets.Sounds.HIT_KNIFE;
        hitSoundPitch = 1f;

        tier = 2;
    }

    private int killpoint = 0;

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
        return  4*(tier+1) +    //12 base, down from 15
                lvl*(tier+1);   //scaling unchanged
    }

    public void GetKillPoint() {
        killpoint++;

        if (killpoint == 125) {
            int lvl = this.level();

            BladeDemon n = new BladeDemon();

            n.enchantment = enchantment;
            n.curseInfusionBonus = curseInfusionBonus;
            n.levelKnown = levelKnown;
            n.cursedKnown = cursedKnown;
            n.cursed = cursed;
            n.augment = augment;
            n.chimeras = chimeras;
            n.level(lvl);

            Dungeon.hero.belongings.weapon = n;

            Dungeon.hero.sprite.emitter().burst(Speck.factory(Speck.RED_LIGHT),12);
        }
    }

    @Override
    public String status() {
        if (this.isIdentified()) return "" + killpoint;
        else return null;}


    private static final String KILL = "killpoint";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(KILL, killpoint);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        killpoint = bundle.getInt(KILL);
    }
}
