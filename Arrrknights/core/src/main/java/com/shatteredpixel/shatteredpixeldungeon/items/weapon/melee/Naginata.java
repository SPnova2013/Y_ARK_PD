package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.effects.coversprite.CleansingEvilCover;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SkillBook;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class Naginata extends MeleeWeapon {

    //the sweep reaches this far along each of the four cardinal directions
    public static final int ABILITY_RANGE = 2;
    //skill book charge restored for every enemy the sweep kills
    public static final int ABILITY_BOOK_CHARGE = 2;

    {
        image = ItemSpriteSheet.NAGINATA;
        hitSound = Assets.Sounds.HIT_SPEAR;
        hitSoundPitch = 1.18f;

        tier = 4;
        DLY = 1.25f;
        RCH = 2;

        bones = false;
    }

    @Override
    public int max(int lvl) {
        if (Dungeon.hero.belongings.getItem(HornOfPlenty.class) != null) {
            if (Dungeon.hero.belongings.getItem(HornOfPlenty.class).isEquipped(Dungeon.hero)) {
              return 5*(tier) + // 20 + 6
                     lvl*(tier+2);
            }}
        return  5*(tier) +   //20 + 5
                lvl*(tier+1);
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, null);

        //one sweep, one roll, applied to everything caught in the four arms of the cross
        int dmg = damageRoll(hero);
        int w = Dungeon.level.width();
        SkillBook book = hero.belongings.getItem(SkillBook.class);

        for (int dir : new int[]{-1, +1, -w, +w}) {
            for (int step = 1; step <= ABILITY_RANGE; step++) {
                int cell = hero.pos + dir * step;
                if (cell < 0 || cell >= Dungeon.level.length()) break;
                //guard against stepping around the edge of a row
                if (Dungeon.level.distance(hero.pos, cell) != step) break;
                if (Dungeon.level.solid[cell]) break;

                Char ch = Actor.findChar(cell);
                if (ch == null || ch == hero || !ch.isAlive()
                        || ch.alignment != Char.Alignment.ENEMY) {
                    continue;
                }

                ch.damage(dmg, hero);

                if (!ch.isAlive() && book != null) {
                    book.SetCharge(ABILITY_BOOK_CHARGE);
                    SpellSprite.show(hero, SpellSprite.CHARGE);
                }
            }
        }

        new CleansingEvilCover().centerAndPlay(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.SKILL_SILVERSLASH);
        Invisibility.dispel();

        afterAbilityUsed(hero);
        hero.spendAndNext(Actor.TICK);
    }

    @Override
    public String abilityInfo() {
        if (levelKnown) {
            return Messages.get(this, "ability_desc",
                    augment.damageFactor(min()),
                    augment.damageFactor(max()));
        } else {
            return Messages.get(this, "typical_ability_desc", min(0), max(0));
        }
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        return super.proc(attacker, defender, damage);
    }
    public static class naginataSkill{}
    @Override
    public String desc() {
        String info = Messages.get(this, "desc");
        if (Dungeon.hero.belongings.getItem(HornOfPlenty.class) != null) {
            if (Dungeon.hero.belongings.getItem(HornOfPlenty.class).isEquipped(Dungeon.hero))
                info += "\n\n" + Messages.get( Naginata.class, "setbouns");}

        return info;
    }
}
