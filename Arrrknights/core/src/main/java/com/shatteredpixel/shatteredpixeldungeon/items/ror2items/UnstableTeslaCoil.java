package com.shatteredpixel.shatteredpixeldungeon.items.ror2items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.List;

public class UnstableTeslaCoil extends ROR2item{
    {
        tier = 3;
        image = ItemSpriteSheet.TESLA;
    }
    @Override
    protected ROR2itemBuff passiveBuff() {
        return new UnstableTeslaCoilBuff();
    }
    private boolean justEquip = true;
    @Override
    public boolean doEquip(Hero hero) {
        if (super.doEquip(hero)){
            justEquip = true;
            return true;
        } else {
            return false;
        }
    }
    public class UnstableTeslaCoilBuff extends ROR2itemBuff{
        @Override
        public boolean act() {
            Buff buff = Dungeon.hero.buff(TeslaCoilActive.class);
            if(justEquip){justEquip = false;}
            else if(buff!=null) buff.detach();
            else Buff.affect(Dungeon.hero, TeslaCoilActive.class);
            spend(TICK*10);
            return true;
        }
    }

    public static class TeslaCoilActive extends Buff {
        @Override
        public boolean act() {
            int count = 0;
            List<Mob> mobsCopy = new ArrayList<>(Dungeon.level.mobs);
            for (Mob mob : mobsCopy) {
                int p = mob.pos;
                if (Dungeon.level.distance(Dungeon.hero.pos, p) <= 3 && count < 3) {
                    Dungeon.hero.sprite.parent.add(new Lightning(Dungeon.hero.pos, p, null));
                    mob.damage(Math.round(Dungeon.hero.damageRoll()/3f), Reflection.newInstance(WandOfLightning.class));
                    count++;
                }
            }
            spend(TICK);
            return true;
        }
        @Override
        public void fx(boolean on) {
            if (on) target.sprite.add(CharSprite.State.ELECTRIC);
            else target.sprite.remove(CharSprite.State.ELECTRIC);
        }
    }
}
