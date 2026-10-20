package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ApocalypseAspirant extends MeleeWeapon{

    {
        image = ItemSpriteSheet.APOCALYPSE_ASPIRANT;
        hitSound = Assets.Sounds.HIT_STRONG;
        hitSoundPitch = 0.9f;

        tier = 5;
    }
}
