package com.shatteredpixel.shatteredpixeldungeon.items.Skill;

import static com.shatteredpixel.shatteredpixeldungeon.Challenges.TEST;
import static com.shatteredpixel.shatteredpixeldungeon.items.ror2items.LightFluxPauldron.LFPChargeMultiplier;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.NervousImpairment;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.NewGameItem.Certificate;
import com.shatteredpixel.shatteredpixeldungeon.items.Pombbay;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SK2.Nervous;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSunLight;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.SP.Badge;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.LightFluxPauldron;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.utils.Bundle;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SkillBook extends Item {
    {
        image = ItemSpriteSheet.SKILL_BOOK;
        stackable = false;
        bones = false;
        unique = true;
        defaultAction = AC_ACT;
    }

    private static final String AC_ACT = "ACT";
    public int charge = 30;
    public int chargeCap = 150;

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_ACT);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_ACT)) {

            GameScene.show(
                    new WndOptions(Messages.get(this, "name"),
                            Messages.get(this, "wnddesc") + infoWnd(),
                            Messages.get(this, "ac_skl1", new DecimalFormat("#").format(30f)),
                            Messages.get(this, "ac_skl2", new DecimalFormat("#").format(60f)),
                            Messages.get(this, "ac_skl3", new DecimalFormat("#").format(100f))) {

                        @Override
                        protected void onSelect(int index) {
                            int energy_cost[] = {30,60,100};
                            int lowest_cost[] ={5,10,15};
                            float real_cost=energy_cost[index]/(RingOfSunLight.SPBonus(Dungeon.hero) + Badge.SunlightMultiper()-1);
                            if(Dungeon.level.feeling == Level.Feeling.CLOUDY) {
                                real_cost-=30;
                                Dungeon.level.feeling = Level.Feeling.NONE;
                            }
                            if (real_cost<lowest_cost[index]) real_cost=lowest_cost[index];
                            if ((index==0 && hero.SK1==null) || (index==1 && hero.SK2==null) || (index==2 && hero.SK3==null)){
                                GLog.w(Messages.get(SkillBook.class, "no_skill"));
                            }else{
                                if (charge < real_cost && !Dungeon.isChallenged(TEST)){//change from budding
                                    GLog.w(Messages.get(SkillBook.class, "low_charge"));
                                }
                                else {
                                    if(hero.SK1!=null)hero.SK1.activatedBySkillbook(true);
                                    if(hero.SK2!=null)hero.SK2.activatedBySkillbook(true);
                                    if(hero.SK3!=null)hero.SK3.activatedBySkillbook(true);
                                    if (!(Dungeon.isChallenged(TEST))){charge-=real_cost;}
                                    updateQuickslot();
                                    switch (index){
                                        case 0:hero.SK1.doSkill();break;
                                        case 1:hero.SK2.doSkill();break;
                                        case 2:hero.SK3.doSkill();break;
                                    }
                                    Talent.onSkillUsed(Dungeon.hero);
                                }
                            }
                        }
                    });
        }
    }

    @Override
    public String info() {
        String info = desc();

        curUser = Dungeon.hero;

        info += "\n\n" + Messages.get(this, "spcharge", Math.round(charge));

        if (curUser.SK1 != null) {
            info += "\n\n" + curUser.SK1.name();
            info += " " + curUser.SK1.desc();
        }

        if (curUser.SK2 != null) {
            info += "\n\n" + curUser.SK2.name();
            info += " " + curUser.SK2.desc();
        }

        if (curUser.SK3 != null) {
            info += "\n\n" + curUser.SK3.name();
            info += " " + curUser.SK3.desc();
        }

        return info;
    }


    public String infoWnd() {
        String infoWnd = "";
        if (curUser.SK1 != null) {
            infoWnd += "\n\n" + curUser.SK1.name();
            infoWnd += " " + curUser.SK1.desc_wnd();
        }

        if (curUser.SK2 != null) {
            infoWnd += "\n\n" + curUser.SK2.name();
            infoWnd += " " + curUser.SK2.desc_wnd();
        }

        if (curUser.SK3 != null) {
            infoWnd += "\n\n" + curUser.SK3.name();
            infoWnd += " " + curUser.SK3.desc_wnd();
        }

        return infoWnd;
    }

    public void onHeroGainExp(float levelPercent, Hero hero) {
        super.onHeroGainExp(levelPercent, hero);

        float chargepur = 10 + (Dungeon.hero.lvl * 6) - 72;
        if (chargepur < 10) chargepur = 10;
        if (chargepur > 100) chargepur = 100;

        if (charge > 100) chargepur /= 2;

        charge += chargepur * levelPercent * LFPChargeMultiplier();
        if(Dungeon.level.feeling == Level.Feeling.CLOUDY) charge += chargepur * levelPercent * LFPChargeMultiplier();
        if (charge > 150) charge = 150;
        updateQuickslot();
    }

    public void SetCharge(int cha)
    {
        charge += cha;
        if(Dungeon.level.feeling == Level.Feeling.CLOUDY) charge += cha;
        if (charge > 150) charge = 150;
        updateQuickslot();
    }

    private static final String CHARGE = "charge";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(CHARGE, charge);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (chargeCap > 0) charge = Math.min(chargeCap, bundle.getInt(CHARGE));
        else charge = bundle.getInt(CHARGE);
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public String status() {

        //if the artifact isn't IDed, or is cursed, don't display anything
        if (!isIdentified() || cursed) {
            return null;
        }
        //display as percent
        if (chargeCap == 150)
            return Messages.format("%d%%", charge);


        //otherwise, if there's no charge, return null.
        return null;
    }
}
