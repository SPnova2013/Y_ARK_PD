package com.shatteredpixel.shatteredpixeldungeon.items;

import static com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent.MECHANICAL_SIGHT;
import static com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent.PREWAR;
import static com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent.PROFICIENCY;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MechanicalSight;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Enfild;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Enfild2;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.FreshInspiration;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gloves;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.GunWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Thunderbolt;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.UpMagazine;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;

import java.util.ArrayList;

import javax.xml.stream.events.EndDocument;

public class MidoriAccessories extends Item {
    private static final String AC_USE = "USE";
    private static final String AC_AIM = "AIM";
    {
        image = ItemSpriteSheet.NULL_DEF;

        stackable = true;
        bones = false;
        defaultAction = AC_USE;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_USE);
        if(Dungeon.hero.hasTalent(MECHANICAL_SIGHT)) actions.add(AC_AIM);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_USE)) {
            GameScene.selectItem(itemSelector, WndBag.Mode.WEAPON, Messages.get(this, "prompt"));
        }
        if (action.equals(AC_AIM)) {
            Buff.affect(Dungeon.hero, MechanicalSight.class).set();
            curItem.detach(Dungeon.hero.belongings.backpack);
        }
    }
    private final WndBag.Listener itemSelector = new WndBag.Listener() {
        @Override
        public void onSelect( final Item item ) {
            if (item != null) {
                if (item instanceof GunWeapon) {
                    curItem.detach(Dungeon.hero.belongings.backpack);
                    GunWeapon fi;
                    fi = (GunWeapon) item;
                    fi.addAccessories();
                    if(Dungeon.hero.hasTalent(PREWAR)&&Dungeon.hero.pointsInTalent(PREWAR)==2) Buff.affect(Dungeon.hero, Barrier.class).incShield(2);
                }else if(item instanceof MeleeWeapon && Dungeon.hero.hasTalent(PROFICIENCY)){
                    curItem.detach(Dungeon.hero.belongings.backpack);
                    ((MeleeWeapon)item).addAccessories();
                }else{
                    GLog.i(Messages.get(MidoriAccessories.class, "does_nothing"));
                }
                updateQuickslot();
            }
        }
    };
    @Override
    public boolean isUpgradable() {
        return false;
    }
    @Override
    public boolean isIdentified() {
        return true;
    }
}
