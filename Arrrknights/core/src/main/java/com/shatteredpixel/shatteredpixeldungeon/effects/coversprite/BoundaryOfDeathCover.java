package com.shatteredpixel.shatteredpixeldungeon.effects.coversprite;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class BoundaryOfDeathCover extends CoverSprite{
    public BoundaryOfDeathCover(){
        super();

        texture( Assets.Sprites.BODCOVER );

        TextureFilm frames = new TextureFilm( texture, 48, 48 );

        defaultAnim = new Animation( 20, false );
        defaultAnim.frames( frames, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 10, 10, 10, 10, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41 );

        play(defaultAnim);
    }

}