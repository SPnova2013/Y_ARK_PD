package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfTenacity;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.audio.Sample;

public class FolkSong extends MeleeWeapon {

    //肉斩骨断: how long the hero refuses to fall, and how long they are paralysed afterwards
    public static final int ABILITY_DURATION = 10;
    public static final float ABILITY_PARALYSIS = 5f;

    {
        image = ItemSpriteSheet.SPECTER;
        hitSound = Assets.Sounds.HIT_CHAINSAW2;
        hitSoundPitch = 1f;

        tier = 4;
    }

    //a free action: the song simply starts playing
    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, null);

        Buff.prolong(hero, FleshAndBone.class, ABILITY_DURATION);

        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
        Invisibility.dispel();

        afterAbilityUsed(hero);
        //no spendAndNext here on purpose, starting the song costs no turn
    }

    @Override
    public String abilityInfo() {
        return Messages.get(this, "ability_desc", ABILITY_DURATION, (int) ABILITY_PARALYSIS);
    }

    //肉斩骨断: while it plays the hero cannot be brought below 1 hp, and the hangover paralyses
    public static class FleshAndBone extends FlavourBuff {

        {
            type = buffType.POSITIVE;
            announced = true;
        }

        @Override
        public int icon() {
            return BuffIndicator.ARMOR;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (ABILITY_DURATION - visualcooldown()) / (float) ABILITY_DURATION);
        }

        @Override
        public void detach() {
            super.detach();
            if (target != null && target.isAlive()) {
                Buff.affect(target, Paralysis.class, ABILITY_PARALYSIS);
            }
        }
    }

    @Override
    public int max(int lvl) {
        return  5*(tier) + 2 +  // 22 + 4
                lvl*(tier);
    }


    @Override
    public int proc(Char attacker, Char defender, int damage) {
        float dmgbouns = 1f;
        if (Dungeon.level.map[attacker.pos] == Terrain.WATER) {
            dmgbouns += 0.15f;
        }
        if (Dungeon.level.map[defender.pos] == Terrain.WATER) {
            dmgbouns += 0.15f;
        }

        damage *= dmgbouns;
        return super.proc(attacker, defender, damage);
    }

    @Override
    public String desc() {
        String info = Messages.get(this, "desc");
        if (Dungeon.hero.belongings.getItem(RingOfTenacity.class) != null) {
            if (Dungeon.hero.belongings.getItem(RingOfTenacity.class).isEquipped(Dungeon.hero))
                info += "\n\n" + Messages.get( FolkSong.class, "setbouns");}

        return info;
    }
}
