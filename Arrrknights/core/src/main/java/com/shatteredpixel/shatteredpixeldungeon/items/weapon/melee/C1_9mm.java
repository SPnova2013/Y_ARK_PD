package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.WelcomeMat;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class C1_9mm extends GunWeapon {
    {
        image = ItemSpriteSheet.C1;
        hitSound = Assets.Sounds.HIT_GUN;
        hitSoundPitch = 0.9f;

        FIREACC = 1.2f;
        FIRETICK = 1f;
        bulletCap = 30;

        usesTargeting = true;

        defaultAction = AC_ZAP;

        tier = 3;
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
    //陷阱部署: lay a welcome mat for whoever walks by
    public static final float ABILITY_DAMAGE_MULT = 4f;

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    public String abilityInfo() {
        return Messages.get(this, "ability_desc",
                (int) (ABILITY_DAMAGE_MULT * 100), (int) WelcomeMat.PARALYSIS);
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        if (target == null) {
            return;
        }
        if (!Dungeon.level.heroFOV[target] || Dungeon.level.solid[target]
                || !Dungeon.level.passable[target]
                || Actor.findChar(target) != null
                || Dungeon.level.plants.get(target) != null) {
            GLog.w(Messages.get(this, "bad_target"));
            return;
        }

        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, null);

        //it is laid like a plant: a real tile, no terrain change, no pathing effect
        int dmg = Math.round(hero.damageRoll() * ABILITY_DAMAGE_MULT);
        WelcomeMat mat = new WelcomeMat().setDamage(dmg);
        mat.pos = target;
        Dungeon.level.plants.put(target, mat);
        GameScene.plantSeed(target);

        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.TRAP);
        Invisibility.dispel();

        afterAbilityUsed(hero);
        hero.spendAndNext(Actor.TICK);
    }
    @Override
    protected void SPShot(Char ch) {
        Buff.affect(ch, Silence.class, 3f);
    }
}
