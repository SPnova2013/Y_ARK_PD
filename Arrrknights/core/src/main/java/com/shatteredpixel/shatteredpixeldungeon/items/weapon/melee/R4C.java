package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Momentum;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SnipersMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.UpMagazine;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class R4C extends MeleeWeapon {
    public static final String AC_ZAP = "ZAP";
    public static final String AC_RELOAD = "RELOAD";
    {
        image = ItemSpriteSheet.ENFILD;
        hitSound = Assets.Sounds.HIT_SHOTGUN;
        hitSoundPitch = 0.9f;

        usesTargeting = true;

        defaultAction = AC_ZAP;

        tier = 5;
    }

    private int bullettier = 5;
    private int bullet = 5;
    private int bulletCap = 25;
    private boolean spshot = false; // 특수 사격 여부

    private float RELOAD_TIME = 3f;

    @Override
    public int max(int lvl) {
        return  3*(tier) +    // 9 + 1
                lvl*(tier-2);
    }

    public int shotmin() {
        return 3 + level();
    }

    public int shotmax() {
        return 7 + bullettier + (level() * bullettier);
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_ZAP);
        actions.add(AC_RELOAD);
        return actions;
    }

    public void reload(int tier, boolean sp) {
        bullettier = tier;
        bullet = bulletCap;

        spshot = sp;

        if (Dungeon.hero.subClass == HeroSubClass.FREERUNNER) Dungeon.hero.spendAndNext(RELOAD_TIME / 2);
        else Dungeon.hero.spendAndNext(RELOAD_TIME);
        Dungeon.hero.sprite.operate( Dungeon.hero.pos );
    }

    @Override
    public String status() { return bullet+""; }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_ZAP) && bullet > 0 && Dungeon.hero.belongings.weapon == this) {
            if (this.cursed != true) {
                cursedKnown = true;
                GameScene.selectCell(zapper);
            }
            else {
                Buff.affect(Dungeon.hero, Burning.class).reignite(Dungeon.hero,4f);
                cursedKnown = true;
                bullet -= 1;
            }
        }

        if (action.equals(AC_RELOAD)) {
            curUser = hero;
            GameScene.selectItem(itemSelector, WndBag.Mode.MISSILEWEAPON, Messages.get(this, "prompt"));
        }
    }

    protected static CellSelector.Listener zapper = new CellSelector.Listener() {

        @Override
        public void onSelect(Integer target) {

            if (target != null) {

                final R4C ss;
                if (curItem instanceof R4C) {
                    ss = (R4C) C1_9mm.curItem;

                    Ballistica shot = new Ballistica(curUser.pos, target, Ballistica.PROJECTILE);
                    int cell = shot.collisionPos;

                    if (target == curUser.pos || cell == curUser.pos) {
                        GLog.i(Messages.get(R4C.class, "self_target"));
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
            return Messages.get(R4C.class, "prompt");
        }
    };

    protected void fx( Ballistica bolt, Callback callback ) {
        MagicMissile.boltFromChar( curUser.sprite.parent,
                MagicMissile.GUN_SHOT,
                curUser.sprite,
                bolt.collisionPos,
                callback);
        Sample.INSTANCE.play( Assets.Sounds.ZAP );
    }
    public boolean tryToZap(Hero owner, int target) {

        if (bullet >= 1) {
            return true;
        } else {
            GLog.w(Messages.get(this, "fizzles"));
            return false;
        }
    }


    protected void onZap( Ballistica bolt ) {
        Char ch = Actor.findChar( bolt.collisionPos );
        if (ch != null) {
            int dmg = Random.Int(shotmin(), shotmax());
            if (Dungeon.hero.subClass == HeroSubClass.SNIPER) dmg -= (ch.drRoll() / 2);
            else dmg -= ch.drRoll();
            if (ch.hit(Dungeon.hero, ch, false)) {
                if (Dungeon.hero.hasTalent(Talent.PROJECTILE_MOMENTUM) && Dungeon.hero.buff(Momentum.class) != null &&  Dungeon.hero.buff(Momentum.class).freerunning()) {
                    dmg *= 1 + (Dungeon.hero.pointsInTalent(Talent.PROJECTILE_MOMENTUM) / 10); }

                ch.damage(dmg, this);
                Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC, 1, Random.Float(0.87f, 1.15f));

                if (spshot) {
                    Ballistica trajectory = new Ballistica(curUser.pos, ch.pos, Ballistica.STOP_TARGET);
                    trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size()-1), Ballistica.PROJECTILE);
                    WandOfBlastWave.throwChar(ch, trajectory, 2); // 넉백 효과
                }

                ch.sprite.burst(0xFFFFFFFF, buffedLvl() / 2 + 2);

                int bonusTurns = Dungeon.hero.hasTalent(Talent.SHARED_UPGRADES) ? this.buffedLvl() : 0;
                if (Dungeon.hero.subClass == HeroSubClass.SNIPER) Buff.prolong(Dungeon.hero, SnipersMark.class, SnipersMark.DURATION).set(ch.id(), bonusTurns);
            }
            else {
                String defense = ch.defenseVerb();
                ch.sprite.showStatus( CharSprite.NEUTRAL, defense );

                //TODO enemy.defenseSound? currently miss plays for monks/crab even when they parry
                Sample.INSTANCE.play(Assets.Sounds.MISS);

            }

        } else {
            Dungeon.level.pressCell(bolt.collisionPos);
        }

        bullet -=1;
        updateQuickslot();

        curUser.spendAndNext(0.8f);
    }

    private final WndBag.Listener itemSelector = new WndBag.Listener() {
        @Override
        public void onSelect( final Item item ) {
            if (item != null) {
                if (item instanceof UpMagazine) {reload(((MissileWeapon)item).tier, true); }
                else reload(((MissileWeapon)item).tier, false);
                item.detach(Dungeon.hero.belongings.backpack);
            }
        }
    };

    @Override
    public String desc() {
        return Messages.get(this, "desc", bullettier);
    }

    public String statsInfo() {
        if (spshot) return Messages.get(this, "stats_desc_sp", shotmin(),shotmax());
        return Messages.get(this, "stats_desc", shotmin(),shotmax());
    }


    private static final String BULLET = "bullet";
    private static final String TIER = "bullettier";
    private static final String SP = "spshot";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(BULLET, bullet);
        bundle.put(TIER, bullettier);
        bundle.put(SP, spshot);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bulletCap > 0) bullet = Math.min(bulletCap, bundle.getInt(BULLET));
        else bullet = bundle.getInt(BULLET);

        bullettier = bundle.getInt(TIER);
        spshot = bundle.getBoolean(SP);
    }
}