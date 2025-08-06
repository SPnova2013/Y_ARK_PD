package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.AirborneSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.UrsusCrossbowmanSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class UrsusCrossbowman extends AirborneSoldier{

    private int blinkCooldown = 0;
    {
        spriteClass = UrsusCrossbowmanSprite.class;

        HP = HT = 20;
        damageMax =9;
        damageMin =2;
        drMax =4;
        drMin =0;
        attackSkill =12;
        defenseSkill = 5;

        EXP = 4;
        maxLvl = 12;

        loot = Gold.class;
        lootChance = 0.3f;
        immunities.add(Silence.class);
    }
    @Override
    protected boolean canAttack( Char enemy ) {
        if (fieldOfView[enemy.pos] && Dungeon.level.distance( pos, target ) == 1 && blinkCooldown <= 0) {
            blink( target );
        }
        Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE);
        return attack.collisionPos == enemy.pos;
    }
    @Override
    protected boolean act() {
        blinkCooldown--;
        return super.act();
    }


    private void blink( int target ) {
        int posx = pos % Dungeon.level.width();
        int posy = pos / Dungeon.level.width();
        int targetx = target % Dungeon.level.width();
        int targety = target / Dungeon.level.width();
        int finalx = Math.max(1, Math.min(posx*2-targetx, Dungeon.level.width()));
        int finaly = Math.max(1, Math.min(posy*2-targety, Dungeon.level.height()));
        int finalTarget = finaly*Dungeon.level.width() + finalx;
        Ballistica route = new Ballistica( pos, finalTarget, Ballistica.MAGIC_BOLT);
        int cell = route.collisionPos;

        //can't occupy the same cell as another char, so move back one.
        if (Actor.findChar( cell ) != null && cell != this.pos)
            cell = route.path.get(route.dist-1);

        if (Dungeon.level.avoid[ cell ]){
            ArrayList<Integer> candidates = new ArrayList<>();
            for (int n : PathFinder.NEIGHBOURS8) {
                cell = route.collisionPos + n;
                if (Dungeon.level.passable[cell] && Actor.findChar( cell ) == null) {
                    candidates.add( cell );
                }
            }
            if (candidates.size() > 0)
                cell = Random.element(candidates);
            else {
                blinkCooldown = Random.IntRange(16, 20);
                return;
            }
        }

        ScrollOfTeleportation.appear( this, cell );
        if (Dungeon.isChallenged(Challenges.TACTICAL_UPGRADE)) Buff.affect(this, Barrier.class).setShield(4);

        blinkCooldown = Random.IntRange(16, 20);
    }


    private static final String BLINK = "blinkcooldown";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(BLINK, blinkCooldown);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        blinkCooldown = bundle.getInt(BLINK);
    }
}
