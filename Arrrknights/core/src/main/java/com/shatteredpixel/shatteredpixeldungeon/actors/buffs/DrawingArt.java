package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import static com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent.FREE_FIRE;
import static com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent.FULL_FIREPOWER;
import static com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent.SKILL_ENHANCEMENT;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.GunWeapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

import java.text.DecimalFormat;
import java.util.Arrays;


public class DrawingArt extends Buff implements ActionIndicator.Action{
    private float pcharge = 0;
    private int chargeTurn = 150;
    private boolean ready = false;
    @Override
    public int icon() {
        return BuffIndicator.BLESS;
    }
    @Override
    public String toString() {
        return Messages.get(this, "name");
    }
    @Override
    public String desc() {
        if(ready) return Messages.get(this, "desc_ready");
        else return Messages.get(this, "desc", String.valueOf(chargeTurn-pcharge));
    }
    @Override
    public String actionName() {
        return Messages.get(this, "action_name");
    }

    @Override
    public int actionIcon() {
        return HeroIcon.COMBO;
    }
    @Override
    public int indicatorColor() {
        return 0x444444;
    }

    @Override
    public void doAction() {
        Hero hero = Dungeon.hero;
        if (hero == null) return;

        if(!(Dungeon.hero.belongings.weapon instanceof GunWeapon)) return;

        if(Dungeon.hero.belongings.weapon.cursed) {
            Buff.affect(Dungeon.hero, Burning.class).reignite(Dungeon.hero,4f);
            Dungeon.hero.belongings.weapon.cursedKnown = true;
            return;
        }
        pcharge = 0;
        ready = false;
        ActionIndicator.clearAction(this);
        GunWeapon ss;
        ss = (GunWeapon) Dungeon.hero.belongings.weapon;
        int fireCount = 0;
        int bulletCount = ss.getBulletNum();
        int maxCount = 5+(Dungeon.hero.hasTalent(FULL_FIREPOWER)? 3:0);
        boolean inFOV = false;
        while(fireCount < maxCount && bulletCount>0){
            for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )){
                if( Dungeon.level.mobs.toArray( new Mob[0] ).length==0){
                    GLog.w(Messages.get(this, "no_enemy"));
                    bulletCount=0;
                    break;
                }
                if(fireCount >= maxCount)break;
                if(bulletCount == 0){
                    GLog.w(Messages.get(GunWeapon.class, "fizzles"));
                    break;
                }
                if (mob.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[mob.pos] && !(mob instanceof NPC)) {
                    Ballistica shot = new Ballistica(target.pos, mob.pos, Ballistica.PROJECTILE);
                    int cell = shot.collisionPos;
                    target.sprite.Sattack(cell);
                    if(ss.tryToShoot(mob.pos, shot, false, 1.2f+0.1f*(Dungeon.hero.pointsInTalent(SKILL_ENHANCEMENT)),1)){
                        ((Hero)target).spend(-ss.getFireTick());
                    }
                    fireCount++;
                    bulletCount--;
                    inFOV = true;
                }
            }
            if(!inFOV) {
                ready = true;
                break;
            }
        }
        if(!inFOV && bulletCount<5) ((Hero)target).spendAndNext(2f);
    }
    @Override
    public void detach() {
        super.detach();
        ActionIndicator.clearAction(this);
    }
    public void Charged(float time) {
        if(!(Dungeon.hero.belongings.weapon instanceof GunWeapon)) detach();
        if(Dungeon.hero.hasTalent(Talent.FULL_FIREPOWER) && Dungeon.hero.pointsInTalent(Talent.FULL_FIREPOWER)>=2) chargeTurn = 100;
        else chargeTurn = 150;
        if (ready) {
            ActionIndicator.setAction(this);
            return;
        }

        pcharge += time;

        if (pcharge >= chargeTurn) {
            ready = true;
        }

        if (ready) ActionIndicator.setAction(this);
        else ActionIndicator.clearAction(this);
    }
    public void gainCharge() {
        pcharge++;
        if (pcharge > chargeTurn) ready = true;//change from budding
        if (ready) ActionIndicator.setAction(this);
    }

    private static final String PCHARGE = "pcharge";
    private static final String READY = "ready";
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(PCHARGE, pcharge);
        bundle.put(READY, ready);
    }
    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        pcharge = bundle.getFloat(PCHARGE);
        ready = bundle.getBoolean(READY);
        if (ready) ActionIndicator.setAction(this);
    }
}
