package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.Comparator;

public class MetallicUnion extends MeleeWeapon {
    public static final String AC_CHANGE = "CHANGE";
    {
        image = ItemSpriteSheet.PANDA;
        hitSound = Assets.Sounds.HIT_PUNCH;
        hitSoundPitch = 0.9f;

        defaultAction = AC_CHANGE;

        tier = 4;
        ACC = 0.95f;
    }

    private boolean swiching = true;
    private int AttackCount = 0;

    //how many tiles the shockwave throws its victims
    public static final int ABILITY_POWER = 2;
    //cripple applied to everything caught in the blast
    public static final float ABILITY_CRIPPLE = 3f;

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        if (target == null) {
            return;
        }

        if (target == hero.pos || !Dungeon.level.heroFOV[target]) {
            GLog.w(Messages.get(this, "ability_target_range"));
            return;
        }

        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, null);

        int w = Dungeon.level.width();
        int tx = target % w;
        int ty = target / w;

        //everything hostile in the 3x3 around the impact point
        ArrayList<Char> victims = new ArrayList<>();
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int x = tx + dx;
                int y = ty + dy;
                if (x < 0 || x >= w || y < 0 || y >= Dungeon.level.height()) {
                    continue;
                }
                Char ch = Actor.findChar(y * w + x);
                if (ch != null && ch.isAlive() && ch.alignment == Char.Alignment.ENEMY) {
                    victims.add(ch);
                }
            }
        }

        //farthest from the impact first, so each one is thrown into the space the next left
        victims.sort(new Comparator<Char>() {
            @Override
            public int compare(Char a, Char b) {
                return Dungeon.level.distance(b.pos, target) - Dungeon.level.distance(a.pos, target);
            }
        });

        for (Char ch : victims) {
            Buff.affect(ch, Cripple.class, ABILITY_CRIPPLE);

            int dirX = Integer.signum((ch.pos % w) - tx);
            int dirY = Integer.signum((ch.pos / w) - ty);
            if (dirX == 0 && dirY == 0) {
                //standing on the impact point: shove it away from the hero instead
                dirX = Integer.signum(tx - (hero.pos % w));
                dirY = Integer.signum(ty - (hero.pos / w));
            }
            if (dirX == 0 && dirY == 0) {
                continue;
            }

            int ex = Math.max(0, Math.min(w - 1, (ch.pos % w) + ABILITY_POWER * dirX));
            int ey = Math.max(0, Math.min(Dungeon.level.height() - 1, (ch.pos / w) + ABILITY_POWER * dirY));

            Ballistica trajectory = new Ballistica(ch.pos, ey * w + ex, Ballistica.MAGIC_BOLT);
            WandOfBlastWave.throwChar(ch, trajectory, ABILITY_POWER, false, true, true);
        }

        WandOfBlastWave.BlastWave.blast(target);
        Sample.INSTANCE.play(Assets.Sounds.BLAST);
        Invisibility.dispel();

        afterAbilityUsed(hero);
        hero.spendAndNext(Actor.TICK);
    }
    @Override
    public String statusBottom() {
        if (!isIdentified() || cursed) return null;
        if (isEquipped(Dungeon.hero)
                && Dungeon.hero.buff(Charger.class) != null) {
            Charger buff = Dungeon.hero.buff(Charger.class);
            return String.valueOf(buff.charges);
        } else {
            return super.status();
        }
    }

    @Override
    public int max(int lvl) {
        if (!swiching)
            return  5*(tier+1) +                	// 25 + 6
                    lvl*(tier+2);
        else return  2*(tier) +                	// 8 + 2
                lvl*(tier-2);
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_CHANGE);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_CHANGE)) {
            if (swiching == true) {
                swiching = false;
                AttackCount = 0;
                GLog.w(Messages.get(this, "modechange_nm"));
                updateQuickslot();
                curUser.spendAndNext(1f);
            }
        }
    }


    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (swiching) {
            Ballistica trajectory = new Ballistica(attacker.pos, defender.pos, Ballistica.STOP_TARGET);
            trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
            WandOfBlastWave.throwChar(defender, trajectory, 1); // 넉백 효과
        }

        AttackCount++;
        if (attacker instanceof Hero) {
            if (AttackCount >= 10) {
                if (swiching) {
                    swiching = false;
                    GLog.w(Messages.get(this, "modechange_nm"));
                    AttackCount = 0;
                    updateQuickslot();
                } else {
                    swiching = true;
                    GLog.w(Messages.get(this, "modechange_ex"));
                    AttackCount = 0;
                    updateQuickslot();
                }
            }
        }

        return super.proc(attacker, defender, damage);
    }

    @Override
    public String desc() {
        if (swiching) return Messages.get(this, "desc_mode2");
        else return Messages.get(this, "desc_mode1");
    }

    public String statsInfo(){
        return Messages.get(this, "stats_desc");
    }

    @Override
    public String status() {
        if (this.isIdentified()) {
            if (swiching) return "EX";
            else return "NM";
        }
        else return null;}

    private static final String SWICH = "swiching";
    private static final String COUNT = "AttackCount";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SWICH, swiching);
        bundle.put(COUNT, AttackCount);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        swiching = bundle.getBoolean(SWICH);
        AttackCount = bundle.getInt(COUNT);
    }
}
