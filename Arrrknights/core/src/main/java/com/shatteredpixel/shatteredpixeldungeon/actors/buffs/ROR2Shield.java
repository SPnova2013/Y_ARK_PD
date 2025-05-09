package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

import javax.swing.plaf.PanelUI;

public class ROR2Shield extends ShieldBuff{
    {
        type = buffType.POSITIVE;
    }
    private float waitBeforeRecover = 10;
    private int maxShield = 0;

    public void setMaxShield(int maxShield, boolean hard){
        this.maxShield=maxShield;
        if(hard) super.hardSetShield(maxShield);
    }

    public void addMaxShield(int maxShield){addMaxShield(maxShield, true);}

    public void addMaxShield(int maxShield, boolean hard){
        this.maxShield += maxShield;
        if(hard) super.incShield(maxShield);
    }

    public void decMaxShield(int maxShield){
        this.maxShield -= maxShield;
        if(shielding()>this.maxShield) super.hardSetShield(maxShield);
        if(this.maxShield<=0) Buff.detach(Dungeon.hero, ROR2Shield.class);
    }

    public int getMaxShield(){
        return maxShield;
    }

    public void setShield(int shield){
        super.hardSetShield(shield);
    }

    @Override
    public boolean act() {
        if(waitBeforeRecover<=0 && !(shielding()>maxShield)) {
            if (target instanceof Hero) {super.incShield((int)Math.ceil(maxShield / 8f));}
            else {super.incShield((int)Math.ceil(maxShield / 4f));}
        }
        if(shielding()>maxShield) super.hardSetShield(maxShield);
        if(waitBeforeRecover>0)waitBeforeRecover--;
        spend(TICK);
        return true;
    }
    @Override
    public int absorbDamage( int dmg ){
        this.waitBeforeRecover = 10f;
        if(target instanceof Hero) this.waitBeforeRecover = 20f;
        if (shielding() >= dmg){
            super.decShield(dmg);
            dmg = 0;
        } else {
            dmg -= shielding();
            super.hardSetShield(0);
        }
        if (target != null) target.needsShieldUpdate = true;
        return dmg;
    }
    @Override
    public int icon() {
        return BuffIndicator.ARMOR;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", getMaxShield(), shielding())
                +((waitBeforeRecover>0)?"\n"+Messages.get(this, "desc_time", (int)waitBeforeRecover):"");
    }
    private static final String MAX_SHIELD = "max_shield";
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(MAX_SHIELD, maxShield);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        maxShield = bundle.getInt(MAX_SHIELD);
    }
}
