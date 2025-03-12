package com.shatteredpixel.shatteredpixeldungeon.items.testtool;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.TomorrowRogueNight;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.AcidSlug_A;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Acidic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Agent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.AirborneSoldier;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Albino;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ArmoredBrute;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ArmoredStatue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bandit;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.BlackwaterSlug;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bombtail;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Brute;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Cannot;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CausticSlime;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CocktailThrower;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Crownslayer_shadow;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.EmpireDrone;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Ergate;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ExplodeSlug_A;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Fanatic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.FireCore;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.FloatingSeaDrifter;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GiantMushroom;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Gloompincer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GuerrillaHerald;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GuerrillaSiegebreaker;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.HeavyBoat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Hound;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM100;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM200;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM201;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DemonSpawner;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Eye;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.FetidSlug;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Ghoul;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Gnoll;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollTrickster;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Golem;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Infantry;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.LavaSlug;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.LightArmoredSoldier;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.MetalCrab;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.MudrockZealot;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.MutantSpider;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Originiutant;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piersailor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Raider;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ReunionDefender;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Guard;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Monk;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Necromancer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RioterLeader;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rockbreaker;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rodent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RoughlyRaisedSlug;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.SandPillar;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.SarkazGuerrillaFighter;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.SeaReaper;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.SeaRunner;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ShieldedGuard;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Slug;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RipperDemon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RotHeart;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RotLasher;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Scorpio;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Senior;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Shaman;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Skeleton;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Slime;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Snake;
//import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.SpectralNecromancer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ExplodSlug_N;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Sniper;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Striker;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.StrikerElite;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Succubus;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Swarm;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Thief;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.TiacauhAddict;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.TiacauhBrave;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.TiacauhFanatic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.TiacauhLancer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.TiacauhRipper;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.TiacauhRitualist;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.TiacauhShaman;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.TiacauhShredder;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.TiacauhSniper;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.TiacauhWarrior;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.UrsusCrossbowman;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Warlock;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.WaveCaster;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.miniboss.BloodMagister;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.miniboss.Centurion;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.miniboss.EmperorPursuer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.miniboss.Faust;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.miniboss.MagicGolem;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.miniboss.Mon3tr;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.miniboss.Sentinel;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.miniboss.Shadow;
import com.shatteredpixel.shatteredpixeldungeon.custom.dict.DictSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.testtool.Generators;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.OptionSlider;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.PointF;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

import javax.xml.crypto.Data;

public class MobPlacer extends Generators {
    {
        image = ItemSpriteSheet.CANDLE;
        defaultAction = AC_PLACE;
    }

    private static final String AC_PLACE = "place";
    private static final String AC_SET = "set";

    private int mobTier = 1;
    private int mobIndex = 0;
    private int elite = 0;
    private static final int MAX_ELITE = 6;
    private int elite_op = 0;

