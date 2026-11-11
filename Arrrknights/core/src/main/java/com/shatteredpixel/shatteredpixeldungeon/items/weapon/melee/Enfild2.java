package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import static com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent.PROFICIENCY;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Gunaccessories.Accessories;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

public class Enfild2 extends MeleeWeapon {
    {
        image = ItemSpriteSheet.ANDREANA;
        hitSound = Assets.Sounds.HIT_SNIPER;
        hitSoundPitch = 0.9f;

        tier = 3;
        DLY = 3f; //0.33x speed
        RCH = 50;    //extra reach

        //also cannot surprise attack, see Hero.canSurpriseAttack
    }

    //阻滞射击战术: one aimed shot that cripples, but only on a healthy target
    public static final float ABILITY_DAMAGE_MULT = 3f;
    public static final float ABILITY_CRIPPLE = 3f;
    //the target must still be above this fraction of its health
    public static final float ABILITY_HP_THRESHOLD = 0.5f;

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        if (target == null) {
            return;
        }

        final Char enemy = Actor.findChar(target);
        if (enemy == null || enemy == hero || !Dungeon.level.heroFOV[target]
                || hero.isCharmedBy(enemy) || enemy.alignment == Char.Alignment.ALLY) {
            GLog.w(Messages.get(this, "ability_no_target"));
            return;
        }
        if (!hero.canAttack(enemy)) {
            GLog.w(Messages.get(this, "ability_target_range"));
            return;
        }
        //only a target that is still standing tall enough can be pinned
        if (enemy.HP * 2 < enemy.HT) {
            GLog.w(Messages.get(this, "ability_too_weak", (int) (ABILITY_HP_THRESHOLD * 100)));
            return;
        }

        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, enemy);
        hero.busy();

        hero.sprite.attack(enemy.pos, new Callback() {
            @Override
            public void call() {
                AttackIndicator.target(enemy);

                if (hero.attack(enemy, ABILITY_DAMAGE_MULT, 0, Char.INFINITE_ACCURACY)) {
                    Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
                }
                Invisibility.dispel();

                if (enemy.isAlive()) {
                    Buff.affect(enemy, Cripple.class, ABILITY_CRIPPLE);
                }

                hero.spendAndNext(hero.attackDelay());
                afterAbilityUsed(hero);
            }
        });
    }

    @Override
    public String abilityInfo() {
        return Messages.get(this, "ability_desc",
                (int) (ABILITY_DAMAGE_MULT * 100), (int) ABILITY_CRIPPLE,
                (int) (ABILITY_HP_THRESHOLD * 100));
    }

//FIXME 得心应手在两把狙上没有做到随着天赋点的增加变成2-2
    @Override
    public int min(int lvl) {
       return 8+lvl+ Maccessories +
               ((Dungeon.hero.hasTalent(Talent.PROFICIENCY)&& Maccessories > 2) ? Maccessories : 0);
    }

    @Override
    public int max(int lvl) {
        return  8*(tier+1) +    // 48 + 15
                lvl*(tier+10) +
                 Maccessories +
                ((Dungeon.hero.hasTalent(Talent.PROFICIENCY)&& Maccessories > 1) ? Maccessories : 0);   //scaling unchanged
    }

    @Override
    public int value() { return super.value() + 100; }

    @Override
    public boolean isUpgradable() { return true; }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (charge >= chargeCap) {
            damage *= 1.3f;
            charge = 0;
        }
        else SPCharge(20);
        return super.proc(attacker, defender, damage);
    }

    @Override
    public int STRReq(int lvl) {
        return STRReq(tier+2, lvl)+Maccessories; //20 base strength req, up from 18
    }
    @Override
    public float speedFactor( Char owner ) {
        float delay = super.speedFactor( owner );
        if(Dungeon.hero.hasTalent(PROFICIENCY)){
            delay *= 1/(1.1f+Math.pow(Dungeon.hero.pointsInTalent(PROFICIENCY),2)*0.1f);
        }
        return delay;
    }

    @Override
    public String status() {

        //if the artifact isn't IDed, or is cursed, don't display anything
        if (!isIdentified() || cursed) {
            return null;
        }
        //display as percent
        if (chargeCap == 100)
            return Messages.format("%d%%", charge);


        //otherwise, if there's no charge, return null.
        return null;
    }
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
