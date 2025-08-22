package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class RazorfrostSprite extends MobSprite{
    public RazorfrostSprite(){
        super();

        texture( Assets.Sprites.RAZORFROST );

        TextureFilm frames = new TextureFilm( texture, 32, 32 );

        idle = new Animation( 2, true );
        idle.frames( frames, 0, 0, 0 );

        run = new Animation( 18, true );
        run.frames( frames, 1, 2, 3, 4, 5, 6 );

        attack = new Animation( 15, false );
        attack.frames( frames, 7, 8, 9, 10, 11, 12, 13 );

        die = new Animation( 10, false );
        die.frames( frames, 0 );

        play( idle );
    }
}
