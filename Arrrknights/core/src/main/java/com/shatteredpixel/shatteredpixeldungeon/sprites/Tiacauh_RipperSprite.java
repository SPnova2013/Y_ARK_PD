package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class Tiacauh_RipperSprite extends MobSprite {

    public Tiacauh_RipperSprite() {
        super();

        texture( Assets.Sprites.TIACAUH_RIPPER);

        TextureFilm frames = new TextureFilm( texture, 56, 38 );

        idle = new Animation( 2, true );
        idle.frames( frames, 0, 0, 0 );

        run = new Animation( 18, true );
        run.frames( frames, 0 );

        attack = new Animation( 15, false );
        attack.frames( frames, 0 );

        die = new Animation( 10, false );
        die.frames( frames, 0 );

        play( idle );
    }
}
