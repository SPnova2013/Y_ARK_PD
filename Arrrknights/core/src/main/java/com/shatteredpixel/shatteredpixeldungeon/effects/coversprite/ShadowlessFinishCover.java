package com.shatteredpixel.shatteredpixeldungeon.effects.coversprite;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class ShadowlessFinishCover extends CoverSprite{
    public ShadowlessFinishCover(){
        super();

        texture( Assets.Sprites.SHADOWLES_FINISH_COVER );

        TextureFilm frames = new TextureFilm( texture, 128, 92 );

        defaultAnim = new Animation( 12, false );
        defaultAnim.frames( frames, 0, 1, 2, 3, 4, 5, 6);

        play(defaultAnim);
    }
}
