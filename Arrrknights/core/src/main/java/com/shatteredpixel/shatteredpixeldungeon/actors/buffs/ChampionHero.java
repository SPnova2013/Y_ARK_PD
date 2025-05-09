package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public abstract class ChampionHero extends Buff{

    {
        type = buffType.POSITIVE;
    }

    public int color;
    protected float left;
    private static int R2ShieldHP = -1;
    private int particalR2SHP = 0;
    private static final String LEFT	= "left";
    @Override
    public boolean act() {
        spend(TICK);
        left -= TICK;
        if (left <= 0){
            detach();
        }
        if(R2ShieldHP != -1){
            particalR2SHP++;
            if(particalR2SHP == 10) R2ShieldHP++;
        }
        return true;
    }
    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( LEFT, left );
    }
    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        left = bundle.getFloat( LEFT );
    }
    public void set( float duration ) {
        this.left = duration;
    }
    public static void getElite(Hero hero,Class<ChampionHero> el,float dur){
        Buff.affect(hero, el).set(dur);
    }
    public static void getEliteByEnemy(Hero hero,ChampionEnemy el,float dur){
        ChampionHero ch;
        int R2ShieldToBe = 0;
        if(el instanceof ChampionEnemy.Blazing) ch = Reflection.newInstance(ChampionHero.Blazing.class);
        else if (el instanceof ChampionEnemy.Projecting) ch = Reflection.newInstance(ChampionHero.Projecting.class);
        else if (el instanceof ChampionEnemy.AntiMagic) ch = Reflection.newInstance(ChampionHero.AntiMagic.class);
        else if (el instanceof ChampionEnemy.Giant) ch = Reflection.newInstance(ChampionHero.Giant.class);
        else if (el instanceof ChampionEnemy.Blessed) ch = Reflection.newInstance(ChampionHero.Blessed.class);
        else if (el instanceof ChampionEnemy.Growing) ch = Reflection.newInstance(ChampionHero.Growing.class);
        else if (el instanceof ChampionEnemy.R2Blazing) ch = Reflection.newInstance(ChampionHero.R2Blazing.class);
        else if (el instanceof ChampionEnemy.R2Overloading) ch = Reflection.newInstance(ChampionHero.R2Overloading.class);
        else if (el instanceof ChampionEnemy.R2Glacial) ch = Reflection.newInstance(ChampionHero.R2Glacial.class);
        else if (el instanceof ChampionEnemy.R2Malachite) ch = Reflection.newInstance(ChampionHero.R2Malachite.class);
        else if (el instanceof ChampionEnemy.R2Celestine) ch = Reflection.newInstance(ChampionHero.R2Celestine.class);
        else if (el instanceof ChampionEnemy.R2Perfected) ch = Reflection.newInstance(ChampionHero.R2Perfected.class);
        else if (el instanceof ChampionEnemy.R2Mending) ch = Reflection.newInstance(ChampionHero.R2Mending.class);
        else ch = Reflection.newInstance(Blazing.class);

        if(ch instanceof ChampionHero.R2Overloading && hero.buff(ChampionHero.R2Overloading.class) == null) R2ShieldToBe = hero.HT/2;
        if(ch instanceof ChampionHero.R2Perfected && hero.buff(ChampionHero.R2Perfected.class) == null) R2ShieldToBe = hero.HT;
        if(R2ShieldToBe > 0){
            Buff.affect(hero, ROR2Shield.class).addMaxShield(R2ShieldToBe);
            if(R2ShieldHP == -1) R2ShieldHP = hero.HP;
        }

        Buff.affect(hero, ch.getClass()).set(dur);
    }
    @Override
    public int icon() {
        return BuffIndicator.CORRUPT;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(color);
    }

    @Override
    public void fx(boolean on) {
        if (on) target.sprite.aura( color );
        else target.sprite.clearAura();
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc");
    }
    public void onAttackProc(Char enemy, int damage ){

    }

    public boolean canAttackWithExtraReach( Char enemy ){
        return false;
    }

    public float meleeDamageFactor(){
        return 1f;
    }

    public float damageTakenFactor(){
        return 1f;
    }

    public float evasionAndAccuracyFactor(){
        return 1f;
    }

    {
        immunities.add(Corruption.class);
    }

    public static class Blazing extends ChampionHero {

        {
            color = 0xFF8800;
        }

        @Override
        public void onAttackProc(Char enemy, int damage) {
            Buff.affect(enemy, Burning.class).reignite(enemy);
        }

        @Override
        public void detach() {
            /*for (int i : PathFinder.NEIGHBOURS9){
                if (!Dungeon.level.solid[target.pos+i]){
                    GameScene.add(Blob.seed(target.pos+i, 2, Fire.class));
                }
            }*/
            super.detach();
        }

        @Override
        public float meleeDamageFactor() {
            return 1.25f;
        }

        {
            immunities.add(Burning.class);
        }
    }

    public static class Projecting extends ChampionHero {

        {
            color = 0x8800FF;
        }

        @Override
        public float meleeDamageFactor() {
            return 1.25f;
        }

        @Override
        public boolean canAttackWithExtraReach( Char enemy ) {
            return target.fieldOfView[enemy.pos]; //if it can see it, it can attack it.
        }
    }

    public static class AntiMagic extends ChampionHero {

        {
            color = 0x00FF00;
        }

        @Override
        public float damageTakenFactor() {
            return 0.75f;
        }

        {
            immunities.addAll(com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic.RESISTS);
        }

    }

    //Also makes target large, see Char.properties()
    public static class Giant extends ChampionHero {

        {
            color = 0x0088FF;
        }

        @Override
        public float damageTakenFactor() {
            return 0.25f;
        }

        @Override
        public boolean canAttackWithExtraReach(Char enemy) {
            //attack range of 2
            return target.fieldOfView[enemy.pos] && Dungeon.level.distance(target.pos, enemy.pos) <= 2;
        }
    }

    public static class Blessed extends ChampionHero {

        {
            color = 0xFFFF00;
        }

        @Override
        public float evasionAndAccuracyFactor() {
            return 3f;
        }
    }

    public static class Growing extends ChampionHero {

        {
            color = 0xFF0000;
        }

        private float multiplier = 1.19f;

        @Override
        public boolean act() {
            multiplier += 0.003f;
            return super.act();
        }

        @Override
        public float meleeDamageFactor() {
            return multiplier;
        }

        @Override
        public float damageTakenFactor() {
            return 1f/multiplier;
        }

        @Override
        public float evasionAndAccuracyFactor() {
            return multiplier;
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", (int)(100*(multiplier-1)), (int)(100*(1 - 1f/multiplier)));
        }

        private static final String MULTIPLIER = "multiplier";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(MULTIPLIER, multiplier);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            multiplier = bundle.getFloat(MULTIPLIER);
        }
    }

    public static class R2Blazing extends ChampionHero {
        {
            color = 0xFF6600;
        }
        @Override
        public void onAttackProc(Char enemy, int damage) {
            Buff.affect(enemy, Burning.class).reignite(enemy);
        }
        @Override
        public boolean act() {
            GameScene.add(Blob.seed(target.pos, 3, Fire.class));
            return super.act();
        }
        {
            immunities.add(Burning.class);
        }
    }
    public static class R2Overloading extends ChampionHero {
        {
            color = 0x55AAFF;
        }
        @Override
        public void onAttackProc(Char enemy, int damage) {
            Buff.append(enemy, BlazingBomb.class).setup(enemy, damage, Dungeon.depth);
        }
        @Override
        public boolean act() {
            target.HP = Math.min(target.HT/2, target.HP);
            return super.act();
        }
        @Override
        public void detach() {
            if(target.buff(ChampionHero.R2Perfected.class)!=null){
                Buff.affect(target, ROR2Shield.class).decMaxShield(target.HT/2);
                target.HP = Math.min(target.HT,R2ShieldHP);
            }else{
                Buff.detach(target, ROR2Shield.class);
                target.HP = Math.min(target.HT,R2ShieldHP);
                R2ShieldHP = -1;
            }
            super.detach();
        }
        public static class BlazingBomb extends Buff{
            private Char victim;
            private int explodeDepth;
            private int left;
            private int damage;

            public void setup( Char victim,int damage, int explodeDepth){
                this.victim = victim;
                this.explodeDepth = explodeDepth;
                this.damage = damage;
                left = 3;
            }
            @Override
            public boolean act() {
                if (explodeDepth == Dungeon.depth){
                    left--;
                    if (left <= 0){
                        WandOfLightning wol = new WandOfLightning();
                        victim.damage(damage,wol);
                        if (victim==Dungeon.hero && !Dungeon.hero.isAlive()) {
                            Dungeon.fail( getClass() );
                            GLog.n( Messages.get(this, "blazingbomb_kill") );
                        }
                        next();
                        detach();
                        return false;
                    }
                }
                spend( TICK );
                return true;
            }
        }
    }
    public static class R2Glacial extends ChampionHero {
        {
            color = 0xCCEEFF;
        }
        @Override
        public void onAttackProc(Char enemy, int damage) {
            Buff.affect(enemy, Chill.class, 2f);
        }
        @Override
        public void detach() {
            /*Bomb bomb = new GlacialBomb();
            Actor.addDelayed(bomb.fuse = new Bomb.Fuse().ignite(bomb), 2);
            Dungeon.level.drop(bomb, target.pos);*/
            super.detach();
        }
        public static class GlacialBomb extends Bomb {
            {
                image = ItemSpriteSheet.FROST_BOMB;
            }
            @Override
            public void explode(int cell) {
                PathFinder.buildDistanceMap( cell, BArray.not( Dungeon.level.solid, null ), 2 );
                for (int i = 0; i < PathFinder.distance.length; i++) {
                    if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                        Char ch = Actor.findChar(i);
                        if (ch != null){
                            ch.damage(Dungeon.depth + Statistics.victoryLapRounds*25, this);
                            Buff.affect(ch, Frost.class, 2f);
                        }
                    }
                }
            }
            @Override
            public boolean doPickUp(Hero hero) {
                return false;
            }
            @Override
            public String desc() {
                return Messages.get(this, "desc");
            }
        }
    }
    public static class R2Malachite extends ChampionHero {
        {
            color = 0x005500;
        }
        @Override
        public void onAttackProc(Char enemy, int damage) {
            Buff.affect(enemy, DisableHealing.class).set(enemy.HP, DisableHealing.DURATION);
        }
    }
    public static class R2Celestine extends ChampionHero {
        {
            color = 0x006688;
        }
        @Override
        public void onAttackProc(Char enemy, int damage) {
            Buff.affect(enemy, Cripple.class, 1f);
        }
        @Override
        public boolean act() {
            ArrayList<Char> affected = new ArrayList<>();
            for (Mob mob : Dungeon.level.mobs) {
                if (Dungeon.level.distance(target.pos, mob.pos) <= 3
                        && mob.buff(R2Celestine.class) == null
                        && mob.alignment == Char.Alignment.ALLY) {
                    affected.add(mob);
                }
            }
            for ( Char ch : affected ){
                Buff.prolong(ch, Vanish.class, 2f);
            }
            return super.act();
        }
    }
    public static class R2Perfected extends ChampionHero {
        {
            color = 0x7777DD;
        }
        @Override
        public boolean act() {
            target.HP = Math.min(1, target.HP);
            return super.act();
        }
        @Override
        public void onAttackProc(Char enemy, int damage) {
            Buff.affect(enemy, Cripple.class, 1f);
            Buff.affect(enemy, Vulnerable.class, 1f);
        }
        @Override
        public void detach() {
            if(target.buff(ChampionHero.R2Overloading.class)!=null){
                Buff.affect(target, ROR2Shield.class).decMaxShield(target.HT);
            }else{
                Buff.detach(target, ROR2Shield.class);
                target.HP = Math.min(target.HT,R2ShieldHP);
                R2ShieldHP = -1;
            }
            super.detach();
        }
    }
    public static class R2Mending extends ChampionHero {
        {
            color = 0x99FF00;
        }
        @Override
        public boolean act() {
            ArrayList<Char> affected = new ArrayList<>();
            for (Mob mob : Dungeon.level.mobs) {
                if (Dungeon.level.distance(target.pos, mob.pos) <= 2
                        && mob.buff(R2Mending.class) == null
                        && mob.alignment == Char.Alignment.ALLY) {
                    affected.add(mob);
                }
            }
            for ( Char ch : affected ){
                ch.HP++;
                ch.HP=Math.min(ch.HP, ch.HT);
                ch.sprite.emitter().start( Speck.factory( Speck.HEALING ), 0.4f, 4 );
            }
            return super.act();
        }
        @Override
        public void detach() {
            /*Bomb bomb = new MendingBomb();
            Actor.addDelayed(bomb.fuse = new Bomb.Fuse().ignite(bomb), 2);
            Dungeon.level.drop(bomb, target.pos);*/
            super.detach();
        }
        public static class MendingBomb extends Bomb {
            {
                image = ItemSpriteSheet.REGROWTH_BOMB;
            }
            @Override
            public void explode(int cell) {
                PathFinder.buildDistanceMap( cell, BArray.not( Dungeon.level.solid, null ), 2 );
                for (int i = 0; i < PathFinder.distance.length; i++) {
                    if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                        Char ch = Actor.findChar(i);
                        if (ch != null){
                            ch.HP+=ch.HT/5;
                            ch.HP=Math.min(ch.HP,ch.HT);
                            ch.sprite.emitter().start( Speck.factory( Speck.HEALING ), 0.4f, 4 );
                        }
                    }
                }
            }
            @Override
            public boolean doPickUp(Hero hero) {
                return false;
            }
            @Override
            public String desc() {
                return Messages.get(this, "desc");
            }
        }
    }
}
