package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.level;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.FigureEightBuilder;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GuerrillaHeraldSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ScoutSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class GuerrillaHerald extends Mob{
    {
        spriteClass = GuerrillaHeraldSprite.class;

        HP = HT = 1;
        drMax = 0;
        drMin = 0;
        defenseSkill = 12;

        EXP = 7;
        maxLvl = 14;

        loot = new ScrollOfRage();
        lootChance = 0.1f; //see createloot

        immunities.add(Silence.class);
    }
    @Override
    public void die(Object cause) {
        super.die(cause);
        level.heraldAlive = false;
        for (Mob mob : level.mobs.toArray(new Mob[0])) {
            if (mob instanceof GuerrillaHerald) {
                level.heraldAlive = true;
                break;
            }
        }
        if(!level.heraldAlive){
            for (Mob mob : level.mobs.toArray(new Mob[0])) {
                mob.sprite.remove(CharSprite.State.HIKARI);
            }
            GLog.h(Messages.get(this, "all_dead"));
        }
    }
}
