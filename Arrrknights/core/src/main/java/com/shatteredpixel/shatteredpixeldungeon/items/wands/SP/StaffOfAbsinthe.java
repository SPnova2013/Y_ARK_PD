package com.shatteredpixel.shatteredpixeldungeon.items.wands.SP;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfAmplified;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.DamageWand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SSP.StaffOfConcept;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class StaffOfAbsinthe extends DamageWand {
    private static ItemSprite.Glowing COL = new ItemSprite.Glowing( 0x191970 );
    {
        image = ItemSpriteSheet.WAND_MAGIC_MISSILE;
    }

    public int min(int lvl){ return 6+lvl*3; }

    public int max(int lvl){
        return 25+7*lvl+ RingOfAmplified.DamageBonus(Dungeon.hero) * 7;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return COL;
    }

    @Override
    protected void onZap( Ballistica bolt ) {

        Char ch = Actor.findChar( bolt.collisionPos );
        if (ch != null) {

            processSoulMark(ch, chargesPerCast());
            if (ch.HP <= ch.HT / 2) {
                ch.damage(damageRoll(), this);
                Sample.INSTANCE.play( Assets.Sounds.HIT_MAGIC, 1, Random.Float(0.87f, 1.15f) );
                ch.sprite.burst(0xFFFFFFFF, buffedLvl() / 2 + 2);
            }

        } else {
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

    }

    public int initialCharges() {
        return 3;
    }
    @Override
    public Class upToStaff() {
        return StaffOfConcept.class;
    }
}
