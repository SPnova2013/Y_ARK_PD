package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Twilight;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SkillBook;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.Aegis;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Flag extends MeleeWeapon  {
    public static final String AC_ZAP = "ZAP";
    {
        image = ItemSpriteSheet.FLAG;
        hitSound = Assets.Sounds.HIT;
        hitSoundPitch = 1f;

        defaultAction = AC_ZAP;

        tier = 3;
    }

    private int backchane = 0;

    @Override
    public int max(int lvl) {
        return  3*(tier+2) +   //15 + 3
                lvl*(tier); }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        //for Irene the ability takes the place of the old button
        if (hero.heroClass != HeroClass.IRENE) {
            actions.add(AC_ZAP);
        }
        return actions;
    }

    //the ability runs on the normal duelist charge: it builds up, banks several uses
    //and refills over time. only the old button needs a completely full weapon charge.
    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, null);

        healingBurst(hero);
        updateQuickslot();

        afterAbilityUsed(hero);
        //no spendAndNext here on purpose, spreading the wings costs no turn
    }

    @Override
    public String abilityInfo() {
        return Messages.get(this, "ability_desc", 10, 5 + level() * 2);
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_ZAP)) {
            healingWings(hero);
        }

        updateQuickslot();
    }

    //the old button: the whole weapon charge goes into one burst of healing
    private boolean healingWings(Hero hero) {
        if (hero.belongings.weapon != this || charge < chargeCap) {
            return false;
        }

        healingBurst(hero);
        charge = 0;
        return true;
    }

    //治愈之翼: the healing itself, shared by the ability and the old button
    private void healingBurst(Hero hero) {
        hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), 2 + buffedLvl() / 2);

        SkillBook book = hero.belongings.getItem(SkillBook.class);
        if (book != null) {
            book.SetCharge(5 + level() * 2);
        }
        int heal = hero.HT / 10;
        if (hero.buff(Aegis.AegisBuff.class) != null) {
            Aegis.addShield(heal);
        }
        hero.HP = Math.min(hero.HP + heal, hero.HT);
        hero.sprite.showStatus(CharSprite.POSITIVE, "+%dHP", heal);
        Sample.INSTANCE.play(Assets.Sounds.SKILL_BASIC);
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (Random.Int(15) < 2 + backchane) {
            Ballistica trajectory = new Ballistica(attacker/*curUser*/.pos, defender.pos, Ballistica.STOP_TARGET);//change from budding
            trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
            WandOfBlastWave.throwChar(defender, trajectory, 2+(level() / 5)); // 넉백 효과
            backchane = 0;
        }
        else backchane++;

        SPCharge(4);
        updateQuickslot();

        return super.proc(attacker, defender, damage);
    }

    @Override
    public String status() {

        if (Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.IRENE) {
            return super.status();
        }
        //if the artifact isn't IDed, or is cursed, don't display anything
        if (!isIdentified() || cursed) {
            return null;
        }
        //display as percent
        if (chargeCap == 100)
            return Messages.format("%d%%", charge);


        //otherwise, if there's no charge, return null.
        return null;
    }
}
