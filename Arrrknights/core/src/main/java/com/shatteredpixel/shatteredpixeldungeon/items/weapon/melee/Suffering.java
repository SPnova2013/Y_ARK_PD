package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Bonk;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CustomeSet;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SP.StaffOfSkyfire;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Suffering extends MeleeWeapon {
    public static final String AC_ZAP = "ZAP";
    public static float partialcharge_suf=0;//change from budding
    {
        image = ItemSpriteSheet.FLAMMETTA;
        hitSound = Assets.Sounds.HIT_SHOTGUN;
        hitSoundPitch = 1f;

        tier =5;
        defaultAction = AC_ZAP;

        ACC = 0.9f;
    }

    protected int collisionProperties = Ballistica.MAGIC_BOLT;

    @Override
    public int max(int lvl) {
        return  4*(tier-1) - 2 +    //14 + 4 (one target = x2)
                lvl*(tier-1);
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (isEquipped(hero) && hero.heroClass != HeroClass.IRENE) actions.add(AC_ZAP);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_ZAP) && charge >= 100) {
            if (this.cursed != true) {
                cursedKnown = true;
                GameScene.selectCell(zapper);
            }
            else {
                Buff.affect(Dungeon.hero, Burning.class).reignite(Dungeon.hero,4f);
                cursedKnown = true;
                charge = 0;
            }
        }
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        int target = 1;
        if (attacker instanceof Hero) {
            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                if (Dungeon.level.adjacent(mob.pos, defender.pos) && mob.alignment != Char.Alignment.ALLY) {
                    mob.damage(Dungeon.hero.damageRoll() - Math.max(defender.drRoll(), defender.drRoll()), this);
                    target++;
                }
            }
        }

        if (target == 1) damage *= 2;

        return super.proc(attacker, defender, damage);
    }

    protected static CellSelector.Listener zapper = new CellSelector.Listener() {

        @Override
        public void onSelect(Integer target) {

            if (target != null) {

                final Suffering ss;
                if (curItem instanceof Suffering) {
                    ss = (Suffering) Suffering.curItem;

                    Ballistica shot = new Ballistica(curUser.pos, target, Ballistica.PROJECTILE);
                    int cell = shot.collisionPos;

                    if (target == curUser.pos || cell == curUser.pos) {
                        GLog.i(Messages.get(Suffering.class, "self_target"));
                        return;
                    }

                    curUser.sprite.zap(cell);

                    //attempts to target the cell aimed at if something is there, otherwise targets the collision pos.
                    if (Actor.findChar(target) != null)
                        QuickSlotButton.target(Actor.findChar(target));
                    else
                        QuickSlotButton.target(Actor.findChar(cell));

                    if (ss.tryToZap(curUser, target)) {
                        ss.fx(shot, new Callback() {
                            public void call() {
                                ss.onZap(shot);
                            }
                        });
                    }

                }
            }
        }

        @Override
        public String prompt() {
            return Messages.get(Suffering.class, "prompt");
        }
    };

    protected void fx( Ballistica bolt, Callback callback ) {
        MagicMissile.boltFromChar( curUser.sprite.parent,
                MagicMissile.MAGIC_MISSILE,
                curUser.sprite,
                bolt.collisionPos,
                callback);
        Sample.INSTANCE.play( Assets.Sounds.ZAP_GUN );
    }
    public boolean tryToZap(Hero owner, int target) {
        if (charge >= 100) {
            return true;
        } else {
            GLog.w(Messages.get(this, "fizzles"));
            return false;
        }
    }


    protected void onZap( Ballistica bolt ) {
        Sample.INSTANCE.play(Assets.Sounds.BURNING);
        StaffOfSkyfire.BlastWave.blast(bolt.collisionPos);
        int dmg = damageRoll(Dungeon.hero);
        dmg *= 1.25f;

        PathFinder.buildDistanceMap( bolt.collisionPos, BArray.not( Dungeon.level.solid, null ), 1 );
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                if (Dungeon.level.pit[i])
                    GameScene.add(Blob.seed(i, 1, Fire.class));
                else
                    GameScene.add(Blob.seed(i, 4, Fire.class));
                CellEmitter.get(i).burst(FlameParticle.FACTORY, 3);
            }
        }

        //throws other chars around the center.
        for (int i : PathFinder.NEIGHBOURS9) {
            if (!Dungeon.level.solid[bolt.collisionPos+i]) {
                GameScene.add(Blob.seed(bolt.collisionPos+i, 1+this.level(), Fire.class)); }
            Char ch = Actor.findChar(bolt.collisionPos + i);

            if (ch != null) {
                if (ch.alignment != Char.Alignment.ALLY) ch.damage(dmg, this);

                if (ch.isAlive() && ch.pos == bolt.collisionPos + i) {
                    Buff.affect(ch, Burning.class);
                } else if (ch == Dungeon.hero) {
                    Dungeon.fail(getClass());
                    GLog.n(Messages.get(this, "ondeath"));
                }
            }
        }

        //throws the char at the center of the blast
        Char ch = Actor.findChar(bolt.collisionPos);
        if (ch != null) {
            ch.damage(dmg, this);

            if (ch.isAlive() && bolt.path.size() > bolt.dist + 1 && ch.pos == bolt.collisionPos) {
                Buff.affect(ch, Burning.class);
            }
        }

        charge = 0;
        updateQuickslot();

        Buff buff = Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);
        if (buff != null) buff.detach();
        buff = Dungeon.hero.buff(Swiftthistle.TimeBubble.class);
        if (buff != null) buff.detach();
        //rabbittime在这里不取消的话会怎么样？

        if (Dungeon.hero.buff(Bonk.BonkBuff.class) != null) Buff.detach(Dungeon.hero, Bonk.BonkBuff.class);

        Invisibility.dispel();

        curUser.spendAndNext(1f);
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "ability_prompt");
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        if (target == null || target == hero.pos) {
            GLog.w(Messages.get(this, "self_target"));
            return;
        }

        Ballistica bolt = new Ballistica(hero.pos, target, Ballistica.PROJECTILE);
        if (bolt.path.size() < 2) {
            GLog.w(Messages.get(this, "self_target"));
            return;
        }

        final int landing = bolt.collisionPos;

        final int mainDmg = damageRoll(hero);
        final int scarDmg = Math.max(1, (int)Math.round(mainDmg * 0.5f));

        MeleeWeapon wep = this;
        wep.beforeAbilityUsed(hero, null);

        int scarCount = Math.min(4, bolt.dist - 1);
        for (int i = 1; i <= scarCount; i++) {
            int c = bolt.path.get(i);
            Dungeon.level.drop(new ScorchItem().ignite(c, scarDmg), c);
        }

        hero.sprite.zap(landing);
        MagicMissile.boltFromChar(hero.sprite.parent, MagicMissile.FIRE, hero.sprite, landing, new Callback() {
            @Override
            public void call() {
                Invisibility.dispel();
                mainExplode(landing, mainDmg);
                wep.afterAbilityUsed(hero);
            }
        });
        hero.spendAndNext(Actor.TICK);
    }

    @Override
    public void SPCharge(int value) {
        if(setbouns()) value *= 2;
        super.SPCharge(value);
    }

    @Override
    public String desc() {
        String info = Messages.get(this, "desc");
        if (setbouns()) info += "\n\n" + Messages.get( Suffering.class, "setbouns");

        return info;
    }

    @Override
    public String status() {
        if (Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.IRENE) {
            return super.status();
        }
        if (chargeCap == 100)
            return Messages.format("%d%%", charge);
        return null;
    }

    private boolean setbouns() {
        if (Dungeon.hero.belongings.getItem(TimekeepersHourglass.class) != null) {
            if (Dungeon.hero.belongings.getItem(TimekeepersHourglass.class).isEquipped(Dungeon.hero))
                return true;
        }
        return false;
    }

    private static void mainExplode(int center, int dmg) {
        PathFinder.buildDistanceMap(center, BArray.not(Dungeon.level.solid, null), 1);
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                if (!Dungeon.level.solid[i]) {
                    CellEmitter.get(i).burst(FlameParticle.FACTORY, 4);
                }
                Char ch = Actor.findChar(i);
                if (ch != null && ch.alignment != Char.Alignment.ALLY) {
                    ch.damage(dmg, Dungeon.hero);
                }
            }
        }
        Sample.INSTANCE.play(Assets.Sounds.BLAST);
    }

    private static void scarExplode(int cell, int dmg) {
        Heap heap = Dungeon.level.heaps.get(cell);
        if (heap != null) {
            for (Item i : heap.items.toArray(new Item[0])) {
                if (i instanceof ScorchItem) {
                    heap.remove(i);
                }
            }
        }
        for (int i : PathFinder.NEIGHBOURS4) {
            int c = cell + i;
            if (!Dungeon.level.insideMap(c)) continue;
            if (!Dungeon.level.solid[c]) {
                CellEmitter.get(c).burst(FlameParticle.FACTORY, 2);
            }
            Char ch = Actor.findChar(c);
            if (ch != null && ch.alignment != Char.Alignment.ALLY) {
                ch.damage(dmg, Dungeon.hero);
            }
        }
        Sample.INSTANCE.play(Assets.Sounds.BURNING);
    }

    //挂在关卡上的引爆者，随灼痕物品一起被存档，玩家离开并返回该层后仍会正常引爆
    public static class Scorch extends Actor {

        {
            actPriority = BLOB_PRIO + 1; //after hero, before other actors
        }

        public int cell = -1;
        public int dmg = 0;

        @Override
        protected boolean act() {
            scarExplode(cell, dmg);
            diactivate();
            Actor.remove(this);
            return true;
        }

        private static final String CELL = "cell";
        private static final String DMG = "dmg";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(CELL, cell);
            bundle.put(DMG, dmg);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            cell = bundle.getInt(CELL);
            dmg = bundle.getInt(DMG);
        }
    }

    public static class ScorchItem extends Item {

        public Scorch scorch;

        {
            dropsDownHeap = true;
            unique = true;
            image = ItemSpriteSheet.UNDONE_MARK;
        }

        //下一回合引爆，其余逻辑与炸弹的引信一致
        public ScorchItem ignite(int cell, int dmg) {
            scorch = new Scorch();
            scorch.cell = cell;
            scorch.dmg = dmg;
            Actor.addDelayed(scorch, 1);
            return this;
        }

        @Override
        public boolean doPickUp(Hero hero) {
            GLog.w(Messages.get(this, "cant_pickup"));
            return false;
        }

        @Override
        public Emitter emitter() {
            Emitter emitter = new Emitter();
            emitter.pos(7.5f, 3.5f);
            emitter.fillTarget = false;
            emitter.pour(FlameParticle.FACTORY, 0.05f);
            return emitter;
        }

        private static final String SCORCH = "scorch";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(SCORCH, scorch);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            if (bundle.contains(SCORCH)) {
                Actor.add(scorch = (Scorch) bundle.get(SCORCH));
            }
        }
    }
}
