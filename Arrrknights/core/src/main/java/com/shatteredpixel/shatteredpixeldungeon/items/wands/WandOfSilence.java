package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SP.StaffOfSnowsant;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class WandOfSilence extends Wand {

    {
        image = ItemSpriteSheet.WAND_SNOWSANT;
    }

    @Override
    public String upgradeStat1(int level) {
        return Integer.toString(3 + level*2);
    }

    @Override
    protected void onZap( Ballistica bolt ) {

        Char ch = Actor.findChar( bolt.collisionPos );
        if (ch != null) {

            processSoulMark(ch, chargesPerCast());
            Buff.affect(ch, Silence.class, 3f+buffedLvl()*2);
            Sample.INSTANCE.play( Assets.Sounds.HIT_MAGIC, 1, Random.Float(0.65f, 0.75f) );

            ch.sprite.burst(0xFFFFFFFF, buffedLvl() / 2 + 2);

        } else {
            Dungeon.level.pressCell(bolt.collisionPos);
        }
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        if (defender.buff(Silence.class) != null)
        {
            int dmg = damage / 5;
            defender.damage(dmg, curUser);
        }

    }

    public int initialCharges() {
        return 2;
    }
    @Override
    public Class upToStaff() {
        return StaffOfSnowsant.class;
    }
}
