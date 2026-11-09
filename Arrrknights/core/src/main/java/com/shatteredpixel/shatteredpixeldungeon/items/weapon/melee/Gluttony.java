package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Gluttony extends MeleeWeapon {
    public static final String AC_ZAP = "ZAP";
    {
        image = ItemSpriteSheet.CARNEL;
        hitSound = Assets.Sounds.HIT_GLUTONY;
        hitSoundPitch = 1f;

        tier = 4;
        DLY = 0.75f;

        defaultAction = AC_ZAP;
    }

    //食噬之印 lasts this long, and reaches this far from the struck target
    public static final int ABILITY_DURATION = 10;
    public static final int ABILITY_RADIUS = 2;

    //a free action: the mark simply settles on the blade
    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, null);

        Buff.prolong(hero, DevourMark.class, ABILITY_DURATION);

        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.BURNING);
        Invisibility.dispel();

        afterAbilityUsed(hero);
        //no spendAndNext here on purpose, biting into the mark costs no turn
    }

    @Override
    public String abilityInfo() {
        return Messages.get(this, "ability_desc", ABILITY_DURATION);
    }

    //while the mark burns, every bite spills over onto whatever stands nearby
    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (attacker.buff(DevourMark.class) != null && defender.isAlive()) {
            PathFinder.buildDistanceMap(defender.pos, BArray.not(Dungeon.level.solid, null), ABILITY_RADIUS);
            for (int cell = 0; cell < PathFinder.distance.length; cell++) {
                //0 is the struck target itself
                if (PathFinder.distance[cell] == 0 || PathFinder.distance[cell] > ABILITY_RADIUS) continue;

                Char ch = Actor.findChar(cell);
                if (ch == null || ch == attacker || ch == defender || !ch.isAlive()) continue;
                if (ch.alignment != Char.Alignment.ENEMY) continue;

                CellEmitter.get(cell).burst(Speck.factory(Speck.WOOL), 3);
                ch.damage(Math.max(1, damage - ch.drRoll()), attacker);
            }
        }

        return super.proc(attacker, defender, damage);
    }

    //食噬之印: the mark itself
    public static class DevourMark extends FlavourBuff {

        {
            type = buffType.POSITIVE;
            announced = true;
        }

        @Override
        public int icon() {
            return BuffIndicator.WEAPON;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (ABILITY_DURATION - visualcooldown()) / (float) ABILITY_DURATION);
        }
    }

    @Override
    public int max(int lvl) {
        return  4*(tier) + 2 +  //18durable_projectiles + 4
                lvl*(tier); }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_ZAP);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_ZAP)) {
            if (!isEquipped(hero)) return;
            if (charge >= 50) {
                if (!cursed) {
                    for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                        if (Dungeon.level.adjacent(mob.pos, hero.pos) && mob.alignment != Char.Alignment.ALLY) {
                            int dmg = hero.damageRoll();
                            dmg *= 1.6f;
                            CellEmitter.get( mob.pos ).burst( Speck.factory( Speck.WOOL ), 3 );
                            mob.damage(dmg, this);
                        }
                    }
                    CellEmitter.get( curUser.pos ).burst( Speck.factory( Speck.WOOL ), 10 );
                    charge -= 50;
                    Sample.INSTANCE.play(Assets.Sounds.BLAST);
                    hero.sprite.zap(hero.pos);
                }
                else {
                    Buff.affect(hero, Roots.class, 5f);
                    cursedKnown = true;
                    charge -= 50;
                }
                Invisibility.dispel();
                updateQuickslot();
                hero.spendAndNext(1f);
            }
        }
    }

    @Override
    public String status() {
        if (chargeCap == 100)
            return Messages.format("%d%%", charge);
        return null;
    }

    //bottom-left: how many weapon charges the ability has right now.
    //the cap is left out on purpose, the slot is too narrow for a fraction
    @Override
    public String statusBottom() {

        if (!isIdentified() || cursed || Dungeon.hero == null) {
            return null;
        }
        Charger charger = Dungeon.hero.buff(Charger.class);
        if (charger == null) {
            return null;
        }
        return Integer.toString(Math.min(charger.charges, charger.chargeCap()));
    }
}
