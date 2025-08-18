package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class Murakumo extends MeleeWeapon{
    public static final String AC_GAWAIN = "GAWAIN";
    {
        image = ItemSpriteSheet.MURAKUMO;
        hitSound = Assets.Sounds.HIT_KNIFE;
        hitSoundPitch = 0.9f;

        tier = 5;
        usesTargeting = true;
        defaultAction = AC_GAWAIN;
    }
    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_GAWAIN);
        return actions;
    }
    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_GAWAIN)) {
            if(Dungeon.hero.belongings.weapon != this) {
                GLog.i(Messages.get(this, "need_to_equip"));
                return;
            }
            GameScene.selectCell(gawain);
        }
    }

    protected static CellSelector.Listener gawain = new CellSelector.Listener() {

        @Override
        public void onSelect(Integer target) {
            if (target == null) { return;}
            if (hero.rooted){
                Camera.main.shake( 1, 1f );
                return;
            }
            if(Dungeon.level.distance(hero.pos, target) > 1){
                GLog.w(Messages.get(Murakumo.class, "cant_reach"));
                return;
            }

            Char enemy = Actor.findChar(target);
            if(enemy != null) target = hero.pos;
            final int dest = target;

            hero.busy();
            hero.sprite.jump(hero.pos, dest, new Callback() {
                @Override
                public void call() {
                    hero.move(dest);
                    Dungeon.level.occupyCell(hero);
                    Dungeon.observe();
                    GameScene.updateFog();

                    for (int i : PathFinder.NEIGHBOURS8) {
                        Char mob = Actor.findChar(hero.pos + i);
                        if (mob != null && mob != hero && mob.alignment != Char.Alignment.ALLY) {
                            int damage = Math.round(hero.belongings.weapon.damageRoll(hero)*0.5f);
                            damage -= mob.drRoll();
                            mob.damage(damage, hero);
                        }
                    }

                    Camera.main.shake(2, 0.5f);

                    Invisibility.dispel();
                    hero.spendAndNext(hero.attackDelay());

                }
            });
        }

        @Override
        public String prompt() {
            return Messages.get(MidnightSword.class, "prompt");
        }
    };
}
