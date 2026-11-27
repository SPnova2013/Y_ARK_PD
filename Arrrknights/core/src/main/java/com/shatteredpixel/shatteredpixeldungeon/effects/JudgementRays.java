package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;

/*
	Six judgement cuts spread evenly around a point. They fade in over 0.2s,
	then all spin the same way at the same speed for one second, after which
	they are erased. At 120 deg/s a one second spin is exactly two slots,
	so the set of rays ends up where it started.
 */
public class JudgementRays extends Group {

    public static final int COUNT = 6;
    public static final float FADE_IN = 0.3f;
    public static final float SPIN_TIME = 1f;
    public static final float SPIN_SPEED = 120f;

    private final Image[] rays = new Image[COUNT];
    private final float baseAngle;
    private float time = 0f;

    //called on the frame the spin ends, before the rays are erased
    public Callback onFinish;

    public JudgementRays(PointF center, float length, float baseAngle) {
        super();

        this.baseAngle = baseAngle;

        for (int i = 0; i < COUNT; i++) {
            Image ray = Effects.get(Effects.Type.JUDGEMENT_CUT);

            //pivot on the left edge, so each ray sweeps around the centre
            ray.origin.set(0, ray.height / 2f);
            ray.scale.x = length / ray.width;

            ray.x = center.x;
            ray.y = center.y - ray.height / 2f;
            ray.alpha(0f);

            add(ray);
            rays[i] = ray;
        }

        updateAngles(0f);
    }

    @Override
    public void update() {
        super.update();

        time += Game.elapsed;

        float alpha = Math.min(1f, time / FADE_IN);
        for (Image ray : rays) {
            ray.alpha(alpha);
        }

        updateAngles(time);

        if (time >= SPIN_TIME) {
            killAndErase();

            //clear first, so a slow frame cannot report this twice
            Callback callback = onFinish;
            onFinish = null;
            if (callback != null) {
                callback.call();
            }
        }
    }

    private void updateAngles(float elapsed) {
        for (int i = 0; i < COUNT; i++) {
            rays[i].angle = baseAngle + i * 360f / COUNT + SPIN_SPEED * elapsed;
        }
    }
}
