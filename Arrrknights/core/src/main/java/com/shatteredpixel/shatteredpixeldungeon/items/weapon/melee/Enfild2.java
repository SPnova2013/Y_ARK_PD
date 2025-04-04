package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import static com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent.PROFICIENCY;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Gunaccessories.Accessories;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

public class Enfild2 extends MeleeWeapon {
    {
        image = ItemSpriteSheet.ANDREANA;
        hitSound = Assets.Sounds.HIT_SNIPER;
        hitSoundPitch = 0.9f;

        tier = 3;
        DLY = 3f; //0.33x speed
        RCH = 50;    //extra reach

        //also cannot surprise attack, see Hero.canSurpriseAttack
    }

//FIXME 得心应手在两把狙上没有做到随着天赋点的增加变成2-2
    @Override
    public int min(int lvl) {
       return 8+lvl+ Maccessories +
               ((Dungeon.hero.hasTalent(Talent.PROFICIENCY)&& Maccessories > 2) ? Maccessories : 0);
    }

    @Override
    public int max(int lvl) {
        return  8*(tier+1) +    // 48 + 15
                lvl*(tier+10) +
                 Maccessories +
                ((Dungeon.hero.hasTalent(Talent.PROFICIENCY)&& Maccessories > 1) ? Maccessories : 0);   //scaling unchanged
    }

    @Override
    public int value() { return super.value() + 100; }

    @Override
    public boolean isUpgradable() { return true; }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (charge >= chargeCap) {
            damage *= 1.3f;
            charge = 0;
        }
        else SPCharge(20);
        return super.proc(attacker, defender, damage);
    }

    @Override
    public int STRReq(int lvl) {
        return STRReq(tier+2, lvl)+Maccessories; //20 base strength req, up from 18
    }
    @Override
    public float speedFactor( Char owner ) {
        float delay = super.speedFactor( owner );
        if(Dungeon.hero.hasTalent(PROFICIENCY)){
            delay *= 1/(1.1f+Math.pow(Dungeon.hero.pointsInTalent(PROFICIENCY),2)*0.1f);
        }
        return delay;
    }

    @Override
    public String status() {

        //if the artifact isn't IDed, or is cursed, don't display anything
        if (!isIdentified() || cursed) {
            return null;
        }
        //display as percent
        if (chargeCap == 100)
            return Messages.format("%d%%", charge);


        //otherwise, if there's no charge, return null.
        return null;
    }
}
