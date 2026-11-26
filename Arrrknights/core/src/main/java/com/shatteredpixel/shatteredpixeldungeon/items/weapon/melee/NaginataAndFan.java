package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hallucination;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Oblivion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class NaginataAndFan extends MeleeWeapon implements MeleeWeapon.BlockingWeapon {
    {
        image = ItemSpriteSheet.NAGINATA_AND_FAN;
        hitSound = Assets.Sounds.HIT_SPEAR;
        hitSoundPitch = 1.18f;

        tier = 4;
    }
    @Override
    public int max(int lvl) {
        return  5*(tier) +   //20 + 4
                lvl*(tier);
    }
    @Override
    public int defenseFactor( Char owner ) {
        return DRMax();
    }

    public int DRMax(){
        return DRMax(buffedLvl());
    }

    public int DRMax(int lvl){
        return 2 + lvl;
    }

    //「假面丧心舞 暗黑能乐」: the dance feeds on what already festers there
    public static final int ABILITY_BONUS_BASE = 8;

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    public String abilityInfo() {
        return Messages.get(this, "ability_desc", ABILITY_BONUS_BASE);
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        if (target == null) {
            return;
        }

        final Char enemy = Actor.findChar(target);
        if (enemy == null || enemy == hero || !Dungeon.level.heroFOV[target] || hero.isCharmedBy(enemy)) {
            GLog.w(Messages.get(this, "ability_no_target"));
            return;
        }
        if (!hero.canAttack(enemy)) {
            GLog.w(Messages.get(this, "ability_target_range"));
            return;
        }

        //every curse already on the target is worth a little more weight:
        //+8 for the first, one less for each after it, never below +1
        int debuffs = 0;
        for (Buff b : enemy.buffs()) {
            if (b.type == Buff.buffType.NEGATIVE) {
                debuffs++;
            }
        }

        int bonus = 0;
        for (int i = 0; i < debuffs; i++) {
            bonus += Math.max(1, ABILITY_BONUS_BASE - i);
        }

        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, enemy);
        hero.busy();

        final int extra = bonus;
        hero.sprite.attack(enemy.pos, new Callback() {
            @Override
            public void call() {
                AttackIndicator.target(enemy);

                if (hero.attack(enemy, 1f, extra, Char.INFINITE_ACCURACY)) {
                    Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
                }
                Invisibility.dispel();

                if (enemy.isAlive()) {
                    //the mask wipes the slate clean...
                    for (Buff b : new ArrayList<>(enemy.buffs())) {
                        if (b.type == Buff.buffType.NEGATIVE) {
                            b.detach();
                        }
                    }
                    //...and lays down every colour it knows
                    applyEveryDebuff(enemy);
                }

                hero.spendAndNext(hero.attackDelay());
                afterAbilityUsed(hero);
            }
        });
    }

    //the whole pool the weapon can normally roll for, applied at once
    private void applyEveryDebuff(Char defender) {
        Buff.prolong(defender, Amok.class, 1);
        Buff.prolong(defender, Charm.class, 1);
        Buff.prolong(defender, Hex.class, 1);
        Buff.prolong(defender, Paralysis.class, 1);
        Buff.prolong(defender, Slow.class, 1);
        Buff.prolong(defender, Vulnerable.class, 1);
        Buff.prolong(defender, Terror.class, 1);
        Buff.prolong(defender, Vertigo.class, 1);
        Buff.prolong(defender, Weakness.class, 1);
        Buff.prolong(defender, Silence.class, 1);
        Buff.affect(defender, Hallucination.class).set(1);
        Buff.affect(defender, Oblivion.class, 1);
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        switch(Random.Int(11)){
            case 0:
                Buff.prolong(defender, Amok.class, 1);
                break;
            case 1:
                Buff.prolong(defender, Charm.class, 1);
                break;
            case 2:
                Buff.prolong(defender, Hex.class, 1);
                break;
            case 3:
                Buff.prolong(defender, Paralysis.class, 1);
                break;
            case 4:
                Buff.prolong(defender, Slow.class, 1);
                break;
            case 5:
                Buff.prolong(defender, Vulnerable.class, 1);
                break;
            case 6:
                Buff.prolong(defender, Terror.class, 1);
                break;
            case 7:
                Buff.prolong(defender, Vertigo.class, 1);
                break;
            case 8:
                Buff.prolong(defender, Weakness.class, 1);
                break;
            case 9:
                Buff.prolong(defender, Silence.class, 1);
                break;
            case 10:
                Buff.affect(defender, Hallucination.class).set(1);
                break;
            case 11:
                Buff.affect(defender, Oblivion.class, 1);
                break;
        }
        return super.proc(attacker, defender, damage);
    }
    public String statsInfo(){
        if (isIdentified()){
            return Messages.get(this, "stats_desc", 2+buffedLvl());
        } else {
            return Messages.get(this, "typical_stats_desc", 2);
        }
    }
}
