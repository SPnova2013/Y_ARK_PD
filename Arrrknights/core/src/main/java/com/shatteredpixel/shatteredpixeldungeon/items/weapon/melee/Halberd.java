package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SkillBook;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class Halberd extends MeleeWeapon {

    //how much skill book charge one order restores
    public static final int ABILITY_BOOK_CHARGE = 10;

    {
        image = ItemSpriteSheet.VANILLA_AXE;
        hitSound = Assets.Sounds.HIT_STAB;
        hitSoundPitch = 1f;

        tier = 2;
    }

    @Override
    public int max(int lvl) {
        return  4*(tier+1) +    //12 base, down from 15
                lvl*(tier+1);   //scaling unchanged
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        SkillBook book = hero.belongings.getItem(SkillBook.class);
        if (book == null) {
            GLog.w(Messages.get(this, "ability_no_book"));
            return;
        }

        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, null);

        book.SetCharge(ABILITY_BOOK_CHARGE);
        SpellSprite.show(hero, SpellSprite.CHARGE);

        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);

        afterAbilityUsed(hero);
        //no spendAndNext here on purpose, shouting the order costs no turn
    }

    @Override
    public String abilityInfo() {
        return Messages.get(this, "ability_desc", ABILITY_BOOK_CHARGE);
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (defender.properties().contains(Char.Property.INFECTED)) {
            damage += 4;
        }

        return super.proc(attacker, defender, damage);
    }
}
