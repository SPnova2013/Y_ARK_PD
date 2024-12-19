package com.shatteredpixel.shatteredpixeldungeon.items.ror2items;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionHero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Reflection;

public class WakeOfVultures extends ROR2item{
    {
        tier = 3;
        image = ItemSpriteSheet.UNDONE_MARK;
    }

    public void VulturesUponKill(Char attacker, Char defender) {
        if(!defender.buffs(ChampionEnemy.class).isEmpty()){
            for(ChampionEnemy ceBuffs : defender.buffs(ChampionEnemy.class)){
                ChampionHero.getEliteByEnemy(((Hero)attacker), ceBuffs, 20f);
            }
        }
    }

    @Override
    protected ROR2itemBuff passiveBuff() {
        return new WakeOfVulturesBuff();
    }

    public class WakeOfVulturesBuff extends ROR2itemBuff{}
}
