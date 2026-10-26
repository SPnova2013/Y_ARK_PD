package com.shatteredpixel.shatteredpixeldungeon.effects.coversprite;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class SpearLandingCover extends CoverSprite{
    public SpearLandingCover(){
        super();

        texture( Assets.Sprites.SPEAR_LANDING_COVER );

        TextureFilm frames = new TextureFilm( texture, 108, 108 );

        defaultAnim = new Animation( 30, false );
        defaultAnim.frames( frames, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        play(defaultAnim);
    }
}
