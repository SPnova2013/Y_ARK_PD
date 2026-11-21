package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

/*
	The night shade: the phantom sheet, held still for idle, movement and death,
	and only really moving when it strikes. Tinted a dark blue and half see-through.
 */
public class NightShadeSprite extends MobSprite {

    private static final float TINT_R = 0.35f;
    private static final float TINT_G = 0.45f;
    private static final float TINT_B = 0.75f;
    private static final float TINT_A = 0.55f;

    public NightShadeSprite() {
        super();

        texture(Assets.Sprites.NIGHT_SHADE);

        //6 frames of 32x32
        TextureFilm frames = new TextureFilm(texture, 32, 32);

        idle = new Animation(8, true);
        idle.frames(frames, 0);

        run = new Animation(12, true);
        run.frames(frames, 0);

        attack = new Animation(15, false);
        attack.frames(frames, 1, 2, 3, 4, 5);

        die = new Animation(10, false);
        die.frames(frames, 0);

        //dark blue and translucent
        tint(TINT_R, TINT_G, TINT_B, TINT_A);

        play(idle);
    }
}
