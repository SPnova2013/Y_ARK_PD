package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class ShieldedGuardSprite extends MobSprite{
    public ShieldedGuardSprite() {
        super();

        texture( Assets.Sprites.SHIELDED_GUARD );

        TextureFilm frames = new TextureFilm( texture, 36, 34 );

        idle = new Animation( 2, true );
        idle.frames( frames, 0 );

        run = new Animation( 12, true );
        run.frames( frames, 0, 1, 2, 3, 4, 5, 6, 7 );

        attack = new Animation( 12, false );
        attack.frames( frames, 8, 9, 10, 11, 12, 13 );

        die = new Animation( 10, false );
        die.frames( frames, 14, 15, 16, 17, 18, 19, 20 );

        play( idle );
    }
}
