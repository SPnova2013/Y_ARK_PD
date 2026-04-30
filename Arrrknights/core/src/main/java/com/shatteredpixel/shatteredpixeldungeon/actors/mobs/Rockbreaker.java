package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Camouflage;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silence;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DefenderSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.Rock_CrabSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RockbreakerSprite;
import com.watabou.utils.Random;

public class Rockbreaker extends Mob {
    {
        spriteClass = RockbreakerSprite.class;

        HP = HT = 200;
        damageMax = 60;
        damageMin = 45;
        drMax = 4;
        drMin = 0;
        defenseSkill = 0;

        maxLvl = 36;
        EXP = 20;
        immunities.add(Silence.class);
        resistances.addAll(AntiMagic.RESISTS);

        loot = Generator.Category.SKL_RND;
        lootChance = 0.2f;
    }
    @Override
    public int attackSkill( Char target ) {
        if (Dungeon.isChallenged(Challenges.TACTICAL_UPGRADE)) return 60+attackSkillIncRate*rounds;
        return 45+attackSkillIncRate*rounds; }
}
