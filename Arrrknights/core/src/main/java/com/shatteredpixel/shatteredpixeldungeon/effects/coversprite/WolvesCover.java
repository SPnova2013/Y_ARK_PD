package com.shatteredpixel.shatteredpixeldungeon.effects.coversprite;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

/*
	The pack, called in for a moment: 12 frames of 108x108 at about 50ms each.
 */
public class WolvesCover extends CoverSprite {

    public WolvesCover() {
        super();

        texture( Assets.Sprites.WOLVES_COVER );

        TextureFilm frames = new TextureFilm( texture, 108, 108 );

        //50ms per frame is 20 frames per second
        defaultAnim = new Animation( 20, false );
        defaultAnim.frames( frames, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 );

        play(defaultAnim);
    }
}
