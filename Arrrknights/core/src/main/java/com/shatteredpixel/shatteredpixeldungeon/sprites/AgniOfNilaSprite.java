package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class AgniOfNilaSprite extends MobSprite {

    public AgniOfNilaSprite() {
        super();

        perspectiveRaise = 5 / 16f;
        renderShadow = false;

        texture( Assets.Sprites.AGNI_OF_NILA );

        TextureFilm frames = new TextureFilm( texture, 36, 34 );

        //two frame idle flicker
        idle = new Animation( 4, true );
        idle.frames( frames, 0, 1 );

        run = idle.clone();

        attack = idle.clone();

        die = new Animation( 8, false );
        die.frames( frames, 0, 1 );

        play( idle );
    }
}
