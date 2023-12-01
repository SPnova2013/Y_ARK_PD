package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.miniboss.Faust;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityLevel;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CrossbowmanSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FaustSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.Siesta_SniperSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Sniper extends Mob {
    {
        spriteClass = Siesta_SniperSprite.class;

        HP = HT = 120;
        defenseSkill = 22;

        EXP = 16;
        maxLvl = 30;

        loot = new PotionOfHealing();
        lootChance = 0.1f;

        immunities.add(Silence.class);
    }

    private int charge = 0; // 2이 될경우 강화 사격

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(32, 48);
    }

    @Override
    public int attackSkill( Char target ) {
        return 44;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 12);
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE);
        return !Dungeon.level.adjacent( pos, enemy.pos ) && attack.collisionPos == enemy.pos;
    }

    @Override
    public int attackProc( Char enemy, int damage ) {
        if (charge >= 2) {
            Buff.affect(enemy, Paralysis.class, 1f);
            charge = 0;
        }
        else {
            damage = super.attackProc(enemy, damage);
            charge++;
        }

        return damage;
    }

    @Override
    public void move(int step, boolean travelling) {
        charge = 0;
        super.move(step, travelling);
    }

    @Override
    protected boolean getCloser( int target ) {
        if (state == HUNTING) {
            return enemySeen && getFurther( target );
        } else {
            return super.getCloser( target );
        }
    }

    @Override
    public void rollToDropLoot() {
        lootChance *= (5f - Dungeon.LimitedDrops.SNIPER_HP.count) / 5f;
        super.rollToDropLoot();
    }
    @Override
    protected Item createLoot(){//change from budding
        Dungeon.LimitedDrops.SNIPER_HP.count++;
        return super.createLoot();
    }

    private static final String SKILLCD   = "charge";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( SKILLCD, charge );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        charge = bundle.getInt(SKILLCD);

    }
}