    private final ArrayList<Class<? extends ChampionEnemy>> eliteBuffs = new ArrayList<>();
    {
        eliteBuffs.add(ChampionEnemy.Blazing.class);
        eliteBuffs.add(ChampionEnemy.AntiMagic.class);
        eliteBuffs.add(ChampionEnemy.Blessed.class);
        eliteBuffs.add(ChampionEnemy.Giant.class);
        eliteBuffs.add(ChampionEnemy.Growing.class);
        eliteBuffs.add(ChampionEnemy.Projecting.class);
    };

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_PLACE);
        actions.add(AC_SET);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_PLACE)) {
            GameScene.selectCell(new CellSelector.Listener() {
                @Override
                public void onSelect(final Integer cell) {
                    if(cell != null){
                        if (canPlaceMob(cell)) {
                            try {
                                Mob m = Reflection.newInstance(allData.get(dataThreshold(mobTier) + mobIndex).mobClass);
                                m.pos = cell;
                                GameScene.add(m);
                                if(elite_op>0){
                                    for(int i=0;i<6;++i){
                                        if((elite_op & (1<<i))>0){
                                            Buff.affect(m, eliteBuffs.get(i));
                                        }
                                    }
                                }
                                ScrollOfTeleportation.appear(m, cell);
                                Dungeon.level.occupyCell(m);
                                if(m instanceof GuerrillaHerald) Dungeon.level.heraldAlive = true;
                            } catch (Exception e) {
                                TomorrowRogueNight.reportException(e);
                            }
                        }else{
                            GLog.w(M.L(MobPlacer.class, "forbidden"));
                        }
                    }
                    curUser.next();
                }
                @Override
                public String prompt() {
                    return M.L(MobPlacer.class, "prompt");
                }
            });

        } else if (action.equals(AC_SET)) {
            GameScene.show(new WndSetMob());
        }
    }

    private boolean canPlaceMob(int cell){
        return Actor.findChar(cell) == null && (!Dungeon.level.solid[cell] || Dungeon.level.map[cell] == Terrain.DOOR || Dungeon.level.map[cell] == Terrain.OPEN_DOOR);
    }


    protected int maxMobIndex(int tier){
        switch (tier){
            case 1: return DataPack.SHIELDED_GUARD.ordinal();
            case 2: return DataPack.GUERRILLA_HERALD.ordinal() - DataPack.SHIELDED_GUARD.ordinal() - 1;
            case 3: return DataPack.DM201.ordinal() - DataPack.GUERRILLA_HERALD.ordinal() - 1;
            case 4: return DataPack.ELE_CHAOS.ordinal() - DataPack.DM201.ordinal() - 1;
            case 5: return DataPack.ACIDIC.ordinal() - DataPack.ELE_CHAOS.ordinal() - 1;
            case 6: return DataPack.PIRANHA.ordinal() - DataPack.ACIDIC.ordinal() - 1;
            case 7: return DataPack.FIRECORE.ordinal() - DataPack.PIRANHA.ordinal() - 1;
            case 8: return DataPack.T_SHAMAN.ordinal() - DataPack.FIRECORE.ordinal() - 1;
            case 9: default: return DataPack.REAPER.ordinal() - DataPack.T_SHAMAN.ordinal() - 1;
        }
    }
    private int dataThreshold(int tier){
        switch (tier){
            case 1: default:
                return 0;
            case 2:
                return DataPack.SHIELDED_GUARD.ordinal()+1;
            case 3:
                return DataPack.GUERRILLA_HERALD.ordinal()+1;
            case 4:
                return DataPack.DM201.ordinal()+1;
            case 5:
                return DataPack.ELE_CHAOS.ordinal()+1;
            case 6:
                return DataPack.ACIDIC.ordinal()+1;
            case 7:
                return DataPack.PIRANHA.ordinal()+1;
            case 8:
                return DataPack.FIRECORE.ordinal()+1;
            case 9:
                return DataPack.T_SHAMAN.ordinal()+1;
        }
    }

    @Override
    public void storeInBundle(Bundle b){
        super.storeInBundle(b);
        b.put("mobTier", mobTier);
        b.put("mobIndex", mobIndex);
        b.put("eliteTags", elite);
        b.put("elite_ops", elite_op);
    }

    @Override
    public void restoreFromBundle(Bundle b){
        super.restoreFromBundle(b);
        mobTier = b.getInt("mobTier");
        mobIndex = b.getInt("mobIndex");
        elite = b.getInt("eliteTags");
        elite_op = b.getInt("elite_ops");
    }


    private class WndSetMob extends Window{

        private static final int WIDTH = 140;
        private static final int HEIGHT = 120;
        private static final int BTN_SIZE = 16;
        private static final int GAP = 2;

        private RenderedTextBlock selectedPage;
        private ArrayList<IconButton> mobButtons = new ArrayList<>();
        private RenderedTextBlock selectedMob;
        private ArrayList<CheckBox> eliteOptions = new ArrayList<>(6);

        public WndSetMob(){
            super();

            resize(WIDTH, HEIGHT);

            RedButton lhs = new RedButton("<<<", 8){
                @Override
                public void onClick(){
                    mobTier--;
                    if(mobTier < 1 || mobTier>9){
                        mobTier = 9;
                    }
                    mobIndex = Math.min(mobIndex, maxMobIndex(mobTier));
                    refreshImage();
                    updateSelectedMob();
                }
            };
            lhs.setRect(GAP, GAP, 24, 18);
            add(lhs);

            RedButton rhs = new RedButton(">>>", 8){
                @Override
                public void onClick(){
                    mobTier++;
                    if(mobTier < 1 || mobTier > 9){
                        mobTier = 1;
                    }
                    mobIndex = Math.min(mobIndex, maxMobIndex(mobTier));
                    refreshImage();
                    updateSelectedMob();
                }
            };
            rhs.setRect(WIDTH - 24 - GAP,  GAP, 24, 18);
            add(rhs);

            selectedPage = PixelScene.renderTextBlock("", 9);
            PixelScene.align(selectedPage);
            add(selectedPage);

            selectedMob = PixelScene.renderTextBlock("", 9);
            selectedMob.hardlight(0xFFFF44);
            PixelScene.align(selectedMob);
            add(selectedMob);
/*
            OptionSlider op = new OptionSlider
                    (M.L(MobPlacer.class, "elite"), "0", String.valueOf(MAX_ELITE), 0, MAX_ELITE) {
                @Override
                protected void onChange() {
                    elite = getSelectedValue();
                }
            };
            op.setRect(GAP, 92, WIDTH - 2*GAP, 24);
            op.setSelectedValue(elite);
            add(op);

 */
            float pos = 92;
            for(int i=0;i<6;++i){
                CheckBox cb = new CheckBox(M.L(MobPlacer.class, "elite_name"+ i));
                cb.active = true;
                cb.checked((elite_op & (1<<i))>0);
                add(cb);
                eliteOptions.add(cb);
                if((i&1)==0) {
                    cb.setRect(0, pos, WIDTH/2f -  GAP/2f, 16);
                }else{
                    cb.setRect(WIDTH/2f+GAP/2f, pos, WIDTH/2f -  GAP/2f, 16);
                    pos += 16 + GAP;
                }
            }

            createMobImage();

            updateSelectedMob();
            layout();
        }

        private void updateEliteSettings(){
            int el = 0;
            for(int i=0;i<6;++i){
                el += eliteOptions.get(i).checked() ? (1<<i) : 0;
            }
            elite_op = el;
        }

        private void updateSelectedMob(){
            int selected = mobTier;
            StringBuilder sb = new StringBuilder();
            for(int i=1;i<=9;++i){
                sb.append((i==selected? "* ":"- "));
            }
            selectedPage.text(sb.toString());
            selectedPage.maxWidth(WIDTH / 2);
            selectedPage.setPos((WIDTH - selectedPage.width())/2, 5);
            updateMobText();
        }

        private void updateMobText(){
            selectedMob.text( M.L(allData.get(dataThreshold(mobTier) + mobIndex).mobClass, "name") );
        }

        private void layout(){
            selectedPage.maxWidth(WIDTH / 2);
            selectedPage.setPos((WIDTH - selectedPage.width())/2, 5);
            selectedMob.maxWidth(WIDTH);
            selectedMob.setPos((WIDTH - selectedMob.width())/2, 80);
            resize(WIDTH, (int)eliteOptions.get(5).bottom() + 1);
        }

        private void createMobImage() {
            int maxNum = maxMobIndex(mobTier) + 1;
            //(N+1)/2
            int firstLine = (maxNum >> 1) + (maxNum & 1);
            float left1 = (WIDTH - (GAP + BTN_SIZE) * firstLine + GAP)/2f;
            float left2 = (WIDTH - (GAP + BTN_SIZE) * (maxNum - firstLine) + GAP)/2f;
            for (int i = 0; i < maxNum; ++i) {
                final int j = i;
                IconButton btn = new IconButton() {
                    @Override
                    public void onClick() {
                        super.onClick();
                        mobIndex = j;
                        updateMobText();
                    }
                };
                btn.icon( Reflection.newInstance(allData.get(dataThreshold(mobTier)+i).getMobClass()).sprite());
                //btn.icon( DictSpriteSheet.miscImages(allData.get(dataThreshold(mobTier)+i).imageId) );
                float max = Math.max(btn.icon().width(), btn.icon().height());
                btn.icon().scale = new PointF(BTN_SIZE/max, BTN_SIZE/max);
                if(i<firstLine){
                    btn.setRect(left1, 30f, BTN_SIZE, BTN_SIZE );
                    left1 += GAP + BTN_SIZE;
                }else{
                    btn.setRect(left2, 56f, BTN_SIZE, BTN_SIZE);
                    left2 += GAP + BTN_SIZE;
                }
                add(btn);
                mobButtons.add(btn);
            }
        }

        private void clearImage(){
            for(int i=0, len = mobButtons.size();i<len;++i){
                mobButtons.get(i).destroy();
            }
        }

        private void refreshImage(){
            clearImage();
            createMobImage();
        }

        @Override
        public void onBackPressed() {
            updateEliteSettings();
            super.onBackPressed();
        }
    }


    //packed with a linkedHashmap to find class by ordinal at O(1);
    private static LinkedHashMap<Integer, DataPack> allData = new LinkedHashMap<>();
    static {
        for(DataPack dp : DataPack.values()){
            allData.put(dp.ordinal(), dp);
        }
    }
    private enum DataPack{
        RAT(Slug.class, DictSpriteSheet.RAT),
        //TESTRAT(TestRat.class, DictSpriteSheet.RAT),
        GNOLL(Gnoll.class, DictSpriteSheet.GNOLL),
        SNAKE(Snake.class, DictSpriteSheet.SNAKE),
        ALBINO(Albino.class, DictSpriteSheet.ALBINO),
        CRAB(Hound.class, DictSpriteSheet.CRAB),
        SWARM(Swarm.class, DictSpriteSheet.SWARM),
        SLIME(Slime.class, DictSpriteSheet.SLIME),
        C_SLIME(CausticSlime.class, DictSpriteSheet.CAUSTIC_SLIME),
        SENTINEL(Sentinel.class,-44),
        F_RAT(FetidSlug.class, DictSpriteSheet.F_RAT),
        GNOLL_DARTER(GnollTrickster.class, DictSpriteSheet.GNOLL_DARTER),
        GREAT_CRAB(ReunionDefender.class, DictSpriteSheet.GREAT_CRAB),
        BLACKWATER_SLUG(BlackwaterSlug.class,ItemSpriteSheet.UNDONE_MARK),
        ROUGHLY_RAISED_SLUG(RoughlyRaisedSlug.class,ItemSpriteSheet.UNDONE_MARK),
        RIOTER_LEADER(RioterLeader.class,ItemSpriteSheet.UNDONE_MARK),
        COCKTAIL_THROWER(CocktailThrower.class,ItemSpriteSheet.UNDONE_MARK),
        GLOOMPINCER(Gloompincer.class,ItemSpriteSheet.UNDONE_MARK),
        RODENT(Rodent.class,ItemSpriteSheet.UNDONE_MARK),
        LIGHT_ARMORED_SOLDIER(LightArmoredSoldier.class,ItemSpriteSheet.UNDONE_MARK),
        SHIELDED_GUARD(ShieldedGuard.class,ItemSpriteSheet.UNDONE_MARK),

        SKELETON(Skeleton.class, DictSpriteSheet.SKELETON),
        AIR(AirborneSoldier.class,-1),//now the id is meaningless
        THIEF(Thief.class, DictSpriteSheet.THIEF),
        BANDIT(Bandit.class, DictSpriteSheet.BANDIT),
        DM100(DM100.class, DictSpriteSheet.DM100),
        GUARD(Guard.class, DictSpriteSheet.GUARD),
        NECRO(Necromancer.class, DictSpriteSheet.NECROMANCER),
        CENTURION(Centurion.class,-46),
        ROT_LASHER(RotLasher.class, DictSpriteSheet.ROT_LASHER),
        ROT_HEART(RotHeart.class, DictSpriteSheet.ROT_HEART),
        NEW_FIRE_ELE(Elemental.NewbornFireElemental.class, DictSpriteSheet.NEW_FIRE_ELE),
        BOMBTAIL(Bombtail.class,ItemSpriteSheet.UNDONE_MARK),
        URSUS_CROSSBOWMAN(UrsusCrossbowman.class,ItemSpriteSheet.UNDONE_MARK),
        GUERRILLA_SIEGEBREAKER(GuerrillaSiegebreaker.class,ItemSpriteSheet.UNDONE_MARK),
        SARKAZ_GUERRILLA_FIGHTER(SarkazGuerrillaFighter.class,ItemSpriteSheet.UNDONE_MARK),
        GUERRILLA_HERALD(GuerrillaHerald.class,ItemSpriteSheet.UNDONE_MARK),


        BAT(Bat.class, DictSpriteSheet.BAT),
        BRUTE(Brute.class, DictSpriteSheet.BRUTE),
        ARMORED_BRUTE(ArmoredBrute.class, DictSpriteSheet.ARMORED_BRUTE),
        SHAMAN(Shaman.random(), DictSpriteSheet.SHAMAN),
        SPINNER(ExplodSlug_N.class, DictSpriteSheet.SPINNER),
        ZEALOT(MudrockZealot.class,-2),
        BLOOD(BloodMagister.class,-45),
        M_GOLEM(MagicGolem.class,-49),
        DM200(DM200.class, DictSpriteSheet.DM200),
        DM201(DM201.class, DictSpriteSheet.DM201),

        GHOUL(Ghoul.class, DictSpriteSheet.GHOUL),
        WARLOCK(Warlock.class, DictSpriteSheet.WARLOCK),
        MONK(Monk.class, DictSpriteSheet.MONK),
        SENIOR(Senior.class, DictSpriteSheet.SENIOR),
        GOLEM(Golem.class, DictSpriteSheet.GOLEM),
        FAUST(Faust.class,-48),
        ELE_FIRE(Elemental.FireElemental.class, DictSpriteSheet.ELEMENTAL_FIRE),
        ELE_FROST(Elemental.FrostElemental.class, DictSpriteSheet.ELEMENTAL_FROST),
        ELE_LIGHTNING(Elemental.ShockElemental.class, DictSpriteSheet.ELEMENTAL_SHOCK),
        ELE_CHAOS(Elemental.ChaosElemental.class, DictSpriteSheet.ELEMENTAL_CHAOS),

        RIPPER(RipperDemon.class, DictSpriteSheet.RIPPER),
        SPAWNER(DemonSpawner.class, DictSpriteSheet.SPAWNER),
        EYE(Eye.class, DictSpriteSheet.EYE),
        DRONE(EmpireDrone.class,-4),
        SUCCUBUS(Succubus.class, DictSpriteSheet.SUCCUBUS),
        RAIDER(Raider.class,-5),
        STRIKER(Striker.class,-3),
        STRIKER_E(StrikerElite.class,-6),
        PURSUER(EmperorPursuer.class,-47),
        SCORPIO(Scorpio.class, DictSpriteSheet.SCORPIO),
        ACIDIC(Acidic.class, DictSpriteSheet.AICDIC),

        STATUE(Statue.class, DictSpriteSheet.STATUE),
        ARMORED_STATUE(ArmoredStatue.class, DictSpriteSheet.ARMORED_STATUE),
        WRAITH(Wraith.class, DictSpriteSheet.WRAITH),
        SLAYER(Crownslayer_shadow.class,-22),
        CANNOT(Cannot.class,-40),
        FANATIC(Fanatic.class,-41),
        SAND(SandPillar.class,-43),
        MON3TR(Mon3tr.class,-50),
        PIRANHA(Piranha.class, DictSpriteSheet.FISH),

        INFANTRY(Infantry.class,-7),
        ERGATE(Ergate.class,-8),
        SAILOR(Piersailor.class,-9),
        BOAT(HeavyBoat.class,-17),
        SNIPER(Sniper.class,-10),
        CASTER(WaveCaster.class,-18),
        AGENT(Agent.class,-11),
        LAVASLUG(LavaSlug.class,-12 ),
        METALCRAB(MetalCrab.class,-13),
        SPIDER(MutantSpider.class,-19),
        BREAKER(Rockbreaker.class,-14),
        EXPLODE(ExplodeSlug_A.class,-15),
        ORIGIN(Originiutant.class,-20),
        ACID_A(AcidSlug_A.class,-16),
        FIRECORE(FireCore.class,-21),

        WARRIOR(TiacauhWarrior.class,-23),
        T_FANATIC(TiacauhFanatic.class,-27),
        LANCER(TiacauhLancer.class,-28),
        ADDICT(TiacauhAddict.class,-29),
        T_RIPPER(TiacauhRipper.class,-30),
        SHREDDER(TiacauhShredder.class,-31),
        RITUALIST(TiacauhRitualist.class,-32),
        BRAVE(TiacauhBrave.class,-33),
        T_SNIPER(TiacauhSniper.class,-34),
        MUSHROOM(GiantMushroom.class,-42),
        T_SHAMAN(TiacauhShaman.class,-25),

        RUNNER(SeaRunner.class,-24),
        DRIFTER(FloatingSeaDrifter.class,-35),
        REAPER(SeaReaper.class,-36);
        //CAPSULE(SeaCapsule.class,-37),
        //OCTO(Sea_Octo.class,-38),
        //LEAF(SeaLeef.class,-39),
        //GUIDER(Sea_Brandguider.class,-26);
        private Class<? extends Mob> mobClass;
        private int imageId;

        DataPack(Class<? extends Mob> cls, int image){
            this.imageId = image;
            this.mobClass = cls;
        }

        public int getImageId(){return imageId;}
        public Class<? extends Mob> getMobClass(){return mobClass;}
    }
}