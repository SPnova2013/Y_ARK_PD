package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class EX42 extends MeleeWeapon {
    {
        image = ItemSpriteSheet.EX41;
        hitSound = Assets.Sounds.HIT_WALL1;
        hitSoundPitch = 0.65f;

        tier = 1;
        RCH = 2;

        bones = false;
    }
    @Override
    public int max(int lvl) {
        return  3*(tier+1) +     //6
                lvl*(tier+1);  //+1 per level, down from +2
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        int df = damage;
        damage = Math.min(damage + defender.drRoll(), damage + defender.drRoll());

        if (damage >= df+3) damage = df+3;

        return super.proc(attacker, defender, damage);
    }

    /*@Override
    public int damageRoll(Char owner) {
        if (owner instanceof Hero) {
            Hero hero = (Hero)owner;
            Char enemy = hero.enemy();
            if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)) {
                int diff = max() - min();
                int damage = augment.damageFactor(Random.NormalIntRange(
                        min() + Math.round(diff*0.3f),
                        max()));
                int exStr = hero.STR() - STRReq();
                if (exStr > 0) {
                    damage += Random.IntRange(0, exStr);
                }
                return damage;
            }
        }
        return super.damageRoll(owner);
    }*/

}
