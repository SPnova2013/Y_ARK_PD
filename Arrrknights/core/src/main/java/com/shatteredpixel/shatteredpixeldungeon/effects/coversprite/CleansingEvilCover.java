package com.shatteredpixel.shatteredpixeldungeon.effects.coversprite;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class CleansingEvilCover extends CoverSprite{
    public CleansingEvilCover(){
        super();

        texture( Assets.Sprites.CLEANSING_EVIL_COVER );

        TextureFilm frames = new TextureFilm( texture, 180, 180 );

        defaultAnim = new Animation( 30, false );
        defaultAnim.frames( frames, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14);

        play(defaultAnim);
    }
}
