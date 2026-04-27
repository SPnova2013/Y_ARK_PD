package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;

public abstract class PlantBuff extends Buff {
    @Override
    public boolean act() {
        spend(TICK);
        return true;
    }
}