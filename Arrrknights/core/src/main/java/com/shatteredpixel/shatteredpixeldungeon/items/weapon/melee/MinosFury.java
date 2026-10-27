package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Nullshield;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class MinosFury extends MeleeWeapon {
    public static final String AC_EAT = "EAT";

    {
        image = ItemSpriteSheet.VULCAN;
        tier = 5;

        bones = false;
    }

    private int drbouns = 0;

    //guard mode lasts ABILITY_DURATION_BASE + weaponLevel/3 turns
    public static final int ABILITY_DURATION_BASE = 5;
    //defense is raised by this fraction, so the guard becomes 200% of its normal value
    public static final float ABILITY_DEFENSE_BONUS = 1f;
    //fraction of max health restored every turn
    public static final float ABILITY_REGEN_FRACTION = 0.02f;

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, null);

        GuardMode mode = Buff.affect(hero, GuardMode.class);
        mode.refresh(ABILITY_DURATION_BASE + buffedLvl()/3);

        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.SKILL_BASIC);

        afterAbilityUsed(hero);
        //no spendAndNext here on purpose, bracing costs no turn
    }

    @Override
    public String abilityInfo() {
        return Messages.get(this, "ability_desc", ABILITY_DURATION_BASE + buffedLvl()/3);
    }

    @Override
    public int max(int lvl) {
        return  4*(tier) +     //20 + 5
                lvl*(tier);                   //+3 per level, down from +6
    }

    @Override
    public int defenseFactor( Char owner ) {
        return 0+drbouns;
    }

    public String statsInfo(){
        if (isIdentified()){
            return Messages.get(this, "stats_desc", 0+drbouns);
        } else {
            return Messages.get(this, "typical_stats_desc", 0);
        }
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_EAT);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_EAT)) {
            GameScene.selectItem(itemSelector, WndBag.Mode.WEAPON, Messages.get(Nullshield.class, "prompt"));
        }
    }

    private final WndBag.Listener itemSelector = new WndBag.Listener()  {
        @Override
        public void onSelect(final Item item) {
            Item mi=Dungeon.hero.belongings.getItem(MinosFury.class);//change from budding
            if (item != null && !item.isEquipped(Dungeon.hero) && drbouns < 20 && item !=mi) {
                drbouns += 2;
                item.detach(Dungeon.hero.belongings.backpack);
            }
        }
    };

    //bracing behind the pick: harder to hurt, and the wounds knit slowly
    public static class GuardMode extends Buff {

        {
            type = buffType.POSITIVE;
            announced = true;
        }

        private float left = 0f;

        //sets the remaining time, using it again refreshes rather than extends
        public void refresh(float duration) {
            left = duration;
        }

        @Override
        public boolean act() {

            int heal = Math.max(1, Math.round(target.HT * ABILITY_REGEN_FRACTION));
            if (target.HP < target.HT) {
                target.HP = Math.min(target.HP + heal, target.HT);
                if (target.sprite != null) {
                    target.sprite.showStatus(CharSprite.POSITIVE, "+%dHP", heal);
                }
            }

            left -= TICK;
            if (left <= 0f) {
                detach();
            } else {
                spend(TICK);
            }
            return true;
        }

        @Override
        public int icon() {
            return BuffIndicator.ARMOR;
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", dispTurns(left));
        }

        private static final String LEFT = "left";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(LEFT, left);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            left = bundle.getFloat(LEFT);
        }
    }

    private static final String DR = "drbouns";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(DR, drbouns);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        drbouns = bundle.getInt(DR);
    }
}
