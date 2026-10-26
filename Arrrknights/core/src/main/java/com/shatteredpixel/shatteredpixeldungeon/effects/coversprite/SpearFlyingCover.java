package com.shatteredpixel.shatteredpixeldungeon.effects.coversprite;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class SpearFlyingCover extends CoverSprite{
    public SpearFlyingCover(){
        super();

        texture( Assets.Sprites.SPEAR_FLYING_COVER );

        TextureFilm frames = new TextureFilm( texture, 108, 108 );

        //4 frames at 8fps, so the flight lines up with the 0.5s drop below
        defaultAnim = new Animation( 16, false );
        defaultAnim.frames( frames, 0, 1, 2, 3);

        play(defaultAnim);
    }
}
