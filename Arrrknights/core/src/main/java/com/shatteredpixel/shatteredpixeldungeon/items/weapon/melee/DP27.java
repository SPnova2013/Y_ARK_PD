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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class DP27 extends GunWeapon {
    {
        image = ItemSpriteSheet.DP27;
        hitSound = Assets.Sounds.HIT_GUN;
        hitSoundPitch = 0.9f;

        FIREACC = 1.5f;
        FIRETICK = 1f;
        bulletCap = 30;

        usesTargeting = true;

        defaultAction = AC_ZAP;
        tier = 3;
    }

    //燃烧榴弹: one incendiary shell lobbed at the chosen spot
    public static final float ABILITY_DAMAGE_MULT = 3f;
    public static final int ABILITY_BLAST_RADIUS = 1;   //a 3x3 burst
    public static final int ABILITY_FIRE_TURNS = 6;

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
        return Messages.get(this, "ability_desc",
                (int) (ABILITY_DAMAGE_MULT * 100), ABILITY_FIRE_TURNS);
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
    protected void duelistAbility(final Hero hero, Integer target) {
        if (target == null || target == hero.pos) {
            return;
        }

        Ballistica bolt = new Ballistica(hero.pos, target, Ballistica.STOP_TARGET);
        final int landing = bolt.collisionPos;
        if (landing == hero.pos) {
            GLog.w(Messages.get(this, "bad_target"));
            return;
        }

        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, null);

        hero.busy();
        hero.sprite.zap(landing);
        MagicMissile.boltFromChar(hero.sprite.parent, MagicMissile.GUN_SHOT, hero.sprite, landing, new Callback() {
            @Override
            public void call() {
                detonate(hero, landing);
                Invisibility.dispel();
                afterAbilityUsed(hero);
                hero.spendAndNext(Actor.TICK);
            }
        });
        Sample.INSTANCE.play(Assets.Sounds.ZAP_GUN);
    }

    private void detonate(Hero hero, int landing) {
        int dmg = Math.round(ShotDamageRoll() * ABILITY_DAMAGE_MULT);

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
                int cell = y * w + x;

                Char ch = Actor.findChar(cell);
                if (ch != null && ch != hero && ch.isAlive() && ch.alignment != Char.Alignment.ALLY) {
                    ch.damage(dmg, this);
                }

                //the ground itself keeps burning for a while
                if (!Dungeon.level.solid[cell]) {
                    GameScene.add(Blob.seed(cell, ABILITY_FIRE_TURNS, Fire.class));
                    CellEmitter.get(cell).burst(FlameParticle.FACTORY, 3);
                }
            }
        }

        CellEmitter.get(landing).burst(BlastParticle.FACTORY, 16);
        Sample.INSTANCE.play(Assets.Sounds.BLAST);
        Camera.main.shake(2, 0.2f);
        GameScene.flash(0x40FFCC66);
        Dungeon.observe();
        GameScene.updateFog();
    }

    @Override
    protected void SPShot(Char ch) {
        Buff.affect(ch, Burning.class).reignite(ch);
    }
    protected int initialCap() {
        return 30;
    }
    @Override
    protected void fx(Ballistica bolt, Callback callback ) {
        int a = 0;
        if (spshot) a = 1;
        MagicMissile.boltFromChar( curUser.sprite.parent,
                MagicMissile.GUN_SHOT+a,
                curUser.sprite,
                bolt.collisionPos,
                callback);
        Sample.INSTANCE.play( Assets.Sounds.ZAP_GUN );
    }
}
