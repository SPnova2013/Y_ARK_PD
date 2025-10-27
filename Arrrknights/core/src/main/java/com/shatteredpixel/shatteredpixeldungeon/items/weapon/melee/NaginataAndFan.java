package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hallucination;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Oblivion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class NaginataAndFan extends MeleeWeapon implements MeleeWeapon.BlockingWeapon {
    {
        image = ItemSpriteSheet.NAGINATA_AND_FAN;
        hitSound = Assets.Sounds.HIT_SPEAR;
        hitSoundPitch = 1.18f;

        tier = 4;
        RCH = 2;
    }
    @Override
    public int max(int lvl) {
        return  5*(tier) +   //20 + 4
                lvl*(tier);
    }
    @Override
    public int defenseFactor( Char owner ) {
        return DRMax();
    }

    public int DRMax(){
        return DRMax(buffedLvl());
    }

    public int DRMax(int lvl){
        return 2 + lvl;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        switch(Random.Int(11)){
            case 0:
                Buff.prolong(defender, Amok.class, 1);
                break;
            case 1:
                Buff.prolong(defender, Charm.class, 1);
                break;
            case 2:
                Buff.prolong(defender, Hex.class, 1);
                break;
            case 3:
                Buff.prolong(defender, Paralysis.class, 1);
                break;
            case 4:
                Buff.prolong(defender, Slow.class, 1);
                break;
            case 5:
                Buff.prolong(defender, Vulnerable.class, 1);
                break;
            case 6:
                Buff.prolong(defender, Terror.class, 1);
                break;
            case 7:
                Buff.prolong(defender, Vertigo.class, 1);
                break;
            case 8:
                Buff.prolong(defender, Weakness.class, 1);
                break;
            case 9:
                Buff.prolong(defender, Silence.class, 1);
                break;
            case 10:
                Buff.affect(defender, Hallucination.class).set(1);
                break;
            case 11:
                Buff.affect(defender, Oblivion.class, 1);
                break;
        }
        return super.proc(attacker, defender, damage);
    }
    public String statsInfo(){
        if (isIdentified()){
            return Messages.get(this, "stats_desc", 2+buffedLvl());
        } else {
            return Messages.get(this, "typical_stats_desc", 2);
        }
    }
}
