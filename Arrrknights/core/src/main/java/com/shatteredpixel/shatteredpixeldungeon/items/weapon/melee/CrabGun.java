package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hallucination;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Brute;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.MirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK2.AncientKin;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CustomeSet;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.Bug_ASprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HaundSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.Rock_CrabSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class CrabGun extends MeleeWeapon {
    {
        image = ItemSpriteSheet.BEENS;
        hitSound = Assets.Sounds.ATK_SPIRITBOW;
        hitSoundPitch = 1f;

        tier = 4;
        RCH = 2;
    }

    @Override
    public int max(int lvl) {
        return  3*(tier+1) +   //15 + 3
                lvl*(tier-1); }

    //the ability keeps the crab call, but on the duelist's own charge
    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        //find a spot for the crab before spending anything
        ArrayList<Integer> spots = new ArrayList<>();
        for (int i : PathFinder.NEIGHBOURS8) {
            int cell = hero.pos + i;
            if (cell < 0 || cell >= Dungeon.level.length()) continue;
            if (Dungeon.level.distance(hero.pos, cell) != 1) continue;
            if (Actor.findChar(cell) != null || !Dungeon.level.passable[cell]) continue;
            spots.add(cell);
        }

        if (spots.isEmpty()) {
            GLog.w(Messages.get(this, "no_space"));
            return;
        }

        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, null);

        summonCrab(spots.get(Random.Int(spots.size())));

        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.SKILL_BASIC);
        Invisibility.dispel();
        updateQuickslot();

        afterAbilityUsed(hero);
        hero.spendAndNext(Actor.TICK);
    }

    @Override
    public String abilityInfo() {
        return Messages.get(this, "ability_desc");
    }

    //the crab itself, shared by the old attack trigger and the ability
    private void summonCrab(int cell) {
        MetalCrab crab = new MetalCrab();
        crab.setting(buffedLvl());
        GameScene.add(crab);
        ScrollOfTeleportation.appear(crab, cell);

        if (setbouns()) {
            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                if (mob.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[mob.pos]) {
                    Buff.affect(mob, Hallucination.class).set(Hallucination.DURATION / 2);
                }
            }
        }
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        //Irene runs the ability instead, so the weapon no longer charges from attacks
        boolean irene = Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.IRENE;

        if (!irene && (attacker instanceof Hero || attacker instanceof DriedRose.GhostHero)) {
            if (charge >= chargeCap) {
                ArrayList<Integer> respawnPoints = new ArrayList<>();

                for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                    int p = defender.pos + PathFinder.NEIGHBOURS8[i];
                    if (Actor.findChar(p) == null && Dungeon.level.passable[p]) {
                        respawnPoints.add(p);
                    }
                }
                int spawnd = 0;
                while (respawnPoints.size() > 0 && spawnd == 0) {
                    int index = Random.index(respawnPoints);

                    summonCrab(respawnPoints.get(index));

                    respawnPoints.remove(index);
                    spawnd++;
                }
                charge = 0;
            } else SPCharge((Random.IntRange(7 + buffedLvl() / 4, 11 + buffedLvl() / 2)));
        }
        updateQuickslot();
        return super.proc(attacker, defender, damage);
    }

    public void SpawnCrab(int lvl, int pos) {
        CrabGun.MetalCrab crab = new CrabGun.MetalCrab();
        crab.setting(lvl);
        GameScene.add(crab);
        ScrollOfTeleportation.appear(crab, pos);
    }

    @Override
    public String desc() {
        String info = Messages.get(this, "desc");
       if (setbouns()) info += "\n\n" + Messages.get( CrabGun.class, "setbouns");

        return info;
    }

    private boolean setbouns() {
        if (Dungeon.hero.belongings.getItem(RingOfWealth.class) != null && Dungeon.hero.belongings.getItem(CustomeSet.class) != null) {
            if (Dungeon.hero.belongings.getItem(RingOfWealth.class).isEquipped(Dungeon.hero) && Dungeon.hero.belongings.getItem(CustomeSet.class).isEquipped(Dungeon.hero))
                return true;
        }
            return false;
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

    public static class MetalCrab extends Mob {
        {
            spriteClass = Rock_CrabSprite.class;
            baseSpeed = 3f;

            immunities.add(Silence.class);
            alignment = Alignment.ALLY;

            state = WANDERING;
        }


        @Override
        public int damageRoll() {
            return Random.NormalIntRange( 2 + Dungeon.depth / 2, 6 + (Dungeon.depth / 2) + maxLvl * 2 );
        }

        @Override
        public int attackSkill( Char target ) {
            return 10 + Dungeon.depth / 2 + maxLvl;
        }

        @Override
        public int drRoll() {
            return Random.NormalIntRange(0, 3 + maxLvl / 2);
        }

        public void setting(int setlvl)
        {
            CustomeSet.CustomSetBuff setBuff = Dungeon.hero.buff( CustomeSet.CustomSetBuff.class);
            int itembuff = 0;
            if (setBuff != null) itembuff = setBuff.itemLevel();
            HP=HT=30 + setlvl * 6 + itembuff * 5;
            defenseSkill = 1 + setlvl + itembuff;
            maxLvl = setlvl + itembuff / 2;
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            enemySeen = true;
        }
    }
}