package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SSP.StaffOfVision;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.tweeners.AlphaTweener;

public class WardSprite_Vision extends MobSprite {

    private Animation tierIdles[] = new Animation[7];

    public WardSprite_Vision() {
        super();

        texture(Assets.Sprites.WARDS);

        TextureFilm frames = new TextureFilm(texture, 42, 32);

        tierIdles[1] = new Animation(1, true);
        tierIdles[1].frames(frames, 0);

        tierIdles[2] = new Animation(1, true);
        tierIdles[2].frames(frames, 1);

        tierIdles[3] = new Animation(1, true);
        tierIdles[3].frames(frames, 2);

        tierIdles[4] = new Animation(1, true);
        tierIdles[4].frames(frames, 3);

        tierIdles[5] = new Animation(1, true);
        tierIdles[5].frames(frames, 4);

        tierIdles[6] = new Animation(1, true);
        tierIdles[6].frames(frames, 5);

    }

    @Override
    public void turnTo(int from, int to) {
        //do nothing
    }

    @Override
    public void die() {
        super.die();
        //cancels die animation and fades out immediately
        play(idle, true);
        emitter().burst(MagicMissile.WardParticle.UP, 10);
        parent.add(new AlphaTweener(this, 0, 2f) {
            @Override
            protected void onComplete() {
                WardSprite_Vision.this.killAndErase();
                parent.erase(this);
            }
        });
    }

    @Override
    public void resetColor() {
        super.resetColor();
        if (ch instanceof StaffOfVision.VisionWard) {
            StaffOfVision.VisionWard ward = (StaffOfVision.VisionWard) ch;
            if (ward.tier <= 3) {
                brightness(Math.max(0.2f, 1f - (ward.totalZaps / (float) (2 * ward.tier - 1))));
            }
        }
    }

    public void linkVisuals(Char ch) {

        if (ch == null) return;
        updateTier(((StaffOfVision.VisionWard) ch).tier);

    }

    public void updateTier(int tier) {

        idle = tierIdles[tier];
        run = idle.clone();
        attack = idle.clone();
        die = idle.clone();

        //always render first
        if (parent != null) {
            parent.sendToBack(this);
        }

        resetColor();
        if (ch != null) place(ch.pos);
        idle();

        if (tier <= 3) {
            shadowWidth = 1.2f;
            shadowHeight = 0.25f;
            perspectiveRaise = 6 / 16f; //6 pixels
        } else {
            shadowWidth = 1.2f;
            shadowHeight = 0.25f;
            perspectiveRaise = 6 / 16f; //6 pixels
        }

    }

    private float baseY = Float.NaN;

    @Override
    public void place(int cell) {
        super.place(cell);
        baseY = y;
    }

    @Override
    public void update() {
        super.update();
        //if tier is greater than 3
        if (perspectiveRaise >= 6 / 16f && !paused) {
            if (Float.isNaN(baseY)) baseY = y;
            y = baseY + (float) Math.sin(Game.timeTotal);
            shadowOffset = 0.25f - 0.8f * (float) Math.sin(Game.timeTotal);
        }
    }

    @Override
    public int blood() {
        return 0xFFCC33FF;
    }
}
