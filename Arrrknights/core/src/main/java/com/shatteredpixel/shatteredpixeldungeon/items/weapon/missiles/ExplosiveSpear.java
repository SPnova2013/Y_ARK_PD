package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.IsekaiItem;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ExplosiveSpear extends MissileWeapon{
    {
        image = ItemSpriteSheet.EXPLOSIVE_SPEAR;
        hitSound = Assets.Sounds.HIT_SPEAR;
        hitSoundPitch = 1f;

        baseUses = 1;
        tier = 5;
    }

    public Fuse fuse;
    private static boolean lightingFuse = false;

    @Override
    public boolean isSimilar(Item item) {
        return super.isSimilar(item) && this.fuse == ((ExplosiveSpear) item).fuse;
    }

    public boolean explodesDestructively(){
        return true;
    }

    @Override
    public void execute(Hero hero, String action) {
        lightingFuse = true;
        super.execute(hero, action);
    }

    @Override
    protected void onThrow( int cell ) {
        if (!Dungeon.level.pit[ cell ] && lightingFuse) {
            if (Dungeon.hero.belongings.getItem(IsekaiItem.class) != null) {
                if (Dungeon.hero.belongings.getItem(IsekaiItem.class).isEquipped(Dungeon.hero)) {
                    if (Dungeon.hero.belongings.getItem(IsekaiItem.class).cursed) Actor.addDelayed(fuse = new ExplosiveSpear.Fuse().ignite(this), 8);
                    else Actor.addDelayed(fuse = new ExplosiveSpear.Fuse().ignite(this), 0);
                }
                else Actor.addDelayed(fuse = new ExplosiveSpear.Fuse().ignite(this), 2);
            }
            else Actor.addDelayed(fuse = new ExplosiveSpear.Fuse().ignite(this), 2);
        }
        if (Actor.findChar( cell ) != null && !(Actor.findChar( cell ) instanceof Hero) ){
            Char enemy = Actor.findChar( cell );
            if (enemy == null || enemy == curUser) {
                parent = null;
                super.onThrow( cell );
            } else {
                if (!curUser.shoot( enemy, this )) {
                    super.onThrow( cell );//
                } else {
                    Buff.append(Dungeon.hero, StickOn.class).setup(enemy, Dungeon.depth);
                }
            }
        } else
            super.onThrow( cell );
    }

    @Override
    public boolean doPickUp(Hero hero) {
        if (fuse != null) {
            GLog.w( Messages.get(this, "snuff_fuse") );
            fuse = null;
        }
        return super.doPickUp(hero);
    }

    public void explode(int cell, boolean stick){
        //We're blowing up, so no need for a fuse anymore.
        this.fuse = null;

        Sample.INSTANCE.play( Assets.Sounds.BLAST );

        if (explodesDestructively()) {

            ArrayList<Char> affected = new ArrayList<>();

            if (Dungeon.level.heroFOV[cell]) {
                CellEmitter.center(cell).burst(BlastParticle.FACTORY, 30);
            }

            boolean terrainAffected = false;
            for (int n : PathFinder.NEIGHBOURS9) {
                int c = cell + n;
                if (c >= 0 && c < Dungeon.level.length()) {
                    if (Dungeon.level.heroFOV[c]) {
                        CellEmitter.get(c).burst(SmokeParticle.FACTORY, 4);
                    }

                    if (Dungeon.level.flamable[c]) {
                        Dungeon.level.destroy(c);
                        GameScene.updateMap(c);
                        terrainAffected = true;
                    }

                    //destroys items / triggers bombs caught in the blast.
                    Heap heap = Dungeon.level.heaps.get(c);
                    if (heap != null)
                        heap.explode();

                    Char ch = Actor.findChar(c);
                    if (ch != null) {
                        affected.add(ch);
                    }
                }
            }

            for (Char ch : affected){

                //if they have already been killed by another bomb
                if(!ch.isAlive()){
                    continue;
                }

                int dmg = Random.NormalIntRange(8 + Dungeon.depth, 16 + Dungeon.depth*2);

                IsekaiItem.IsekaiBuff BombBuff = Dungeon.hero.buff( IsekaiItem.IsekaiBuff.class);
                if (BombBuff != null) {
                    dmg += BombBuff.itemLevel() * 4;
                }

                //those not at the center of the blast take less damage
                if (ch.pos != cell){
                    dmg = Math.round(dmg*0.67f);
                }
                if (ch.pos == cell && stick){
                    dmg *=2;
                }
                dmg*=2;
                dmg -= ch.drRoll();

                if (dmg > 0) {
                    ch.damage(dmg, this);
                }

                if (ch == Dungeon.hero && !ch.isAlive()) {
                    Dungeon.fail(Bomb.class);
                }

                if (BombBuff != null && ch.isAlive() && ch != Dungeon.hero) {
                    Buff.affect(ch, Paralysis.class, 3f);
                }
            }

            if (terrainAffected) {
                Dungeon.observe();
            }
        }
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return fuse != null ? new ItemSprite.Glowing( 0xFF0000, 0.6f) : null;
    }

    @Override
    public float durabilityPerUse(int level) {
        return 100f;
    }

    @Override
    public int value() {
        return 40 * quantity;
    }

    @Override
    public String desc() {
        if (fuse == null)
            return super.desc()+ "\n\n" + Messages.get(this, "desc_fuse");
        else
            return super.desc() + "\n\n" + Messages.get(this, "desc_burning");
    }

    private static final String FUSE = "fuse";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put( FUSE, fuse );
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains( FUSE ))
            Actor.add( fuse = ((ExplosiveSpear.Fuse)bundle.get(FUSE)).ignite(this) );
    }

    public static class Fuse extends Actor {

        {
            actPriority = BLOB_PRIO+1; //after hero, before other actors
        }

        private ExplosiveSpear exspear;

        public Fuse ignite(ExplosiveSpear exspear){
            this.exspear = exspear;
            return this;
        }

        @Override
        protected boolean act() {

            //something caused our bomb to explode early, or be defused. Do nothing.
            if (exspear.fuse != this){
                Actor.remove( this );
                return true;
            }

            //look for our bomb, remove it from its heap, and blow it up.
            for (Heap heap : Dungeon.level.heaps.valueList()) {
                if (heap.items.contains(exspear)) {
                    heap.remove(exspear);
                    exspear.explode(heap.pos, false);
                    diactivate();
                    Actor.remove(this);
                    return true;
                }
            }

            //can't find our bomb, something must have removed it, do nothing.
            exspear.fuse = null;
            Actor.remove( this );
            return true;
        }
    }

    public static class StickOn extends Buff{

        private Char victim;
        private int explodeDepth;
        private int left;

        public void setup( Char victim, int explodeDepth){
            this.victim = victim;
            this.explodeDepth = explodeDepth;
            left = 3;
        }

        @Override
        public boolean act() {
            if (explodeDepth == Dungeon.depth){
                left--;
                if (left <= 0){
                    ExplosiveSpear exps = new ExplosiveSpear();
                    exps.explode(victim.pos, true);
                    next();
                    detach();
                    return false;
                }
            }
            spend( TICK );
            return true;
        }

        private static final String VICTIM = "victim";
        private static final String EXPLODEN_DEPTH = "explode_depth";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(VICTIM, victim);
            bundle.put(EXPLODEN_DEPTH, explodeDepth);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            victim = (Char) bundle.get(VICTIM);
            explodeDepth = bundle.getInt(EXPLODEN_DEPTH);
        }

    }

}
