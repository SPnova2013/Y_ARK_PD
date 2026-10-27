package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class MidnightSword extends MeleeWeapon {
    public static final String AC_ZAP = "ZAP";
    {
        image = ItemSpriteSheet.MIDSWORD;
        hitSound = Assets.Sounds.HIT;
        hitSoundPitch = 1f;

        tier = 2;

        usesTargeting = true;
        defaultAction = AC_ZAP;
    }

    protected int collisionProperties = Ballistica.MAGIC_BOLT;

    private int arts = 3;
    private int artschargeCap = 3;

    //the enchantment lasts ABILITY_DURATION_BASE + weaponLevel/2 turns
    public static final int ABILITY_DURATION_BASE = 5;
    //attack power is raised by this fraction while the blade is enchanted
    public static final float ABILITY_DAMAGE_BONUS = 0.3f;

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, null);

        Buff.prolong(hero, WeaponEnchant.class, ABILITY_DURATION_BASE + buffedLvl()/2);

        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.SKILL_BASIC);

        afterAbilityUsed(hero);
        //no spendAndNext here on purpose, enchanting costs no turn
    }

    @Override
    public String abilityInfo() {
        return Messages.get(this, "ability_desc", ABILITY_DURATION_BASE + buffedLvl()/2);
    }

    @Override
    public int damageRoll(Char owner) {
        int dmg = super.damageRoll(owner);
        if (owner.buff(WeaponEnchant.class) != null) {
            dmg = Math.round(dmg * (1f + ABILITY_DAMAGE_BONUS));
        }
        return dmg;
    }

    //while enchanted the blade's damage counts as spell damage, see Char#damageTypeOf
    @Override
    public boolean dealsSpellDamage(Char owner) {
        return owner.buff(WeaponEnchant.class) != null;
    }

    //the blade drinks in magic: harder swings, and the wound burns like a spell
    public static class WeaponEnchant extends FlavourBuff {

        {
            type = buffType.POSITIVE;
            announced = true;
        }

        @Override
        public int icon() {
            return BuffIndicator.WEAPON;
        }
    }

    @Override
    public int max(int lvl) {
        return  5*(tier) +  //9 + 2
                lvl*(tier);
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        SPCharge(1);
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

        if (action.equals(AC_ZAP) && arts > 0) {
            if (this.cursed != true) {
                cursedKnown = true;
                GameScene.selectCell(zapper);
            }
            else {
                Buff.affect(Dungeon.hero, Burning.class).reignite(Dungeon.hero,4f);
                cursedKnown = true;
                arts -= 1;
            }
        }
    }

    public String statsInfo() {
            return Messages.get(this, "stats_desc", 2+buffedLvl(),11+buffedLvl()*2);
    }

    public void SPCharge(int n) {
        if (Random.Int(17) < 2) {
            arts += n;
            if (artschargeCap < arts) arts = artschargeCap;
            updateQuickslot();
        }
    }

    @Override
    public String defaultAction() {
        return AC_ZAP;
    }
    @Override
    public String status() {
        return arts + "/" + artschargeCap;
    }

    @Override
    public String statusBottom() {
        if (!isIdentified() || cursed) return null;
        if (isEquipped(Dungeon.hero)
                && Dungeon.hero.buff(Charger.class) != null) {
            Charger buff = Dungeon.hero.buff(Charger.class);
            return String.valueOf(buff.charges);
        } else {
            return super.status();
        }
    }
    private static final String CHARGE = "arts";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(CHARGE, arts);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (artschargeCap > 0) arts = Math.min(artschargeCap, bundle.getInt(CHARGE));
        else arts = bundle.getInt(CHARGE);
    }

    protected static CellSelector.Listener zapper = new CellSelector.Listener() {

        @Override
        public void onSelect(Integer target) {

            if (target != null) {

                final MidnightSword ss;
                if (curItem instanceof MidnightSword) {
                    ss = (MidnightSword) MidnightSword.curItem;

                    Ballistica shot = new Ballistica(curUser.pos, target, Ballistica.PROJECTILE);
                    int cell = shot.collisionPos;

                    if (target == curUser.pos || cell == curUser.pos) {
                        GLog.i(Messages.get(MidnightSword.class, "self_target"));
                        return;
                    }

                    curUser.sprite.zap(cell);

                    //attempts to target the cell aimed at if something is there, otherwise targets the collision pos.
                    if (Actor.findChar(target) != null)
                        QuickSlotButton.target(Actor.findChar(target));
                    else
                        QuickSlotButton.target(Actor.findChar(cell));

                    if (ss.tryToZap(curUser, target)) {
                        ss.fx(shot, new Callback() {
                            public void call() {
                                ss.onZap(shot);
                            }
                        });
                    }

                }
            }
        }

        @Override
        public String prompt() {
            return Messages.get(MidnightSword.class, "prompt");
        }
    };

    protected void fx( Ballistica bolt, Callback callback ) {
        MagicMissile.boltFromChar( curUser.sprite.parent,
                MagicMissile.MAGIC_MISSILE,
                curUser.sprite,
                bolt.collisionPos,
                callback);
        Sample.INSTANCE.play( Assets.Sounds.ZAP );
    }
    public boolean tryToZap(Hero owner, int target) {

        if (owner.buff(MagicImmune.class) != null) {
            GLog.w(Messages.get(this, "no_magic"));
            return false;
        }

        if (arts >= 1) {
            return true;
        } else {
            GLog.w(Messages.get(this, "fizzles"));
            return false;
        }
    }


    protected void onZap( Ballistica bolt ) {
        Char ch = Actor.findChar( bolt.collisionPos );
        if (ch != null) {
            int dmg = Random.Int(2+buffedLvl(), 11+buffedLvl()*2);
            ch.damage(dmg, this);
            Sample.INSTANCE.play( Assets.Sounds.HIT_MAGIC, 1, Random.Float(0.87f, 1.15f) );

            ch.sprite.burst(0xFFFFFFFF, buffedLvl() / 2 + 2);

        } else {
            Dungeon.level.pressCell(bolt.collisionPos);
        }

        arts -=1;
        updateQuickslot();

       curUser.spendAndNext(1f);
    }
}