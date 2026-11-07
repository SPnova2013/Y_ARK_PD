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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.PaperTwin;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BatSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BeeSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BreakerSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.KazemaruSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class KazemaruWeapon extends MeleeWeapon {

    {
        image = ItemSpriteSheet.KAZEMARU;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 1.11f;

        tier = 4;
    }

    @Override
    public int max(int lvl) {
        return  5*(tier) +
                lvl*(tier+1);
    }

    public String statsInfo() {
        if (isIdentified()) {
            return Messages.get(this, "stats_desc", 15+buffedLvl(), 20+buffedLvl()*5);//change from budding
        } else {
            return Messages.get(this, "typical_stats_desc", 15, 20);
        }
    }

    //how much of the hero's life the fold costs
    public static final int ABILITY_LIFE_COST = 50;

    //纸艺·双影: fold a paper twin out of half of the hero's life
    @Override
    protected void duelistAbility(Hero hero, Integer target) {

        ArrayList<Integer> spots = new ArrayList<>();
        for (int i : PathFinder.NEIGHBOURS8) {
            int p = hero.pos + i;
            if (p < 0 || p >= Dungeon.level.length()) continue;
            //guard against stepping around the edge of a row
            if (Dungeon.level.distance(hero.pos, p) != 1) continue;
            if (Actor.findChar(p) != null || !Dungeon.level.passable[p]) continue;
            spots.add(p);
        }

        if (spots.isEmpty()) {
            GLog.w(Messages.get(this, "no_space"));
            return;
        }

        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, null);

        //pay with half of the life it is folded from
        int lost = hero.HP - Math.max(1, hero.HP / 2);
        hero.HP -= lost;
        if (lost > 0) {
            hero.sprite.showStatus(CharSprite.NEGATIVE, "-%d", lost);
        }

        PaperTwin twin = new PaperTwin();
        twin.setup(hero);
        GameScene.add(twin);
        ScrollOfTeleportation.appear(twin, spots.get(Random.Int(spots.size())));
        Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
        Invisibility.dispel();

        afterAbilityUsed(hero);
        hero.spendAndNext(Actor.TICK);
    }

    @Override
    public String abilityInfo() {
        return Messages.get(this, "ability_desc", ABILITY_LIFE_COST);
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (Random.Int(10) == 0) {
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

                KazemaruSummon summon = new KazemaruSummon();
                summon.GetWeaponLvl(buffedLvl());
                summon.GetTarget(defender);
                summon.alignment=attacker.alignment;
                GameScene.add(summon);
                ScrollOfTeleportation.appear(summon, respawnPoints.get(index));

                respawnPoints.remove(index);
                spawnd++;
            }
        }

        return super.proc(attacker, defender, damage);
    }

    public static class KazemaruSummon extends Mob {
        {
            HP = HT = 1;

            spriteClass = KazemaruSprite.class;

            flying = true;
            state = WANDERING;

        }

        @Override
        public void onAttackComplete() {
            attack( enemy );
            next();
            die(this);
        }
        @Override
        public boolean attack(Char enemy) {
            if (enemy == null) return false;

            boolean visibleFight = Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[enemy.pos];

            if (hit( this, enemy, true )) {

                int dmg = damageRoll();
                enemy.damage( dmg, this );
                enemy.sprite.bloodBurstA( sprite.center(), dmg );
                enemy.sprite.flash();

                if (Dungeon.level.heroFOV[pos]) Sample.INSTANCE.play(Assets.Sounds.HIT_SLASH, 1f, Random.Float(0.96f, 1.05f));

                if (enemy == Dungeon.hero && !enemy.isAlive()) {
                    Dungeon.fail( getClass() );
                }
            } else {
                enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
            }

            if (!enemy.isAlive() && visibleFight) {
                if (enemy == Dungeon.hero) {

                    Dungeon.fail( getClass() );
                    GLog.n( Messages.capitalize(Messages.get(Char.class, "kill", name())) );
                }
            }

            return true;

        }
        @Override
        public int damageRoll() {
            return Random.NormalIntRange(maxLvl + 15, 20 + (maxLvl * 5));
        }
        @Override//change from budding
        public int attackSkill( Char target ){
            return INFINITE_ACCURACY;
        }
        public void GetWeaponLvl(int wlvl) {
            maxLvl = wlvl;
        }
        public void GetTarget(Char t) {target = t.pos;}//change from budding
        private static String ISALLY = "is_ally";
        @Override
        public void storeInBundle( Bundle bundle ) {
            super.storeInBundle( bundle );
            bundle.put( ISALLY, alignment );
        }

        @Override
        public void restoreFromBundle( Bundle bundle ) {
            super.restoreFromBundle( bundle );
            alignment = bundle.getEnum( ISALLY ,Alignment.class);
        }
    }
}