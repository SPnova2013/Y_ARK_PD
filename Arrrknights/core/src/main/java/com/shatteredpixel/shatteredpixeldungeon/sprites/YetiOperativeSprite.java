package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class YetiOperativeSprite extends MobSprite{
    public YetiOperativeSprite(){
        super();

        texture( Assets.Sprites.YETIOPERATIVE );

        TextureFilm frames = new TextureFilm( texture, 32, 32 );

        idle = new Animation( 2, true );
        idle.frames( frames, 23 );

        run = new Animation( 12, true );
        run.frames( frames, 0, 1, 2, 3, 4, 5, 6 );

        attack = new Animation( 12, false );
        attack.frames( frames, 7, 8, 9, 10, 11, 12 );

        die = new Animation( 10, false );
        die.frames( frames, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22 );

        play( idle );
    }
}
