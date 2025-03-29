package com.shatteredpixel.shatteredpixeldungeon.items.ror2items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SnowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HandclapSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class FrostRelic extends ROR2item{
    {
        tier = 3;
        image = ItemSpriteSheet.FROST_RELIC;
    }
    public ArrayList<Emitter> frostRelicEmitters = new ArrayList<>();
    public int frostRelicRange = 0;
    private int rangeReduceCount = 0;

    @Override
    public void uponKill(Char attacker, Char defender, int damage ) {
        rangeReduceCount=0;
        if(frostRelicRange<3)frostRelicRange++;
    }
    @Override
    protected ROR2itemBuff passiveBuff() {
        return new FrostRelicBuff();
    }

    public class FrostRelicBuff extends ROR2itemBuff{
        @Override
        public boolean act() {
            if(frostRelicRange>0)rangeReduceCount++;
            if(rangeReduceCount>=10){
                rangeReduceCount = 0;
                if(frostRelicRange>0)frostRelicRange--;
            }

            PathFinder.buildDistanceMap( Dungeon.hero.pos, BArray.not( Dungeon.level.solid, null ), frostRelicRange );
            for (int i = 0; i < PathFinder.distance.length; i++) {
                if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                    Emitter e = CellEmitter.get(i);
                    e.pour(SnowParticle.FACTORY, 0.01f);
                    frostRelicEmitters.add(e);

                    Char ch = Actor.findChar(i);
                    if (ch != null && ch != Dungeon.hero){
                        ch.damage((int)Math.ceil(Dungeon.depth/2f), this);
                        if (!ch.isImmune(Freezing.class)) {
                            Buff.affect(ch, Chill.class, Dungeon.level.water[i] ? 5f : 3f);
                        }
                    }
                }
            }

            spend(TICK);

            return true;
        }

        {
            immunities.add(Chill.class);
            immunities.add(Frost.class);
        }
        private static final String FROSTRELICRANGE	= "frostRelicRange";
        private static final String RANGEREDUCECOUNT	= "rangeReduceCount";

        @Override
        public void storeInBundle( Bundle bundle ) {
            super.storeInBundle( bundle );
            bundle.put( FROSTRELICRANGE, frostRelicRange );
            bundle.put( RANGEREDUCECOUNT, rangeReduceCount );
        }

        @Override
        public void restoreFromBundle( Bundle bundle ) {
            super.restoreFromBundle( bundle );
            frostRelicRange = bundle.getInt( FROSTRELICRANGE );
            rangeReduceCount = bundle.getInt( RANGEREDUCECOUNT );
        }
    }
}
