package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class BlazingSunSprite extends MobSprite {

    public BlazingSunSprite() {
        super();

        perspectiveRaise = 5 / 16f;
        renderShadow = false;

        texture( Assets.Sprites.BLAZING_SUN );

        TextureFilm frames = new TextureFilm( texture, 36, 36 );

        idle = new Animation( 1, true );
        idle.frames( frames, 0 );

        run = idle.clone();

        attack = idle.clone();

        die = new Animation( 6, false );
        die.frames( frames, 0 );

        play( idle );
    }
}
