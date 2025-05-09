package com.shatteredpixel.shatteredpixeldungeon.items.wands.SSP;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfAmplified;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Elastic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class StaffOfTwinTurbo extends Wand {
    {
        image = ItemSpriteSheet.WAND_TWIN;
        collisionProperties = Ballistica.PROJECTILE;
    }
    protected int strRoll(){
        return Random.IntRange(0, 2*(buffedLvl() + 3));
    }

    @Override
    public String statsDesc() {
        if (levelKnown)
            return Messages.get(this, "stats_known_desc",
                    2*(buffedLvl() + 3) ,
                    3 + 3*buffedLvl() + RingOfAmplified.DamageBonus(Dungeon.hero) * 3
            );
        else
            return Messages.get(this, "stats_desc", 6, 6);
    }

    @Override
    protected void onZap(Ballistica bolt) {
        Sample.INSTANCE.play( Assets.Sounds.BLAST );
        WandOfBlastWave.BlastWave.blast(bolt.collisionPos);
        Char ch = Actor.findChar(bolt.collisionPos);
        if (ch != null){
            processSoulMark(ch, chargesPerCast());
            if (ch.isAlive() && bolt.path.size() > bolt.dist+1 && ch.pos == bolt.collisionPos) {
                Ballistica trajectory = new Ballistica(ch.pos, bolt.path.get(bolt.dist + 1), Ballistica.MAGIC_BOLT);
                int strength = Math.max(strRoll(),strRoll());
                throwChar(ch, trajectory, strength, false);
            }
        }
    }
    public static void throwChar(final Char ch, final Ballistica trajectory, int power,
                                 boolean closeDoors) {
        throwChar(ch, trajectory, power, closeDoors, true);
    }

    public static void throwChar(final Char ch, final Ballistica trajectory, int power,
                                 boolean closeDoors, boolean collideDmg){
        if (ch.properties().contains(Char.Property.BOSS)) {
            power /= 2;
        }

        int dist = Math.min(trajectory.dist, power);

        boolean collided = dist == trajectory.dist;

        if (dist == 0 || ch.properties().contains(Char.Property.IMMOVABLE) || ch.properties().contains(Char.Property.NO_KNOCKBACK)) {
            ch.damage(Math.max(
                    Random.IntRange(0, 3 + 3*curItem.buffedLvl() + RingOfAmplified.DamageBonus(Dungeon.hero) * 3),
                    Random.IntRange(0, 3 + 3*curItem.buffedLvl() + RingOfAmplified.DamageBonus(Dungeon.hero) * 3)
            ), curItem);
            return;
        }

        //large characters cannot be moved into non-open space
        for (int i = 1; i <= dist; i++) {
            if (Char.hasProp(ch, Char.Property.LARGE) && !Dungeon.level.openSpace[trajectory.path.get(i)]){
                dist = i-1;
                collided = true;
                break;
            }
            if (Dungeon.level.pit[trajectory.path.get(i)]){
                dist = i;
                break;
            }
        }

        if (Actor.findChar(trajectory.path.get(dist)) != null){
            dist--;
            collided = true;
        }

        if (dist < 0) return;

        final int newPos = trajectory.path.get(dist);

        if (newPos == ch.pos) return;

        final int finalDist = dist;
        final boolean finalCollided = collided && collideDmg;
        final int initialpos = ch.pos;

        Actor.addDelayed(new Pushing(ch, ch.pos, newPos, new Callback() {
            public void call() {
                if (initialpos != ch.pos) {
                    //something caused movement before pushing resolved, cancel to be safe.
                    ch.sprite.place(ch.pos);
                    return;
                }
                int oldPos = ch.pos;
                ch.pos = newPos;
                if (finalCollided && ch.isAlive()) {
                    ch.damage(Math.max(
                            Random.IntRange(0, 3 + 3*curItem.buffedLvl() + RingOfAmplified.DamageBonus(Dungeon.hero) * 3 + 8*finalDist),
                            Random.IntRange(0, 3 + 3*curItem.buffedLvl() + RingOfAmplified.DamageBonus(Dungeon.hero) * 3 + 8*finalDist)
                    ), this);
                    Paralysis.prolong(ch, Paralysis.class, Math.max(Random.IntRange(0,2+finalDist),Random.IntRange(0,2+finalDist)));
                }
                if (closeDoors && Dungeon.level.map[oldPos] == Terrain.OPEN_DOOR){
                    Door.leave(oldPos);
                }
                Dungeon.level.occupyCell(ch);
                if (ch == Dungeon.hero){
                    //FIXME currently no logic here if the throw effect kills the hero
                    Dungeon.observe();
                }
            }
        }), -1);
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        new Elastic().proc(staff, attacker, defender, damage);
    }
}
