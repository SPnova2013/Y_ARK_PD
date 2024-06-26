package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.AcidSlug_A;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Agent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM100;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Ergate;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ExplodSlug_N;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ExplodeSlug_A;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Eye;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Shaman;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.SiestaBoss;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.TiacauhRitualist;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.TiacauhShaman;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Warlock;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

import java.util.HashSet;

public class Silence extends FlavourBuff {

    {
        type = buffType.NEGATIVE;
        announced = true;
    }

    @Override
    public int icon() {
        return BuffIndicator.SILENCE;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String heroMessage() {
        return Messages.get(this, "heromsg");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns());
    }

    public static final HashSet<Class> SILENCEABLE = new HashSet<>();
    static {
        SILENCEABLE.add(AcidSlug_A.class);
        SILENCEABLE.add(Agent.class);
        SILENCEABLE.add(SiestaBoss.BossAgent.class);
        SILENCEABLE.add(Bat.class);
        SILENCEABLE.add(DM100.class);
        SILENCEABLE.add(Ergate.class);
        SILENCEABLE.add(ExplodeSlug_A.class);
        SILENCEABLE.add(ExplodSlug_N.class);
        SILENCEABLE.add(Eye.class);
        SILENCEABLE.add(Shaman.class);
        SILENCEABLE.add(TiacauhRitualist.class);
        SILENCEABLE.add(TiacauhShaman.class);
        SILENCEABLE.add(Warlock.class);
    }
}
