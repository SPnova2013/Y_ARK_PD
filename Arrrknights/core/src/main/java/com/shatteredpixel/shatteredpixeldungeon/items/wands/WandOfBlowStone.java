package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class WandOfBlowStone extends DamageWand{
    {
        image = ItemSpriteSheet.WAND_BLOW_STONE;
    }
    @Override
    public int min(int lvl) {
        return 2+2*lvl;
    }

    @Override
    public int max(int lvl) {
        return 5+5*lvl;
    }

    @Override
    protected void onZap(Ballistica bolt) {
        Char ch = Actor.findChar( bolt.collisionPos );
        if (ch != null) {
            processSoulMark(ch, chargesPerCast());
            int damage = damageRoll() - ch.drRoll();
            if(damage>0) ch.damage(damage, Dungeon.hero);
            Sample.INSTANCE.play( Assets.Sounds.HIT_MAGIC, 1, Random.Float(0.87f, 1.15f) );
            ch.sprite.burst(0xFFFFFFFF, buffedLvl() + 5);
        } else {
            Dungeon.level.pressCell(bolt.collisionPos);
        }
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        defender.damage(damage/3, Dungeon.hero);
    }
    public int initialCharges() {
        return 1;
    }
}
