package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.time.format.TextStyle;

public class KingOfNothing extends MeleeWeapon{
    {
        image = ItemSpriteSheet.KING_OF_NOTHING;
        hitSound = Assets.Sounds.HIT_STRONG;
        hitSoundPitch = 0.9f;

        tier = 4;
    }
    @Override
    public boolean doPickUp( Hero hero ) {
        if(Challenges.activeChallenges() >= 8){
            int lvl = this.level();

            ApocalypseAspirant n = new ApocalypseAspirant();

            n.enchantment = enchantment;
            n.curseInfusionBonus = curseInfusionBonus;
            n.levelKnown = levelKnown;
            n.cursedKnown = cursedKnown;
            n.cursed = cursed;
            n.augment = augment;
            n.chimeras = chimeras;
            n.level(lvl);

            this.detach(Dungeon.hero.belongings.backpack);
            if (!n.collect()) {
                Dungeon.level.drop(n, curUser.pos).sprite.drop();
            }
            hero.spendAndNext(TIME_TO_PICK_UP);
            return true;
        }
        return super.doPickUp( hero );
    }
}
