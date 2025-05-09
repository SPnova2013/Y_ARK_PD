package com.shatteredpixel.shatteredpixeldungeon.sprites.skins;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class HinaSprite extends MobSprite {

    private Animation fly;
    private Animation read;
    public HinaSprite() {
        super();

        texture( Assets.Sprites.HINA );

        TextureFilm frames = new TextureFilm( texture, 50, 40 );

        idle = new MovieClip.Animation( 7, true );
        idle.frames( frames, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 45, 46, 47, 48, 49, 50, 51, 52);

        run = new MovieClip.Animation( 20, true );
        run.frames( frames, 1, 2, 3, 4, 5, 6, 7, 8 );

        die = new MovieClip.Animation( 8, false );
        die.frames( frames, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39 );

        attack = new MovieClip.Animation( 25, false );
        attack.frames( frames, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 );

        Sattack = attack.clone();

        zap = attack.clone();

        operate = new Animation( 8, false );
        operate.frames( frames, 40, 41, 40, 41 );

        fly = new Animation( 8, true );
        fly.frames( frames, 42, 43, 44 );

        read = new Animation( 10, false );
        read.frames( frames, 21, 22, 23, 24, 25, 26, 27, 28 );

        alpha(0.5f);

        play( idle.clone() );
    }

    @Override
    public void idle() {
        isMoving = false;
        super.idle();
    }
}