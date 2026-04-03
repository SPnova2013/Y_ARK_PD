package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMistress;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.OathofFire;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.List;

public class SwordofArtorius extends MeleeWeapon {
    {
        image = ItemSpriteSheet.ARTORIUS;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 1.12f;

        tier = 5;
    }

    @Override
    public int max(int lvl) {
        return  4 * (tier-2) +
                lvl*(tier-2);
    }

    public String statsInfo() {
        if (isIdentified()) {
            return Messages.get(this, "stats_desc", 4+buffedLvl(), 12+buffedLvl()*4);
        } else {
            return Messages.get(this, "typical_stats_desc", 4, 12);
        }
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {

        Ballistica beam = new Ballistica(attacker.pos, defender.pos, Ballistica.WONT_STOP);
        int maxDistance = Math.min(5, beam.dist);
        int cell = beam.path.get(Math.min(beam.dist, maxDistance));
        attacker.sprite.parent.add(new Beam.DeathRay(attacker.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(cell)));
        boolean terrainAffected = false;

        ArrayList<Char> chars = new ArrayList<>();


        int terrainPassed = 2;
        for (int c : beam.subPath(1, maxDistance)) {

            Char ch;
            if ((ch = Actor.findChar( c )) != null) {

                //we don't want to count passed terrain after the last enemy hit. That would be a lot of bonus levels.
                //terrainPassed starts at 2, equivalent of rounding up when /3 for integer arithmetic.
                terrainPassed = terrainPassed%3;

                chars.add( ch );
            }

            if (Dungeon.level.solid[c]) {
                terrainPassed++;
            }

            if (Dungeon.level.flamable[c]) {

                Dungeon.level.destroy( c );
                GameScene.updateMap( c );
                terrainAffected = true;

            }

            CellEmitter.center( c ).burst( PurpleParticle.BURST, Random.IntRange( 1, 2 ) );
        }

        if (terrainAffected) {
            Dungeon.observe();
        }

        int dmg = Random.NormalIntRange(4+buffedLvl(), 12+buffedLvl()*4);
        if(isSetbouns()) dmg *= 1.3f;
        for (Char ch : chars) {
            ch.damage(dmg, this );
            ch.sprite.centerEmitter().burst( PurpleParticle.BURST, Random.IntRange( 1, 2 ) );
            ch.sprite.flash();
        }

        return super.proc(attacker, defender, damage);
    }

    @Override
    public String desc() {
        String info = Messages.get(this, "desc");
        if (isSetbouns()) info += "\n\n" + Messages.get( SwordofArtorius.class, "setbouns");

        return info;
    }

    private boolean isSetbouns() {
        if (Dungeon.hero.belongings.getItem(RingOfMistress.class) != null) {
            if (Dungeon.hero.belongings.getItem(RingOfMistress.class).isEquipped(Dungeon.hero))
                return true;
        }
        return false;
    }
    @Override
    public String abilityInfo() {
        if (levelKnown){
            return Messages.get(this, "ability_desc");
        } else {
            return Messages.get(this, "typical_ability_desc");
        }
    }
    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        beforeAbilityUsed(hero, null);
        Buff.prolong(hero, OathofFire.OathFireBuff.class , OathofFire.OathFireBuff.DURATION);
        Buff.affect(hero, FireRain.class).set(100f);
        hero.sprite.operate(hero.pos);
        hero.next();
        afterAbilityUsed(hero);
    }

    public static class FireRain extends Buff {
        {
            type = buffType.POSITIVE;
        }

        protected float left;

        private static final String LEFT = "left";
        private static final String SPAWNERS = "spawners";
        private static final String TIMER = "timer";

        private ArrayList<Integer> spawners = new ArrayList<>();

        private int spawnTimer = 0;

        private ArrayList<Emitter> activeEmitters = new ArrayList<>();

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(LEFT, left);

            int[] arr = new int[spawners.size()];
            for (int i = 0; i < spawners.size(); i++) {
                arr[i] = spawners.get(i);
            }
            bundle.put(SPAWNERS, arr);

            bundle.put(TIMER, spawnTimer);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            left = bundle.getFloat(LEFT);

            spawners.clear();
            int[] arr = bundle.getIntArray(SPAWNERS);
            if (arr != null) {
                for (int v : arr) {
                    spawners.add(v);
                }
            }

            spawnTimer = bundle.getInt(TIMER);

            activeEmitters.clear();
        }

        public void set(float duration) {
            this.left = duration;

            spawnAtLeft();
        }

        private void seedColumnAtX(int x) {
            int w = Dungeon.level.width();
            int h = Dungeon.level.height();

            if (x < 0 || x >= w) {
                return;
            }

            for (int y = 0; y < h; y++) {
                int cell = x + y * w;

                if (!Dungeon.level.solid[cell]) {
                    GameScene.add(Blob.seed(cell, 3, Fire.class));

                    try {
                        Emitter em = CellEmitter.get(cell);
                        em.pour(Speck.factory(Speck.INFERNO, true), 0.1f);
                        activeEmitters.add(em);
                    } catch (Exception e) {
                    }
                }
            }
        }

        private void spawnAtLeft() {
            spawners.add(0);
            seedColumnAtX(0);
            spawnTimer = 0;
        }

        @Override
        public boolean act() {
            if (!activeEmitters.isEmpty()) {
                for (Emitter em : activeEmitters) {
                    if (em != null) {
                        em.on = false;
                    }
                }
                activeEmitters.clear();
            }

            spend(TICK);
            left -= TICK;

            int w = Dungeon.level.width();

            if (!spawners.isEmpty()) {
                List<Integer> newSpawners = new ArrayList<>(spawners.size());
                for (int x : spawners) {
                    seedColumnAtX(x);

                    int nx = x + 1;
                    if (nx < w) {
                        newSpawners.add(nx);
                    }
                }
                spawners.clear();
                spawners.addAll(newSpawners);
            }

            spawnTimer++;
            if (spawnTimer >= 10) {
                spawnAtLeft();
            }

            if (left <= 0) {
                for (Emitter em : activeEmitters) {
                    if (em != null) em.on = false;
                }
                activeEmitters.clear();
                detach();
            }

            return true;
        }
    }
}
