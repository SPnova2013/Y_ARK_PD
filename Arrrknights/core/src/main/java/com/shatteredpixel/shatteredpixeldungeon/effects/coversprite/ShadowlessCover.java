package com.shatteredpixel.shatteredpixeldungeon.effects.coversprite;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class ShadowlessCover extends CoverSprite{
    public ShadowlessCover(){
        super();

        texture( Assets.Sprites.SHADOWLESSCOVER );

        TextureFilm frames = new TextureFilm( texture, 96, 96 );

        defaultAnim = new Animation( 20, false );
        defaultAnim.frames( frames, 0, 1, 2, 3, 4, 5);

        play(defaultAnim);
    }
}
