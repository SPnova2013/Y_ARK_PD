package com.shatteredpixel.shatteredpixeldungeon.effects.coversprite;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.PointF;

public class CoverSprite extends MovieClip implements Tweener.Listener, MovieClip.Listener {

    //the amount the sprite is raised from flat when viewed in a raised perspective
    protected float perspectiveRaise    = 6 / 16f; //6 pixels

    protected Animation defaultAnim;

    public CoverSprite() {
        super();
        listener = this;
    }

    public void place( int cell ) {
        point( worldToCamera( cell ) );
    }
    public void placeAndPlay(int cell) {
        this.visible = true;
        place(cell);
        play(defaultAnim, true);
        Game.scene().add(this);
    }
    public void centerAndPlay(int cell) {
        this.visible = true;

        final int csize = DungeonTilemap.SIZE;
        int w = Dungeon.level.width();
        float centerX = ((cell % w) + 0.5f) * csize;
        float centerY = ((cell / w) + 0.5f) * csize;


        float x = centerX - width() / 2;
        float y = centerY - height() / 2;

        y -= csize * perspectiveRaise;

        point(new PointF(
                PixelScene.align(Camera.main, x),
                PixelScene.align(Camera.main, y)
        ));

        play(defaultAnim, true);
        Game.scene().add(this);
    }
    public PointF worldToCamera(int cell ) {

        final int csize = DungeonTilemap.SIZE;

        return new PointF(
                PixelScene.align(Camera.main, ((cell % Dungeon.level.width()) + 0.5f) * csize - width() * 0.5f),
                PixelScene.align(Camera.main, ((cell / Dungeon.level.width()) + 1.0f) * csize - height() - csize * perspectiveRaise)
        );
    }

    @Override
    public void onComplete(Animation anim) {
        killAndErase();
    }

    @Override
    public void onComplete(Tweener tweener) { }

    public void heroAttackProc(int targetCell){
        if(Dungeon.hero.heroClass == HeroClass.ROSECAT && Dungeon.hero.CharSkin == 0) {
            EX42Cover ex42Cover = new EX42Cover();
            ex42Cover.placeAndPlay(targetCell);
        }
    }
}
