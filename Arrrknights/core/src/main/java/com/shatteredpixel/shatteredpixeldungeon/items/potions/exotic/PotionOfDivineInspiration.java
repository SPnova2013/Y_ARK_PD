package com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.StatusPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentsPane;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndHero;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class PotionOfDivineInspiration extends ExoticPotion{

    {
        icon = ItemSpriteSheet.Icons.POTION_HOLYFUROR;
    }
//FIXME 有反馈称在某层天赋未满之前就喝下神意启发并选择该层，会导致该层天赋点的最后两点消失
    @Override
    //need to override drink so that time isn't spent right away
    protected void drink(final Hero hero) {
        curUser = hero;
        curItem = this;

        boolean[] enabled = new boolean[5];
        enabled[1] = enabled[2] = enabled[3] = enabled[4] = true;

        DivineInspirationTracker tracker = hero.buff(DivineInspirationTracker.class);

        if (tracker != null){
            boolean allBoosted = true;
            for (int i = 1; i <= 4; i++){
                if (tracker.isBoosted(i)){
                    enabled[i] = false;
                } else {
                    allBoosted = false;
                }
            }

            if (allBoosted){
                GLog.w(Messages.get(this, "no_more_points"));
                return;
            }
        }

        if (!isIdentified()) {
            curItem.detach(curUser.belongings.backpack);
        }

        GameScene.show(new WndOptions(
                new ItemSprite(this),
                Messages.titleCase(trueName()),
                Messages.get(PotionOfDivineInspiration.class, "select_tier"),
                Messages.titleCase(Messages.get(TalentsPane.class, "tier", 1)),
                Messages.titleCase(Messages.get(TalentsPane.class, "tier", 2)),
                Messages.titleCase(Messages.get(TalentsPane.class, "tier", 3)),
                Messages.titleCase(Messages.get(TalentsPane.class, "tier", 4))
        ){
            @Override
            protected boolean enabled(int index) {
                return (enabled[index+1] || Statistics.victoryLapRounds>0);
            }

            @Override
            protected void onSelect(int index) {
                super.onSelect(index);

                if (index != -1){
                    Buff.affect(curUser, DivineInspirationTracker.class).setBoosted(index+1);

                    if (isIdentified()) {
                        curItem.detach(curUser.belongings.backpack);
                    }

                    identify();
                    curUser.busy();
                    curUser.sprite.operate(curUser.pos);

                    curUser.spendAndNext(1f);

                    boolean unspentTalents = false;
                    for (int i = 1; i <= Dungeon.hero.talents.size(); i++){
                        if (Dungeon.hero.talentPointsAvailable(i) > 0){
                            unspentTalents = true;
                            break;
                        }
                    }
                    if (unspentTalents){
                        StatusPane.talentBlink = 10f;
                        WndHero.lastIdx = 1;
                    }

                    GameScene.showlevelUpStars();

                    Sample.INSTANCE.play( Assets.Sounds.DRINK );
                    Sample.INSTANCE.playDelayed(Assets.Sounds.LEVELUP, 0.3f, 0.7f, 1.2f);
                    Sample.INSTANCE.playDelayed(Assets.Sounds.LEVELUP, 0.6f, 0.7f, 1.2f);
                    new Flare( 6, 32 ).color(0xFFFF00, true).show( curUser.sprite, 2f );
                    GLog.p(Messages.get(PotionOfDivineInspiration.class, "bonus"));

                }
            }

            @Override
            public void onBackPressed() {
                //do nothing, prevents accidentally closing
            }
        });

    }

    public static class DivineInspirationTracker extends Buff {

        {
            type = buffType.POSITIVE;
        }

        private boolean[] boostedTiers = new boolean[5];
        private int[] tierBoostedCounts = new int[5];

        private static final String BOOSTED_TIERS = "boosted_tiers";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(BOOSTED_TIERS, boostedTiers);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            boostedTiers = bundle.getBooleanArray(BOOSTED_TIERS);
        }

        public void setBoosted( int tier ){
            boostedTiers[tier] = true;
            tierBoostedCounts[tier] += 2;
        }

        public boolean isBoosted( int tier ){
            return boostedTiers[tier];
        }
        public int boostedCounts( int tier ){
            return tierBoostedCounts[tier];
        }
    }
}
