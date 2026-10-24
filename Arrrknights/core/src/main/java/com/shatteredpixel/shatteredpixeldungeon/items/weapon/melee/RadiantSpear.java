package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.RadiantKnight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.BlazingSun;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSunLight;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfTenacity;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.TrueDamage;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class RadiantSpear extends MeleeWeapon {

    //the blessing lasts ABILITY_DURATION_BASE + weaponLevel/3 turns
    public static final int ABILITY_DURATION_BASE = 15;
    //paralysis applied by the rising sun
    public static final float ABILITY_PARALYSIS = 3f;

    {
        image = ItemSpriteSheet.NEARL_SPEAR;
        hitSound = Assets.Sounds.HIT_SPEAR;
        hitSoundPitch = 1.18f;

        tier = 5;
    }

    @Override
    public int max(int lvl) {
        return  5*(tier) +   //25 + 5
                lvl*(tier); }


    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (defender.buff(Blindness.class) != null) {
            Buff.affect(defender, Vulnerable.class, 3f); }

        if (defender.buff(Vulnerable.class) != null) {
            if (Dungeon.hero.belongings.getItem(RingOfSunLight.class) != null && Dungeon.hero.belongings.getItem(RingOfHaste.class) != null) {
                if (Dungeon.hero.belongings.getItem(RingOfSunLight.class).isEquipped(Dungeon.hero) && Dungeon.hero.belongings.getItem(RingOfHaste.class).isEquipped(Dungeon.hero)) {
                    damage += attacker.damageRoll() / 2;
                    return super.proc(attacker, defender, damage);
                }}
          damage += attacker.damageRoll() / 4;
        }

        return super.proc(attacker, defender, damage);
    }

    @Override
    public int reachFactor(Char owner) {
        int reach = super.reachFactor(owner);
        if (owner.buff(RadiantBlessing.class) != null) {
            reach += 1;
        }
        return reach;
    }

    //attacks made next to the hero or next to the summoned sun are true damage
    @Override
    public boolean dealsTrueDamage(Char owner, Char target) {
        RadiantBlessing bless = owner.buff(RadiantBlessing.class);
        if (bless == null) {
            return false;
        }
        if (Dungeon.level.distance(owner.pos, target.pos) <= 1) {
            return true;
        }
        Actor actor = Actor.findById(bless.sunId);
        if (!(actor instanceof Char)) {
            return false;
        }
        Char sun = (Char) actor;
        return sun.isAlive() && Dungeon.level.distance(sun.pos, target.pos) <= 1;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    protected int baseChargeUse(Hero hero, Char target) {
        //paid for with the weapon's own charge, not with duelist charges
        return 0;
    }
    @Override
    public String abilityInfo() {
        return Messages.get(this, "ability_desc", ABILITY_DURATION_BASE + buffedLvl()/3);
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        if (target == null) {
            return;
        }

        if (charge < chargeCap) {
            GLog.w(Messages.get(this, "no_charge"));
            return;
        }

        if (target == hero.pos || !Dungeon.level.adjacent(hero.pos, target)
                || !Dungeon.level.passable[target] || Dungeon.level.solid[target]) {
            GLog.w(Messages.get(this, "bad_target"));
            return;
        }

        //anything hostile already standing there is shoved one tile further out
        Char occupant = Actor.findChar(target);
        int shovedTo = -1;
        if (occupant != null) {
            if (occupant == hero || occupant.alignment == Char.Alignment.ALLY) {
                GLog.w(Messages.get(this, "bad_target"));
                return;
            }
            shovedTo = shoveDestination(hero, target, occupant);
            if (shovedTo == -1) {
                GLog.w(Messages.get(this, "no_room"));
                return;
            }
        }

        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, null);

        charge = 0;
        updateQuickslot();

        if (occupant != null) {
            int from = occupant.pos;
            occupant.move(shovedTo, false);
            if (occupant.sprite != null) {
                occupant.sprite.move(from, shovedTo);
            }
        }

        final BlazingSun sun = new BlazingSun();
        sun.setting(buffedLvl());
        GameScene.add(sun);
        ScrollOfTeleportation.appear(sun, target);
        Sample.INSTANCE.play(Assets.Sounds.SKILL_BABYNIGHT);

        //the rising sun burns and stuns whatever is next to it
        PathFinder.buildDistanceMap(target, BArray.not(Dungeon.level.solid, null), 1);
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] > 1) continue;

            Char ch = Actor.findChar(i);
            if (ch == null || ch == hero || !ch.isAlive() || ch.alignment == Char.Alignment.ALLY) continue;

            TrueDamage.deal(ch, Math.round(damageRoll(hero)), hero);
            if (ch.isAlive()) {
                Buff.affect(ch, Paralysis.class, ABILITY_PARALYSIS);
            }
        }

        //a new sun replaces the old one
        RadiantBlessing previous = hero.buff(RadiantBlessing.class);
        if (previous != null) {
            previous.detach();
        }
        Buff.affect(hero, RadiantBlessing.class, ABILITY_DURATION_BASE + buffedLvl()/3).sunId = sun.id();

        hero.sprite.operate(hero.pos);
        GameScene.flash(0x90FFFFFF);

        afterAbilityUsed(hero);
        hero.spendAndNext(Actor.TICK);
    }

    //the cell one tile further out from the hero, or -1 if the occupant cannot be moved there
    private int shoveDestination(Hero hero, int target, Char occupant) {
        if (occupant.properties().contains(Char.Property.IMMOVABLE)
                || occupant.properties().contains(Char.Property.NO_KNOCKBACK)) {
            return -1;
        }

        int w = Dungeon.level.width();
        int dx = (target % w) - (hero.pos % w);
        int dy = (target / w) - (hero.pos / w);

        int tx = (target % w) + dx;
        int ty = (target / w) + dy;
        if (tx < 0 || tx >= w || ty < 0 || ty >= Dungeon.level.height()) {
            return -1;
        }

        int dest = ty * w + tx;
        if (dest == occupant.pos
                || !Dungeon.level.passable[dest]
                || Dungeon.level.solid[dest]
                || Actor.findChar(dest) != null) {
            return -1;
        }
        if (Char.hasProp(occupant, Char.Property.LARGE) && !Dungeon.level.openSpace[dest]) {
            return -1;
        }

        return dest;
    }

    @Override
    public String status() {
        if (!isIdentified() || cursed) {
            return null;
        }
        if (chargeCap == 100) {
            return Messages.format("%d%%", charge);
        }
        return null;
    }

    @Override
    public String desc() {
        String info = Messages.get(this, "desc");
        if (Dungeon.hero.belongings.getItem(RingOfSunLight.class) != null && Dungeon.hero.belongings.getItem(RingOfHaste.class) != null) {
            if (Dungeon.hero.belongings.getItem(RingOfSunLight.class).isEquipped(Dungeon.hero) && Dungeon.hero.belongings.getItem(RingOfHaste.class).isEquipped(Dungeon.hero))
                info += "\n\n" + Messages.get( RadiantSpear.class, "setbouns");}

        return info;
    }

    //the sun is gone, but its light lingers on the wielder
    public static class RadiantBlessing extends FlavourBuff {

        public int sunId = 0;

        {
            type = buffType.POSITIVE;
            announced = true;
        }

        @Override
        public void detach() {
            super.detach();
            if (sunId != 0) {
                Actor sun = Actor.findById(sunId);
                if (sun instanceof Char && ((Char) sun).isAlive()) {
                    ((Char) sun).die(null);
                }
                sunId = 0;
            }
        }

        @Override
        public int icon() {
            return BuffIndicator.LIGHT;
        }

        private static final String SUNID = "sunId";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(SUNID, sunId);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            sunId = bundle.getInt(SUNID);
        }
    }
}
