package com.shatteredpixel.shatteredpixeldungeon.effects.coversprite;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class GawainCover extends CoverSprite{
    public GawainCover(){
        super();
        texture( Assets.Sprites.GAWAINCOVER );

        TextureFilm frames = new TextureFilm( texture, 96, 96 );

        defaultAnim = new Animation( 24, false );
        defaultAnim.frames( frames, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 );

        play(defaultAnim);
    }
}
