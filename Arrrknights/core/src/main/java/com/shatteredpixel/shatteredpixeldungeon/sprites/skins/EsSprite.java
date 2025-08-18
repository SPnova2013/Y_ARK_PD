package com.shatteredpixel.shatteredpixeldungeon.sprites.skins;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class EsSprite extends SkinSprite{
    public EsSprite() {
        super();
        texture( Assets.Sprites.ES );
        TextureFilm frames = new TextureFilm( texture, 80, 50 );

        idle = new MovieClip.Animation( 2, true );
        idle.frames( frames, 0 );

        run = new MovieClip.Animation( 15, false );
        run.frames( frames, 0 );

        attack = new MovieClip.Animation( 10, false );
        attack.frames( frames, 0 );

        zap = attack.clone();

        die = new MovieClip.Animation( 8, false );
        die.frames( frames, 0 );

        alpha(0.5f);

        play( idle.clone() );
    }

    @Override
    public void idle() {
        isMoving = false;
        super.idle();
    }
}
