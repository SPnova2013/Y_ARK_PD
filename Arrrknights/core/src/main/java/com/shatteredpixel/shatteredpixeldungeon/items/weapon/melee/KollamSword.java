package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.AgniOfNila;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ChaliceOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMistress;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndAvatarOfMahasattva;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class KollamSword extends MeleeWeapon {
    public static final String AC_ZAP = "ZAP";

    //how far the fire of nila reaches along each cardinal direction
    public static final int CROSS_RANGE = 2;
    //hits of the five precepts
    public static final int PRECEPT_STRIKES = 5;
    //an avatar rite costs two weapon charges
    public static final int ABILITY_CHARGE_USE = 2;
    {
        image = ItemSpriteSheet.DONKEY_SWORD;
        hitSound = Assets.Sounds.HIT_SWORD2;
        hitSoundPitch = 1.03f;

        tier = 5;
        defaultAction = AC_ZAP;
    }

    @Override
    public int max(int lvl) {
        return  5*(tier+1) + // 30+6
                lvl*(tier+1);
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        SPCharge(2);
        return super.proc(attacker, defender, damage);
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        //for Irene the avatar takes the place of the old active skill
        if (hero.heroClass != HeroClass.IRENE) {
            actions.add(AC_ZAP);
        }
        return actions;
    }

    @Override
    protected int baseChargeUse(Hero hero, Char target) {
        return ABILITY_CHARGE_USE;
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        curUser = hero;
        curItem = this;

        GameScene.show(new WndAvatarOfMahasattva(this, hero, new WndAvatarOfMahasattva.Listener() {
            @Override
            public void onSelect(int index) {
                switch (index) {
                    case 0: breathOfNila(hero); break;
                    case 1: cleanseWithFire(hero); break;
                    case 2: fivePrecepts(hero); break;
                    case 3: mahasattvaDebate(hero); break;
                }
            }
        }));
    }

    @Override
    public String abilityInfo() {
        return Messages.get(this, "ability_desc", ABILITY_CHARGE_USE);
    }

    //呼吸法: set a fire of nila down at the chosen spot
    private void breathOfNila(final Hero hero) {
        GameScene.selectCell(new CellSelector.Listener() {
            @Override
            public void onSelect(Integer cell) {
                if (cell == null) {
                    return;
                }
                if (!Dungeon.level.heroFOV[cell] || Dungeon.level.solid[cell] || Actor.findChar(cell) != null) {
                    GLog.w(Messages.get(KollamSword.class, "bad_cell"));
                    return;
                }

                beforeAbilityUsed(hero, null);

                AgniOfNila.summon(cell);
                CellEmitter.get(cell).burst(ElmoParticle.FACTORY, 16);
                Sample.INSTANCE.play(Assets.Sounds.BURNING);
                Invisibility.dispel();

                afterAbilityUsed(hero);
                hero.spendAndNext(Actor.TICK);
            }

            @Override
            public String prompt() {
                return Messages.get(KollamSword.class, "prompt_breath");
            }
        });
    }

    //清火执: every fire answers with a cross of spell damage
    private void cleanseWithFire(Hero hero) {
        ArrayList<AgniOfNila> fires = AgniOfNila.allFires();
        if (fires.isEmpty()) {
            GLog.w(Messages.get(this, "no_fire"));
            return;
        }

        beforeAbilityUsed(hero, null);

        int dmg = hero.damageRoll();
        int w = Dungeon.level.width();

        for (AgniOfNila fire : fires) {
            for (int dir : new int[]{-1, +1, -w, +w}) {
                for (int step = 1; step <= CROSS_RANGE; step++) {
                    int cell = fire.pos + dir * step;
                    if (cell < 0 || cell >= Dungeon.level.length()) break;
                    //guard against stepping around the edge of a row
                    if (Dungeon.level.distance(fire.pos, cell) != step) break;
                    if (Dungeon.level.solid[cell]) break;

                    CellEmitter.get(cell).burst(ElmoParticle.FACTORY, 16);

                    Char ch = Actor.findChar(cell);
                    if (ch != null && ch != hero && ch.isAlive() && ch.alignment == Char.Alignment.ENEMY) {
                        ch.damage(dmg, new WandOfMagicMissile());
                    }
                }
            }
        }

        Sample.INSTANCE.play(Assets.Sounds.BURNING);
        Invisibility.dispel();

        afterAbilityUsed(hero);
        hero.spendAndNext(hero.attackDelay());
    }

    //惩五戒: close in on the target and strike five times
    private void fivePrecepts(final Hero hero) {
        GameScene.selectCell(new CellSelector.Listener() {
            @Override
            public void onSelect(Integer cell) {
                if (cell == null) {
                    return;
                }

                final Char enemy = Actor.findChar(cell);
                if (enemy == null || enemy == hero || !Dungeon.level.heroFOV[cell]
                        || hero.isCharmedBy(enemy) || enemy.alignment == Char.Alignment.ALLY) {
                    GLog.w(Messages.get(KollamSword.class, "bad_target"));
                    return;
                }

                if (hero.canAttack(enemy)) {
                    beforeAbilityUsed(hero, enemy);
                    hero.busy();
                    strikePrecepts(hero, enemy, PRECEPT_STRIKES);
                    return;
                }

                Ballistica path = new Ballistica(hero.pos, enemy.pos, Ballistica.PROJECTILE);
                if (path.collisionPos != enemy.pos) {
                    GLog.w(Messages.get(KollamSword.class, "bad_target"));
                    return;
                }
                final int leapPos = path.path.get(path.dist - 1);
                if (!Dungeon.level.passable[leapPos]) {
                    GLog.w(Messages.get(KollamSword.class, "bad_target"));
                    return;
                }

                beforeAbilityUsed(hero, enemy);
                hero.busy();

                hero.sprite.jump(hero.pos, leapPos, new Callback() {
                    @Override
                    public void call() {
                        hero.move(leapPos);
                        Dungeon.level.occupyCell(hero);
                        Dungeon.observe();
                        GameScene.updateFog();
                        strikePrecepts(hero, enemy, PRECEPT_STRIKES);
                    }
                });
            }

            @Override
            public String prompt() {
                return Messages.get(KollamSword.class, "prompt_precepts");
            }
        });
    }

    private void strikePrecepts(final Hero hero, final Char enemy, final int remaining) {
        if (remaining <= 0 || !enemy.isAlive()) {
            afterAbilityUsed(hero);
            hero.spendAndNext(hero.attackDelay());
            return;
        }

        hero.sprite.Sattack(enemy.pos, new Callback() {
            @Override
            public void call() {
                AttackIndicator.target(enemy);
                hero.attack(enemy, 1f, 0f, Char.INFINITE_ACCURACY);
                Invisibility.dispel();
                strikePrecepts(hero, enemy, remaining - 1);
            }
        });
    }

    //菩萨辩: the old corruption skill of this blade
    private void mahasattvaDebate(Hero hero) {
        beforeAbilityUsed(hero, null);

        MindBrack(hero);
        Sample.INSTANCE.play(Assets.Sounds.CURSED);
        Invisibility.dispel();

        afterAbilityUsed(hero);
        hero.spendAndNext(Actor.TICK);
    }

    @Override
    public String desc() {
        String info = Messages.get(this, "desc");
        if (setbouns()) info += "\n\n" + Messages.get( KollamSword.class, "setbouns");

        return info;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_ZAP) && isEquipped(hero)) {
            if (charge >= chargeCap) {
                MindBrack(hero);
                Sample.INSTANCE.play(Assets.Sounds.CURSED);
                charge = 0;

                hero.sprite.Sattack(0);
                updateQuickslot();
                curUser.spendAndNext(1f);
            }
        }
    }

    private void MindBrack(Hero hero) {
        PathFinder.buildDistanceMap(hero.pos, BArray.not(Dungeon.level.solid, null), 2);
        for (int cell = 0; cell < PathFinder.distance.length; cell++) {
            if (PathFinder.distance[cell] < Integer.MAX_VALUE) {
                CellEmitter.get( cell ).burst( ShadowParticle.CURSE, 10 );
                Char ch = Actor.findChar(cell);
                if (ch != null&& !(ch instanceof Hero) && ch instanceof Mob && ch.alignment != Char.Alignment.ALLY) {
                    if (!ch.isImmune(Corruption.class)) {
                        boolean chance = false;//change from budding
                        if (!setbouns() && Random.Int(2) != 0) chance = true;
                        else if (setbouns()) chance = true;

                        if (chance)Buff.affect(ch, Corruption.class);

                        boolean droppingLoot = ch.alignment != Char.Alignment.ALLY;

                        if (ch.buff(Corruption.class) != null){
                            if (droppingLoot) ((Mob)ch).rollToDropLoot();
                            Statistics.enemiesSlain++;
                            Badges.validateMonstersSlain();
                            Statistics.qualifiedForNoKilling = false;
                            if (((Mob)ch).EXP > 0 && curUser.lvl <= ((Mob)ch).maxLvl) {
                                curUser.sprite.showStatus(CharSprite.POSITIVE, Messages.get(((Mob)ch), "exp", ((Mob)ch).EXP));
                                curUser.earnExp(((Mob)ch).EXP, ((Mob)ch).getClass());
                            } else {
                                curUser.earnExp(0, ((Mob)ch).getClass());
                            }
                        }
                    }

                }}}
    }

    private boolean setbouns() {
        if (Dungeon.hero.belongings.getItem(RingOfMistress.class) != null && Dungeon.hero.belongings.getItem(ChaliceOfBlood.class) != null) {
            if (Dungeon.hero.belongings.getItem(RingOfMistress.class).isEquipped(Dungeon.hero) && Dungeon.hero.belongings.getItem(ChaliceOfBlood.class).isEquipped(Dungeon.hero))
                return true;
        }
        return false;
    }

    @Override
    public String status() {
        if (chargeCap == 100)
            return Messages.format("%d%%", charge);
        return null;
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{ShadowFirmament.class, Firmament.class, SwordofArtorius.class};
            inQuantity = new int[]{1, 1, 1};

            cost = 0;

            output = KollamSword.class;
            outQuantity = 1;
        }

    }
}
