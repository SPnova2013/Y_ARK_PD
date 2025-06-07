package com.shatteredpixel.shatteredpixeldungeon.sprites.skins;

import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;

public abstract class SkinSprite extends MobSprite {
    public boolean allowSpecialIdle = false;
    public float specialIdleDelay = 5f;
    public boolean allowSpecialAfterAttack = false;
    public boolean isAllowSpecialIdle(){
        return allowSpecialIdle;
    }
    public boolean isAllowSpecialAfterAttack(){
        return allowSpecialAfterAttack;
    }
    public float getSpecialIdleDelay(){
        return specialIdleDelay;
    }
}
