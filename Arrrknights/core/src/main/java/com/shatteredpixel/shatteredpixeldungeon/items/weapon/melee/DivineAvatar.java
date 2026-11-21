package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SkillBook;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSunLight;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.Aegis;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class DivineAvatar extends MeleeWeapon {
    public static final String AC_ZAP = "ZAP";
    {
        image = ItemSpriteSheet.BABY_KNIGHT;
        hitSound = Assets.Sounds.HIT_SWORD;
        hitSoundPitch = 0.86f;

        defaultAction = AC_ZAP;

        tier = 5;
    }

    @Override
    public int max(int lvl) {
        return  5*(tier) +   //25 + 5
                lvl*(tier); }

    //the ability runs on the normal duelist charge, the same light the old button shines
    @Override
    public String abilityInfo() {
        return Messages.get(this, "ability_desc", 20);
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, null);

        shine(hero);

        updateQuickslot();
        afterAbilityUsed(hero);
        //no spendAndNext here on purpose, the light costs no turn
    }

    //光!: the light itself, shared by the old button and the ability
    private void shine(Hero hero) {
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (mob.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[mob.pos]
                    && !mob.properties().contains(Char.Property.BOSS)
                    && !mob.properties().contains(Char.Property.MINIBOSS)
                    && mob.state != mob.PASSIVE) {
                Buff.affect(mob, MagicalSleep.class);
            }
        }

        int heal = hero.HT / 5;
        if (hero.buff(Aegis.AegisBuff.class) != null) {
            Aegis.addShield(heal);
        }
        hero.HP = Math.min(hero.HP + heal, hero.HT);
        hero.sprite.showStatus(CharSprite.POSITIVE, "+%dHP", heal);

        GameScene.flash(0x80FFFFFF);
        Sample.INSTANCE.play(Assets.Sounds.SKILL_BABYNIGHT);
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        //for Irene the ability takes the place of the old button
        if (hero.heroClass != HeroClass.IRENE) {
            actions.add(AC_ZAP);
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_ZAP)) {
            if (charge >= chargeCap && Dungeon.hero.belongings.weapon == this) {
                shine(hero);
                charge = 0;
            }
        }

        updateQuickslot();
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {

        if (defender.buff(Blindness.class) != null) {
            Buff.affect(attacker, Bless.class, 2f);
        }

        if (Random.Int(25) < 3 + level()) {
            if (Dungeon.hero.belongings.getItem(RingOfSunLight.class) != null) {
                if (Dungeon.hero.belongings.getItem(RingOfSunLight.class).isEquipped(Dungeon.hero)) {
                    SPCharge(3);
                }}
            SPCharge(5);

            Buff.affect(defender, Blindness.class, 5f);
        }

        updateQuickslot();

        return super.proc(attacker, defender, damage);
    }

    @Override
    public String status() {

        //Irene runs the ability, so show its charge rather than the old skill's
        if (Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.IRENE) {
            return super.status();
        }

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

    @Override
    public String desc() {
        String info = Messages.get(this, "desc");
        if (Dungeon.hero.belongings.getItem(RingOfSunLight.class) != null) {
            if (Dungeon.hero.belongings.getItem(RingOfSunLight.class).isEquipped(Dungeon.hero))
                info += "\n\n" + Messages.get( DivineAvatar.class, "setbouns");}

        return info;
    }
}
