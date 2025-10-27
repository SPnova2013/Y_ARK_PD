package com.shatteredpixel.shatteredpixeldungeon.items.wands.SSP;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class StaffOfConcept extends Wand {
    {
        image = ItemSpriteSheet.WAND_CONCEPT;
    }

    @Override
    protected void onZap(Ballistica bolt) {
        Char ch = Actor.findChar( bolt.collisionPos );
        if (ch != null) {
            processSoulMark(ch, this.level()+1);
            Sample.INSTANCE.play( Assets.Sounds.HIT_MAGIC, 1, Random.Float(0.87f, 1.15f) );
            ch.sprite.burst(0xFFFFFFFF, buffedLvl() / 2 + 2);
            //apply the magic charge buff if we have another wand in inventory of a lower level, or already have the buff
            for (Wand.Charger wandCharger : curUser.buffs(Wand.Charger.class)){
                if (wandCharger.wand().buffedLvl() < buffedLvl() || curUser.buff(WandOfMagicMissile.MagicCharge.class) != null){
                    Buff.prolong(curUser, WandOfMagicMissile.MagicCharge.class, WandOfMagicMissile.MagicCharge.DURATION).setLevel(buffedLvl());
                    break;
                }
            }
        }else {
            Dungeon.level.pressCell(bolt.collisionPos);
        }
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        SpellSprite.show(attacker, SpellSprite.CHARGE);
        for (Wand.Charger c : attacker.buffs(Wand.Charger.class)){
            if (c.wand() != this){
                c.gainCharge(0.5f);
            }
        }
        ArrayList<Wand> wands = Dungeon.hero.belongings.getAllItems(Wand.class);
        //Random.shuffle(wands);
        for(Wand w : wands) {
            if(w!=this){
                w.onHit(staff,attacker,defender,damage);
            }
        }
    }
    public int initialCharges() {
        return 4;
    }
    @Override
    public void updateLevel() {
        maxCharges = initialCharges() + level();
        curCharges = Math.min( curCharges, maxCharges );
    }
}
