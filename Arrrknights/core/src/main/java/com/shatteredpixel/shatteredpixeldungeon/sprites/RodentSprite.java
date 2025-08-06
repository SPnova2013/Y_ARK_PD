package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class RodentSprite extends MobSprite{
    public RodentSprite() {
        super();

        texture( Assets.Sprites.RODENT );

        TextureFilm frames = new TextureFilm( texture, 36, 36 );

        idle = new Animation( 2, true );
        idle.frames( frames, 0 );

        run = new Animation( 12, true );
        run.frames( frames, 0, 1, 2, 3, 4, 5 );

        attack = new Animation( 12, false );
        attack.frames( frames, 6, 7, 8, 9, 10, 11 );

        die = new Animation( 10, false );
        die.frames( frames, 12, 13, 14, 15, 16, 17, 18);

        play( idle );
    }
}
