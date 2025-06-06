package com.shatteredpixel.shatteredpixeldungeon.effects.coversprite;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.Game;
import com.watabou.noosa.TextureFilm;

public class EX42Cover extends CoverSprite{
    public EX42Cover(){
        super();

        texture( Assets.Sprites.EX42COVER );

        TextureFilm frames = new TextureFilm( texture, 38, 36 );

        defaultAnim = new Animation( 24, false );
        defaultAnim.frames( frames, 8, 9, 10, 11, 12, 13, 14, 15, 16 );

        play(defaultAnim);
    }

    public void placeAndPlay(int cell) {
        this.visible = true;
        super.place(cell);
        play(defaultAnim, true);
        Game.scene().add(this);
    }
}