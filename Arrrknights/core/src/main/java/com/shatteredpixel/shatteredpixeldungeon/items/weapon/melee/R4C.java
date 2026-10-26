package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.IsekaiItem;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

import java.util.ArrayList;

public class R4C extends GunWeapon {

    //how many tiles the shell flies before it detonates on its own
    public static final int ABILITY_RANGE = 4;
    //damage multiplier when it runs out of flight, and when it slams into a wall
    public static final int ABILITY_BLAST_MULT = 2;
    public static final int ABILITY_COLLISION_MULT = 4;
    //radius of the 3x3 blast, in tiles
    public static final int ABILITY_BLAST_RADIUS = 1;

    {
        image = ItemSpriteSheet.R4C;
        hitSound = Assets.Sounds.HIT_AR;
        hitSoundPitch = 0.9f;

        FIREACC = 1.6f;
        FIRETICK = 0.7f;
        bulletCap = 30;

        usesTargeting = true;

        defaultAction = AC_ZAP;

        tier = 5;
    }
    @Override
    protected void SPShot(Char ch) {
        Ballistica trajectory = new Ballistica(curUser.pos, ch.pos, Ballistica.STOP_TARGET);
        trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
        WandOfBlastWave.throwChar(ch, trajectory, 2); // 넉백 효과
    }
    protected int initialCap() {
        return 30;
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
    public String defaultAction() {
        return AC_ZAP;
    }
    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    public String abilityInfo() {
        return Messages.get(this, "ability_desc", shotmin(), shotmax());
    }

    @Override
    protected void duelistAbility(final Hero hero, Integer target) {
        if (target == null || target == hero.pos) {
            return;
        }

        Ballistica bolt = new Ballistica(hero.pos, target, Ballistica.WONT_STOP);
        if (bolt.path.size() < 2) {
            GLog.w(Messages.get(this, "bad_target"));
            return;
        }

        //work out where the shell comes down before spending anything
        int impact = hero.pos;
        boolean collided = false;
        for (int i = 1; i <= ABILITY_RANGE; i++) {
            if (i >= bolt.path.size()) {
                collided = true;
                break;
            }
            int cell = bolt.path.get(i);
            if (Dungeon.level.solid[cell]) {
                collided = true;
                break;
            }
            impact = cell;
            if (i == ABILITY_RANGE) {
                break;
            }
        }

        //the barrel is right against a wall, nothing to shoot at
        if (impact == hero.pos) {
            GLog.w(Messages.get(this, "no_room"));
            return;
        }

        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, null);

        final int landing = impact;
        final boolean hitWall = collided;

        hero.busy();
        hero.sprite.zap(landing);
        MagicMissile.boltFromChar(hero.sprite.parent, MagicMissile.FORCE, hero.sprite, landing, new Callback() {
            @Override
            public void call() {
                resolveShell(hero, bolt, landing, hitWall);
                Invisibility.dispel();
                afterAbilityUsed(hero);
                hero.spendAndNext(Actor.TICK);
            }
        });
    }

    private void resolveShell(Hero hero, Ballistica bolt, int landing, boolean hitWall) {

        //everything the shell passes through takes a hit and gets shoved back a tile
        ArrayList<Char> struck = new ArrayList<>();
        for (int i = 1; i <= ABILITY_RANGE && i < bolt.path.size(); i++) {
            int cell = bolt.path.get(i);
            if (Dungeon.level.solid[cell]) {
                break;
            }
            Char ch = Actor.findChar(cell);
            if (ch != null && ch != hero && ch.alignment == Char.Alignment.ENEMY && ch.isAlive()) {
                struck.add(ch);
            }
            if (i == ABILITY_RANGE) {
                break;
            }
        }

        //from the far end back, so each push resolves against the cells the previous one freed
        for (int i = struck.size() - 1; i >= 0; i--) {
            Char ch = struck.get(i);
            if (!ch.isAlive()) {
                continue;
            }
            ch.damage(ShotDamageRoll(), this);
            if (!ch.isAlive()) {
                continue;
            }
            Ballistica trajectory = new Ballistica(hero.pos, ch.pos, Ballistica.STOP_TARGET);
            trajectory = new Ballistica(trajectory.collisionPos,
                    trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
            WandOfBlastWave.throwChar(ch, trajectory, 1, true, true, true);
        }

        //detonation: a 3x3 burst around the impact point
        int dmg = ShotDamageRoll() * (hitWall ? ABILITY_COLLISION_MULT : ABILITY_BLAST_MULT);
        int w = Dungeon.level.width();
        int cx = landing % w;
        int cy = landing / w;

        for (int dy = -ABILITY_BLAST_RADIUS; dy <= ABILITY_BLAST_RADIUS; dy++) {
            for (int dx = -ABILITY_BLAST_RADIUS; dx <= ABILITY_BLAST_RADIUS; dx++) {
                int x = cx + dx;
                int y = cy + dy;
                if (x < 0 || x >= w || y < 0 || y >= Dungeon.level.height()) {
                    continue;
                }
                Char ch = Actor.findChar(y * w + x);
                if (ch == null || ch == hero || !ch.isAlive() || ch.alignment == Char.Alignment.ALLY) {
                    continue;
                }
                ch.damage(dmg, this);
            }
        }

        CellEmitter.get(landing).burst(BlastParticle.FACTORY, 16);
        Sample.INSTANCE.play(Assets.Sounds.BLAST);
        Camera.main.shake(2, 0.2f);
        GameScene.flash(hitWall ? 0x60FF8844 : 0x40FFCC66);
        Dungeon.observe();
        GameScene.updateFog();
    }

    @Override
    public String desc() {
       String info = Messages.get(this, "desc", bullettier);
            if (Dungeon.hero.belongings.getItem(IsekaiItem.class) != null) {
                if (Dungeon.hero.belongings.getItem(IsekaiItem.class).isEquipped(Dungeon.hero))
                    info += "\n\n" + Messages.get( R4C.class, "setbouns");}


        return info;
    }
}
