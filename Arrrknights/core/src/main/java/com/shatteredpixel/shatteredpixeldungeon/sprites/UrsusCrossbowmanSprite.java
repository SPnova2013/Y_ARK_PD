package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class UrsusCrossbowmanSprite extends MobSprite{
    public UrsusCrossbowmanSprite() {
        super();

        texture( Assets.Sprites.URSUS_CROSSBOWMAN );

        TextureFilm frames = new TextureFilm( texture, 32, 32 );

        idle = new Animation( 2, true );
        idle.frames( frames, 8, 9, 10 );

        run = new Animation( 12, true );
        run.frames( frames, 11, 12, 13, 14, 15, 16 );

        attack = new Animation( 12, false );
        attack.frames( frames, 0, 1, 2, 3, 4, 5, 6, 7 );

        die = new Animation( 10, false );
        die.frames( frames, 17, 18, 19, 20 );

        play( idle );
    }
}
