package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class KingOfNothing extends MeleeWeapon{
    {
        image = ItemSpriteSheet.KING_OF_NOTHING;
        hitSound = Assets.Sounds.HIT_STRONG;
        hitSoundPitch = 0.9f;

        tier = 4;
    }
}
