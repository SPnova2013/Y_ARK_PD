package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.watabou.noosa.TextureFilm;

public class BombtailSprite extends MobSprite{
    public BombtailSprite() {
        super();

        texture( Assets.Sprites.BOMBTAIL );

        TextureFilm frames = new TextureFilm( texture, 32, 32 );

        idle = new Animation( 2, true );
        idle.frames( frames, 0, 1, 2, 3);

        run = new Animation( 15, true );
        run.frames( frames, 0, 1, 2, 3 );

        attack = new Animation( 15, false );
        attack.frames( frames, 3, 4, 5, 6 );

        die = new Animation( 12, false );
        die.frames( frames, 7, 8, 9, 10, 11, 12, 13, 14 );

        play( idle );
    }
    @Override
    public void die() {
        super.die();
        if (Dungeon.level.heroFOV[ch.pos]) {
            emitter().burst( Speck.factory( Speck.BONE ), 6 );
        }
    }

    @Override
    public int blood() {
        return 0xFFcccccc;
    }
}
