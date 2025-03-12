package com.shatteredpixel.shatteredpixeldungeon.items.wands.SSP;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SoulMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.DamageWand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blazing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class StaffOfValstrax extends DamageWand {
    private static ItemSprite.Glowing COL = new ItemSprite.Glowing(0xFF3333);
    {
        image = ItemSpriteSheet.WAND_VALSTRAX;
        collisionProperties = Ballistica.PROJECTILE;
    }
    @Override
    public ItemSprite.Glowing glowing() {
        return COL;
    }
    @Override
    public int min(int lvl) {
        return 1+lvl;
    }

    @Override
    public int max(int lvl) {
        return 9+3*lvl;
    }

    @Override
    protected void onZap(Ballistica bolt) {
        Sample.INSTANCE.play( Assets.Sounds.HIT_MAGIC, 1, Random.Float(0.87f, 1.15f) );
        Buff.append(Dungeon.hero, ValstraxIncoming.class).setup(bolt.collisionPos, Dungeon.depth, chargesPerCast(), buffedLvl());
    }

    @Override
    protected int chargesPerCast() {
        //consumes 30% of current charges, rounded up, with a minimum of one.
        return Math.max(1, (int)Math.ceil(curCharges*0.3f));
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        new Blazing().proc(staff, attacker, defender, damage);
    }

    @Override
    public String statsDesc() {
        if (levelKnown)
            return Messages.get(this, "stats_known_desc", chargesPerCast(), min(), max(), min()*(chargesPerCast()+1), max()*(chargesPerCast()+1) );
        else
            return Messages.get(this, "stats_desc", chargesPerCast(), min(0), max(0));
    }

    public static class ValstraxIncoming extends Buff{
        private int targetPos;
        private int explodeDepth;
        private int remainingPhases;
        private int currentPhase;
        private int totalPhases;
        private int wandLevel;
        public void setup( int pos, int explodeDepth, int phase, int lvl){
            this.targetPos = pos;
            this.explodeDepth = explodeDepth;
            remainingPhases = phase+1;
            this.currentPhase = phase;
            totalPhases = phase;
            wandLevel = lvl;
        }

        @Override
        public boolean act() {
            if (explodeDepth == Dungeon.depth){
                remainingPhases--;
                if (remainingPhases <= 0){
                    StaffOfValstrax sov = new StaffOfValstrax();
                    ArrayList<Char> affected = new ArrayList<>();
                    do{
                        PathFinder.buildDistanceMap(targetPos, BArray.not( Dungeon.level.solid, null ), currentPhase);
                        for (int i = 0; i < PathFinder.distance.length; i++) {
                            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                                Char ch = Actor.findChar(i);
                                if (ch != null){
                                    affected.add(ch);
                                }
                            }
                        }
                        PathFinder.buildDistanceMap(targetPos, BArray.not( Dungeon.level.solid, null ), currentPhase -1 );
                        for (int i = 0; i < PathFinder.distance.length; i++) {
                            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                                Char ch = Actor.findChar(i);
                                if (ch != null){
                                    affected.remove(ch);
                                }
                            }
                        }
                        for (Char ch : affected){
                            int damage = Random.NormalIntRange( 5 + wandLevel, 15 + 3*wandLevel );
                            ch.damage(Math.round(damage*(totalPhases - currentPhase +1)), sov);
                            if (ch == Dungeon.hero && !ch.isAlive()){
                                Dungeon.fail(StaffOfValstrax.class);
                            }
                        }
                        affected.clear();
                        currentPhase--;
                    }while(currentPhase >0);
                    if (Actor.findChar(targetPos) != null){
                        int damage = Random.NormalIntRange( 5 + wandLevel, 15 + 3*wandLevel );
                        Actor.findChar(targetPos).damage(Math.round(damage*(totalPhases +1)), sov);
                    }//每次循环都排除内圈，所以单独处理最中心的伤害
                    PathFinder.buildDistanceMap(targetPos, BArray.not( Dungeon.level.solid, null ), totalPhases);
                    for (int i = 0; i < PathFinder.distance.length; i++) {
                        if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                            Char ch = Actor.findChar(i);
                            if (ch != null){
                                affected.add(ch);
                            }
                        }
                    }
                    for ( Char ch : affected ){
                        if (Dungeon.hero.hasTalent(Talent.ARCANE_SNIPE)) {
                            int dur = 5*Dungeon.hero.pointsInTalent(Talent.ARCANE_SNIPE);
                            Buff.append(Dungeon.hero, TalismanOfForesight.CharAwareness.class, dur).charID = ch.id();
                        }
                        if (ch != Dungeon.hero &&
                                Dungeon.hero.subClassSet.contains(HeroSubClass.WARLOCK) &&
                                Random.Float() > (Math.pow(0.95f - Dungeon.hero.pointsInTalent(Talent.EMOTION) / 15f, (wandLevel* totalPhases)+1) - 0.07f)){//change from budding
                            SoulMark.prolong(ch, SoulMark.class, SoulMark.DURATION + wandLevel + (Dungeon.hero.pointsInTalent(Talent.LORD) * 2));
                        }
                        if (ch.isAlive()) {
                            Buff.affect(ch, Burning.class).reignite(ch);
                            switch (currentPhase) {
                                case 1:
                                    break; //no effects
                                case 2:
                                    Buff.affect(ch, Cripple.class, 4f);
                                    break;
                                case 3:
                                    Buff.affect(ch, Cripple.class, 7f);
                                    Buff.affect(ch, Paralysis.class, 3f);
                                    break;
                                case 4:
                                    Buff.affect(ch, Cripple.class, 10f);
                                    Buff.affect(ch, Paralysis.class, 6f);
                                    break;
                            }
                        }
                    }
                    //特效部分
                    currentPhase = totalPhases;
                    do{
                        PathFinder.buildDistanceMap(targetPos, BArray.not( Dungeon.level.solid, null ), currentPhase);
                        for (int i = 0; i < PathFinder.distance.length; i++) {
                            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                                CellEmitter.get(i).burst(BlastParticle.FACTORY, (int)(Math.pow((totalPhases - currentPhase +1),2)));
                            }
                        }
                        currentPhase--;
                    }
                    while (currentPhase >0);
                    switch(totalPhases){
                        case 0:
                            break;
                        case 1: default:
                            CellEmitter.get(targetPos).burst(BlastParticle.FACTORY, 10);
                            Sample.INSTANCE.play(Assets.Sounds.BLAST);
                            break;
                        case 2:
                            Camera.main.shake(1, 0.7f);
                            GameScene.flash(0x20FF0000);
                            CellEmitter.get(targetPos).burst(BlastParticle.FACTORY, 12);
                            Sample.INSTANCE.play(Assets.Sounds.BLAST);
                            Sample.INSTANCE.play(Assets.Sounds.ROCKS);
                            break;
                        case 3:
                            Wave.blast(targetPos);
                            Camera.main.shake(2, 0.9f);
                            GameScene.flash(0x60FF0000);
                            PathFinder.buildDistanceMap(targetPos, BArray.not( Dungeon.level.solid, null ), 1 );
                            for (int i = 0; i < PathFinder.distance.length; i++) {
                                if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                                    CellEmitter.get(i).burst(SmokeParticle.FACTORY, 5);
                                }
                            }
                            CellEmitter.get(targetPos).burst(SmokeParticle.FACTORY, 5);
                            CellEmitter.get(targetPos).burst(BlastParticle.FACTORY, 15);
                            Sample.INSTANCE.play(Assets.Sounds.BLAST);
                            Sample.INSTANCE.play(Assets.Sounds.ROCKS);
                            Sample.INSTANCE.play(Assets.Sounds.HIT_STRIKE);
                            break;
                        case 4:
                            Wave.blast(targetPos);
                            Camera.main.shake(3, 1.2f);
                            GameScene.flash(0xA0FF0000);
                            PathFinder.buildDistanceMap(targetPos, BArray.not( Dungeon.level.solid, null ), 2 );
                            for (int i = 0; i < PathFinder.distance.length; i++) {
                                if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                                    CellEmitter.get(i).burst(SmokeParticle.FACTORY, 5);
                                }
                            }
                            CellEmitter.get(targetPos).burst(SmokeParticle.FACTORY, 10);
                            CellEmitter.get(targetPos).burst(BlastParticle.FACTORY, 15);
                            Sample.INSTANCE.play(Assets.Sounds.BLAST);
                            Sample.INSTANCE.play(Assets.Sounds.ROCKS);
                            Sample.INSTANCE.play(Assets.Sounds.HIT_STRIKE);
                            break;
                    }
                    next();
                    detach();
                    return false;
                }else {
                    PathFinder.buildDistanceMap(targetPos, BArray.not(Dungeon.level.solid, null), (currentPhase - remainingPhases)+1);
                    for (int i = 0; i < PathFinder.distance.length; i++) {
                        if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                            curUser.sprite.parent.addToBack(new TargetedCell(i, 0xFF0000));
                        }
                    }
                }
            }
            spend( TICK );
            return true;
        }

    }

    public static class Wave extends Image {

        private static final float TIME_TO_FADE = 0.5f;

        private float time;

        public Wave(){
            super(Effects.get(Effects.Type.RIPPLE_ORIGIN));
            origin.set(width / 2, height / 2);
        }

        public void reset(int pos) {
            revive();

            x = (pos % Dungeon.level.width()) * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - width) / 2;
            y = (pos / Dungeon.level.width()) * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - height) / 2;

            time = TIME_TO_FADE;
        }

        @Override
        public void update() {
            super.update();

            if ((time -= Game.elapsed) <= 0) {
                kill();
            } else {
                //float p = -(float)(Math.pow(time,2))+1;
                //alpha(p);
                float q = time / TIME_TO_FADE;
                alpha(q);
                scale.y = scale.x = (float)(Math.pow((1-q)*4,2));
            }
        }

        public static void blast(int pos) {
            Group parent = Dungeon.hero.sprite.parent;
            Wave b = (Wave) parent.recycle(Wave.class);
            parent.bringToFront(b);
            b.reset(pos);
        }

    }
}
