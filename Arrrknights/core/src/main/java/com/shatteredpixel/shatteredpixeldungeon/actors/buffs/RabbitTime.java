package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class RabbitTime extends Buff{
    {
        type = buffType.POSITIVE;
        announced = true;
    }

    private float left;
    private float totaltime = 0;
    ArrayList<Integer> presses = new ArrayList<>();

    @Override
    public int icon() {
        return BuffIndicator.RABBIT_TIME;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0, (5 - left) / 5);
    }

    public void add(float time){
        left += time;
        totaltime += time;
    }

    public void set(float time) { left = time;}
    public void erasePresses(){presses.clear();}


    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns(left));
    }

    public void processTime(float time){

        left -= time;

        //use 1/1,000 to account for rounding errors
        if (left < -0.001f){
            detach();
        }

    }

    public void setDelayedPress(int cell){
        if (!presses.contains(cell))
            presses.add(cell);
    }

    private void triggerPresses(){
        for (int cell : presses)
            Dungeon.level.pressCell(cell);

        presses = new ArrayList<>();
    }

    @Override
    public void detach(){
        if(Dungeon.hero.pointsInTalent(Talent.ACCURATE_HIT)==2){
            Buff.affect(Dungeon.hero,Bless.class,totaltime);
        }
        super.detach();
        triggerPresses();
        target.next();
    }

    @Override
    public void fx(boolean on) {
        Emitter.freezeEmitters = on;
        if (on){
            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                if (mob.sprite != null) mob.sprite.add(CharSprite.State.PARALYSED);
            }
        } else {
            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                if (mob.paralysed <= 0) mob.sprite.remove(CharSprite.State.PARALYSED);
            }
        }
    }

    private static final String PRESSES = "presses";
    private static final String LEFT = "left";
    private static final String TOTALTIME = "totaltime";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);

        int[] values = new int[presses.size()];
        for (int i = 0; i < values.length; i ++)
            values[i] = presses.get(i);
        bundle.put( PRESSES , values );

        bundle.put( LEFT, left);
        bundle.put( TOTALTIME, totaltime);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);

        int[] values = bundle.getIntArray( PRESSES );
        for (int value : values)
            presses.add(value);

        left = bundle.getFloat(LEFT);
        totaltime = bundle.getFloat(TOTALTIME);
    }
}
