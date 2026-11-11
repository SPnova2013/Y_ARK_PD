package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.shatteredpixeldungeon.items.AnnihilationGear;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class EX42 extends MeleeWeapon {
    public static final String AC_ZAP = "ZAP";
    {
        image = ItemSpriteSheet.EX41;
        hitSound = Assets.Sounds.HIT_WALL1;
        hitSoundPitch = 0.65f;

        defaultAction = AC_ZAP;

        tier = 1;

        bones = false;
    }

    public boolean swiching = false;

    @Override
    public int max(int lvl) {
        if (swiching == false) {
            return  1 + 4*(tier+1) +     //9
                    lvl*(tier+1);  //+1 per level, down from +2
        } else {
            return  1 + tier+1 +     //3
                    lvl*(tier);}  //+1 per level, down from +2
    }

    //如你所愿: the next landed hit pins its target and folds two EX44 beside it
    public static final int ABILITY_SPAWNS = 2;
    public static final float ABILITY_PARALYSIS = 1f;

    //a free action: the wish is simply made
    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, null);

        Buff.affect(hero, WishGranted.class);

        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
        Invisibility.dispel();

        afterAbilityUsed(hero);
        //no spendAndNext here on purpose, making a wish costs no turn
    }

    @Override
    public String abilityInfo() {
        return Messages.get(this, "ability_desc", ABILITY_SPAWNS, (int) ABILITY_PARALYSIS);
    }

    //the wish itself
    public static class WishGranted extends Buff {

        {
            type = buffType.POSITIVE;
            announced = true;
        }

        @Override
        public int icon() {
            return BuffIndicator.WEAPON;
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc");
        }
    }

    //pin the target, then fold up to two EX44 around it
    private void grantWish(Char enemy) {
        Buff.affect(enemy, Paralysis.class, ABILITY_PARALYSIS);

        //the copies are built from whatever weapon the hero is holding right now
        int weaponLevel = 0;
        KindOfWeapon wep = Dungeon.hero.belongings.weapon;
        if (wep != null) {
            weaponLevel = wep.level();
        }

        int augtype = 0;
        if (wep instanceof MeleeWeapon) {
            switch (((MeleeWeapon) wep).augment) {
                case DAMAGE:    augtype = 1; break;
                case SPEED:     augtype = 2; break;
                case OVERLOAD:  augtype = 3; break;
                default:        augtype = 0; break;
            }
        }

        int spawned = 0;
        for (int i : PathFinder.NEIGHBOURS8) {
            if (spawned >= ABILITY_SPAWNS) break;

            int cell = enemy.pos + i;
            if (cell < 0 || cell >= Dungeon.level.length()) continue;
            //guard against stepping around the edge of a row
            if (Dungeon.level.distance(enemy.pos, cell) != 1) continue;
            if (Actor.findChar(cell) != null || !Dungeon.level.passable[cell]) continue;

            AnnihilationGear.EX44 w = new AnnihilationGear.EX44();
            w.pos = cell;
            w.setting(Dungeon.hero, weaponLevel, augtype);
            GameScene.add(w);
            spawned++;
        }
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {

        if (attacker.buff(WishGranted.class) != null && defender.isAlive()) {
            Buff.detach(attacker, WishGranted.class);
            grantWish(defender);
        }

        if (defender instanceof Piranha)
        {
            damage *= 3;
        }
        return super.proc(attacker, defender, damage);
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
            if (swiching == true) {swiching = false; RCH = 1; ACC = 1f;}
            else {swiching = true; RCH = 50; ACC = 3f;}

            updateQuickslot();
            curUser.spendAndNext(0.5f);}
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
    private static final String RCHSAVE = "RCH";
    private static final String ACCSAVE = "ACC";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SWICH, swiching);
        bundle.put(RCHSAVE, RCH);
        bundle.put(ACCSAVE, ACC);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        swiching = bundle.getBoolean(SWICH);
        RCH = bundle.getInt(RCHSAVE);
        ACC = bundle.getInt(ACCSAVE);
    }

}
