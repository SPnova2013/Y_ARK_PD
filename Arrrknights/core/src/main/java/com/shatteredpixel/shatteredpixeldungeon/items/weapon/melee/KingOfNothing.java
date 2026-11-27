package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.JudgementRays;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;

public class KingOfNothing extends MeleeWeapon{
    public static final String AC_JUDGE = "JUDGE";

    //王之裁断: six rays, spun for a second
    public static final int RAY_RANGE = 6;          //in tiles
    public static final float DAMAGE_MULT = 1f;

    {
        image = ItemSpriteSheet.KING_OF_NOTHING;
        hitSound = Assets.Sounds.HIT_STRONG;
        hitSoundPitch = 0.9f;

        tier = 4;

        defaultAction = AC_JUDGE;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_JUDGE);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_JUDGE)) {
            if (!isEquipped(hero)) {
                return;
            }
            if (charge < chargeCap) {
                GLog.w(Messages.get(this, "no_charge"));
                return;
            }

            judge(hero);
        }
    }

    private void judge(Hero hero) {
        //charge = 0;
        updateQuickslot();

        final float baseAngle = Random.Float(360f);
        final int dmg = Math.round(damageRoll(hero) * DAMAGE_MULT);

        //the cuts reach the whole floor
        float length = (float) Math.hypot(Dungeon.level.width(), Dungeon.level.height())
                * DungeonTilemap.SIZE;

        JudgementRays rays = new JudgementRays(hero.sprite.center(), length, baseAngle);
        rays.onFinish = new Callback() {
            @Override
            public void call() {
                verdict(hero, baseAngle, dmg);
            }
        };
        hero.sprite.parent.add(rays);

        Sample.INSTANCE.play(Assets.Sounds.SKILL_SILVERSLASH);
        Invisibility.dispel();
    }

    //the verdict lands the moment the spin is over
    private void verdict(Hero hero, float baseAngle, int damage) {
        if (hero == null || Dungeon.level == null) {
            return;
        }

        GameScene.flash(0x40FFFFFF);
        Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG, 1f, 0.8f);

        int w = Dungeon.level.width();
        int h = Dungeon.level.height();
        int sx = hero.pos % w;
        int sy = hero.pos / w;
        int maxSteps = w + h;

        HashSet<Char> hitChars = new HashSet<>();
        HashSet<Integer> cells = new HashSet<>();

        for (int i = 0; i < JudgementRays.COUNT; i++) {
            double rad = Math.toRadians(baseAngle + i * 360f / JudgementRays.COUNT);
            float cos = (float) Math.cos(rad);
            float sin = (float) Math.sin(rad);

            cells.clear();

            //walk the line cell by cell, so the angle stays exact all the way out
            for (int step = 1; step <= maxSteps; step++) {
                int cx = sx + Math.round(cos * step);
                int cy = sy + Math.round(sin * step);
                if (cx < 0 || cx >= w || cy < 0 || cy >= h) {
                    break;
                }

                int cell = cy * w + cx;
                if (!cells.add(cell)) {
                    continue;
                }
                if (Dungeon.level.solid[cell]) {
                    break;
                }

                Char ch = Actor.findChar(cell);
                if (ch == null || ch == hero || !ch.isAlive()) {
                    continue;
                }
                if (ch.alignment != Char.Alignment.ENEMY) {
                    continue;
                }
                if (!hitChars.add(ch)) {
                    continue;
                }

                ch.damage(damage, hero);
                ch.sprite.flash();
                CellEmitter.center(cell).burst(PurpleParticle.BURST, 4);
            }
        }
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        SPCharge(4);
        updateQuickslot();
        return super.proc(attacker, defender, damage);
    }

    @Override
    public String status() {
        if (!isIdentified() || cursed) {
            return null;
        }
        if (chargeCap == 100) {
            return Messages.format("%d%%", charge);
        }
        return null;
    }
    @Override
    public boolean doPickUp( Hero hero ) {
        if(Challenges.activeChallenges() >= 8){
            int lvl = this.level();

            ApocalypseAspirant n = new ApocalypseAspirant();

            n.enchantment = enchantment;
            n.curseInfusionBonus = curseInfusionBonus;
            n.levelKnown = levelKnown;
            n.cursedKnown = cursedKnown;
            n.cursed = cursed;
            n.augment = augment;
            n.chimeras = chimeras;
            n.level(lvl);

            this.detach(Dungeon.hero.belongings.backpack);
            if (!n.collect()) {
                Dungeon.level.drop(n, curUser.pos).sprite.drop();
            }
            hero.spendAndNext(TIME_TO_PICK_UP);
            return true;
        }
        return super.doPickUp( hero );
    }
}
