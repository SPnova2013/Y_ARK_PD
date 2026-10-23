package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfFuror;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.Aegis;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Scythe extends MeleeWeapon {
    {
        image = ItemSpriteSheet.SICKEL;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 1.17f;

        tier = 4;
    }

    private int HealCount = 0;

    //the ability lasts ABILITY_DURATION_BASE + weaponLevel turns
    public static final int ABILITY_DURATION_BASE = 5;
    //attack speed is doubled, i.e. the delay is halved
    public static final float ABILITY_SPEED_BONUS = 1f;
    //bonus damage against wounded enemies
    public static final float ABILITY_DAMAGE_BONUS = 0.5f;
    //an enemy counts as wounded below this fraction of its max health
    public static final float ABILITY_WOUNDED_THRESHOLD = 0.5f;

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, null);

        Buff.affect(hero, Harvest.class, ABILITY_DURATION_BASE + buffedLvl());

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

    //attack speed is a delay, so a 100% speed bonus halves it
    @Override
    public float speedFactor(Char owner) {
        float dly = super.speedFactor(owner);
        if (owner.buff(Harvest.class) != null) {
            dly *= 1f / (1f + ABILITY_SPEED_BONUS);
        }
        return dly;
    }

    @Override
    public int max(int lvl) {
        return  4*(tier) - 2 +   //14 + 4
                lvl*(tier); }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        HealCount++;
        int extratarget = 0;
        if (attacker instanceof Hero) {
            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                if (Dungeon.level.adjacent(mob.pos, defender.pos) && mob.alignment != Char.Alignment.ALLY) {
                    int dmg = Dungeon.hero.damageRoll() - Math.max(defender.drRoll(), defender.drRoll());
                    mob.damage(dmg, this);
                    extratarget++;
                    HealCount++;
                }
            }
        }

        float bounsdmg = Math.min(1.5f, 1f+(extratarget*0.1f));

        if (Dungeon.hero.belongings.getItem(RingOfFuror.class) != null) {
            if (Dungeon.hero.belongings.getItem(RingOfFuror.class).isEquipped(Dungeon.hero) && defender.properties().contains(Char.Property.BOSS))
                bounsdmg += 0.35f;
        }

        //harvest cuts deeper into anything already wounded
        if (attacker.buff(Harvest.class) != null && defender.HP < defender.HT * ABILITY_WOUNDED_THRESHOLD) {
            damage *= 1 + ABILITY_DAMAGE_BONUS;
        }

        damage = Math.round(damage * bounsdmg);

        Heal(attacker);

        return super.proc(attacker, defender, damage);
    }
    @Override
    public float wepCorrect(){
        return 0.25f;
    }

    private void Heal(Char attacker) {
        if (HealCount >= 8) {
            int heal = 3 + level();

            if (attacker instanceof Hero) {
            if (Dungeon.hero.lvl > 7 && Dungeon.depth < 5) {
                heal /= 4;
            }
            else if (Dungeon.hero.lvl > 12 && Dungeon.depth < 10) {
                heal /= 4;
            }
            else if (Dungeon.hero.lvl > 18 && Dungeon.depth < 15) {
                heal /= 4;
            }
            else if (Dungeon.hero.lvl > 23 && Dungeon.depth < 20) {
                heal /= 4;
            }}

            attacker.HP = Math.min(attacker.HP + heal, attacker.HT);
            if (attacker instanceof Hero && Dungeon.hero.buff( Aegis.AegisBuff.class) != null){
                Aegis.addShield(heal);
            }
            attacker.sprite.emitter().burst(Speck.factory(Speck.HEALING), 2);
            attacker.sprite.showStatus(CharSprite.POSITIVE, "+%dHP", heal);
            HealCount = 0;
        }
    }

    @Override
    public String status() {
        if (Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.IRENE) {
            return super.status();
        }
        if (this.isIdentified()) return HealCount + "/" + 8;
    else return null;}

    @Override
    public String desc() {
        String info = Messages.get(this, "desc");
        if (Dungeon.hero.belongings.getItem(RingOfFuror.class) != null) {
            if (Dungeon.hero.belongings.getItem(RingOfFuror.class).isEquipped(Dungeon.hero))
                info += "\n\n" + Messages.get( Scythe.class, "setbouns");}

        return info;
    }


    //whet the blade: faster swings, and extra damage to wounded enemies
    public static class Harvest extends FlavourBuff {

        {
            type = buffType.POSITIVE;
            announced = true;
        }

        @Override
        public int icon() {
            return BuffIndicator.HASTE;
        }
    }

    private static final String HEALPOINT = "HealCount";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(HEALPOINT, HealCount);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        HealCount = bundle.getInt(HEALPOINT);
    }
}
