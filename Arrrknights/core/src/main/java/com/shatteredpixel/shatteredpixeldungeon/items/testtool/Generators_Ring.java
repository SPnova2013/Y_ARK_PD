package com.shatteredpixel.shatteredpixeldungeon.items.testtool;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfAccuracy;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfAmplified;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfAssassin;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfCommand;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfDominate;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfElements;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEvasion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfFuror;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMight;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMistress;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSunLight;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfTenacity;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SP.StaffOfAbsinthe;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SP.StaffOfAngelina;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SP.StaffOfBreeze;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SP.StaffOfCorrupting;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SP.StaffOfGreyy;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SP.StaffOfLeaf;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SP.StaffOfLena;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SP.StaffOfMayer;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SP.StaffOfMudrock;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SP.StaffOfPodenco;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SP.StaffOfPurgatory;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SP.StaffOfShining;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SP.StaffOfSkyfire;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SP.StaffOfSnowsant;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SP.StaffOfSussurro;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SP.StaffOfSuzuran;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SP.StaffOfTime;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SP.StaffOfVigna;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SP.StaffOfWeedy;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SSP.StaffOfConcept;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SSP.StaffOfMageHand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SSP.StaffOfTwinTurbo;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SSP.StaffOfValstrax;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.SSP.StaffOfVision;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlowStone;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorrosion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorruption;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFireblast;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfHallucination;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfPrismaticLight;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfSilence;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfTransfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
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
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Objects;

//Also contains wand
public class Generators_Ring extends Generators {
    {
        image = ItemSpriteSheet.RING_HOLDER;
    }

    private int levelToGen;
    private int category;
    private boolean cursed;
    private int selected;
    private static final int RING_CAT = 0;
    private static final int WAND_CAT = 1;
    private static final int WAND_SP  = 2;
    private static final int WAND_SSP  = 3;
    private static final int MAX_RING = 17;
    private static final int MAX_WAND = 17;
    private static final int MAX_SPWAND = 20;
    private static final int MAX_SSPWAND = 5;

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_GIVE)) {
            GameScene.show(new SettingsWindow());
        }
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        return super.actions(hero);
    }

    private void createItem(){
        boolean collect = false;
        if (category == RING_CAT) {
            Ring r = Reflection.newInstance(idToRing(selected));
            if(r != null) {
                if(Challenges.isItemBlocked(r)) return;
                modifyRing(r);
                collect = r.identify().collect();
                if(collect){
                    GLog.i(Messages.get(this, "collect_success", r.name()));
                }else{
                    r.doDrop(curUser);
                }
            }
        } else if (category == WAND_CAT) {
            Wand w = Reflection.newInstance(idToWand(selected));
            if( w != null) {
                if(Challenges.isItemBlocked(w)) return;
                modifyWand(w);
                collect = w.identify().collect();
                if(collect){
                    GLog.i(Messages.get(this, "collect_success", w.name()));
                }else{
                    w.doDrop(curUser);
                }
            }
        }else if (category == WAND_SP){
            Wand w= Reflection.newInstance(idToSPWand(selected));
            if (w!=null){
                if(Challenges.isItemBlocked(w)) return;
                modifyWand(w);
                collect = w.identify().collect();
                if(collect){
                    GLog.i(Messages.get(this, "collect_success", w.name()));
                }else{
                    w.doDrop(curUser);
                }
            }
        }else {
            Wand w= Reflection.newInstance(idToSSPWand(selected));
            if (w!=null){
                if(Challenges.isItemBlocked(w)) return;
                modifyWand(w);
                collect = w.identify().collect();
                if(collect){
                    GLog.i(Messages.get(this, "collect_success", w.name()));
                }else{
                    w.doDrop(curUser);
                }
            }
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("selected", selected);
        bundle.put("is_cursed", cursed);
        bundle.put("level_to_gen", levelToGen);
        bundle.put("category", category);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        selected = bundle.getInt("selected");
        cursed = bundle.getBoolean("is_cursed");
        levelToGen = bundle.getInt("level_to_gen");
        category = bundle.getInt("category");
    }

    private void modifyRing(Ring r) {
        r.level(levelToGen);
        r.cursed = cursed;
    }

    private void modifyWand(Wand w) {
        w.level(levelToGen);
        w.cursed = cursed;
    }

    private Class<? extends Ring> idToRing(int id) {
        switch (id) {
            case 0:
                return RingOfAccuracy.class;
            case 1:
                return RingOfElements.class;
            case 2:
                return RingOfEvasion.class;
            case 3:
                return RingOfEnergy.class;
            case 4:
                return RingOfForce.class;
            case 5:
                return RingOfFuror.class;
            case 6:
                return RingOfHaste.class;
            case 7:
                return RingOfMight.class;
            case 8:
                return RingOfSharpshooting.class;
            case 9:
                return RingOfTenacity.class;
            case 10:
                return RingOfWealth.class;
            case 11:
                return RingOfAmplified.class;
            case 12:
                return RingOfAssassin.class;
            case 13:
                return RingOfCommand.class;
            case 14:
                return RingOfDominate.class;
            case 15:
                return RingOfMistress.class;
            case 16:
            default:
                return RingOfSunLight.class;
        }
    }

    private Class<? extends Wand> idToWand(int id) {
        switch (id) {
            case 0:
                return WandOfBlastWave.class;
            case 1:
                return WandOfCorrosion.class;
            case 2:
                return WandOfCorruption.class;
            case 3:
                return WandOfDisintegration.class;
            case 4:
                return WandOfFireblast.class;
            case 5:
                return WandOfFrost.class;
            case 6:
                return WandOfLightning.class;
            case 7:
                return WandOfLivingEarth.class;
            case 8:
                return WandOfMagicMissile.class;
            case 9:
                return WandOfPrismaticLight.class;
            case 10:
                return WandOfRegrowth.class;
            case 11:
                return WandOfTransfusion.class;
            case 12:
                return WandOfWarding.class;
            case 13:
                return WandOfHallucination.class;
            case 14:
                return WandOfHealing.class;
            case 15:
            default:
                return WandOfSilence.class;
            case 16:
                return WandOfBlowStone.class;
        }

    }

    private Class<? extends Wand> idToSPWand(int id) {
        switch (id){
            case 1:
                return StaffOfAbsinthe.class;
            case 2:
                return StaffOfAngelina.class;
            case 3:
                return StaffOfBreeze.class;
            case 4:
                return StaffOfCorrupting.class;
            case 5:
                return StaffOfGreyy.class;
            case 6:
                return StaffOfLeaf.class;
            case 7:
                return StaffOfLena.class;
            case 8:
                return StaffOfMayer.class;
            case 9:
                return StaffOfMudrock.class;
            case 10:
                return StaffOfPodenco.class;
            case 11:
                return StaffOfPurgatory.class;
            case 12:
                return StaffOfShining.class;
            case 13:
                return StaffOfSkyfire.class;
            case 14:
                return StaffOfSnowsant.class;
            case 15:
                return StaffOfSussurro.class;
            case 16:
                return StaffOfSuzuran.class;
            case 17:
                return StaffOfTime.class;
            case 18:
                return StaffOfVigna.class;
            default:
            case 19:
                return StaffOfWeedy.class;
        }
    }
    private Class<? extends Wand> idToSSPWand(int id) {
        switch (id) {
            case 0: return StaffOfConcept.class;
            case 1: return StaffOfMageHand.class;
            case 2: return StaffOfTwinTurbo.class;
            case 3: return StaffOfValstrax.class;
            default: return StaffOfVision.class;
        }
    }
    private static ArrayList<Class<? extends Ring>> ringList = new ArrayList<>();
    private static ArrayList<Class<? extends Wand>> wandList = new ArrayList<>();
    private static ArrayList<Class<? extends Wand>> spwandList = new ArrayList<>();
    private static ArrayList<Class<? extends Wand>> sspWandList = new ArrayList<>();

    private void buildRingList() {
        if (!ringList.isEmpty()) return;
        for (int i = 0; i < MAX_RING; ++i) {
            ringList.add(idToRing(i));
        }
    }

    private void buildWandList() {
        if (!wandList.isEmpty()) return;
        for (int i = 0; i < MAX_WAND; ++i) {
            wandList.add(idToWand(i));
        }
    }
    private void buildSPWandList(){
        if (!spwandList.isEmpty()) return;
        for (int i = 0; i < MAX_SPWAND; ++i) {
            spwandList.add(idToSPWand(i));
        }
    }
    private void buildSSPWandList() {
        if (!sspWandList.isEmpty()) return;
        for (int i = 0; i < MAX_SSPWAND; ++i) {
            sspWandList.add(idToSSPWand(i));
        }
    }
    private int total(int category){
        if (category == RING_CAT) return MAX_RING;
        if (category == WAND_CAT) return MAX_WAND;
        if (category == WAND_SP) return MAX_SPWAND;
        if (category == WAND_SSP) return MAX_SSPWAND;
        return 0;
    }

    private class SettingsWindow extends Window {
        private static final int WIDTH = 140;
        private static final int BTN_SIZE = 14;
        private static final int GAP = 2;
        private OptionSlider o_level;
        private OptionSlider o_category;
        private CheckBox c_curse;
        private RenderedTextBlock t_selected;
        private RedButton b_create;
        private ArrayList<IconButton> iconButtonsList = new ArrayList<>();

        public SettingsWindow() {
            buildRingList();
            buildWandList();
            buildSPWandList();
            buildSSPWandList();
            o_category = new OptionSlider(
                    Messages.get(this, "category"),
                    Messages.get(this, "ring"),
                    Messages.get(this, "wand"), 0, 3) {
                @Override
                protected void onChange() {
                    category = getSelectedValue();
                    selected = 0;
                    for (IconButton ib : iconButtonsList.toArray(new IconButton[0])) {
                        ib.destroy();
                    }
                    createImage();
                    layout();
                }
            };
            o_category.setSelectedValue(category);
            o_category.setRect(0, GAP, WIDTH, 24);
            add(o_category);

            createImage();

            t_selected = PixelScene.renderTextBlock("", 6);
            t_selected.text();
            add(t_selected);

            o_level = new OptionSlider(Messages.get(this, "level"), "0", "12", 0, 12) {
                @Override
                protected void onChange() {
                    levelToGen = getSelectedValue();
                }
            };
            o_level.setSelectedValue(levelToGen);
            add(o_level);

            c_curse = new CheckBox(Messages.get(this, "curse")) {
                @Override
                protected void onClick() {
                    super.onClick();
                    cursed = checked();
                }
            };
            c_curse.checked(cursed);
            add(c_curse);

            b_create = new RedButton(Messages.get(this, "create_button")) {
                @Override
                protected void onClick() {
                    createItem();
                }
            };
            add(b_create);

            updateText();
        }

        private void layout() {
            o_category.setRect(0, GAP, WIDTH, 24);
            t_selected.setPos(0, o_category.bottom() + 3 * GAP + 2 * BTN_SIZE);
            o_level.setRect(0, t_selected.bottom() + GAP, WIDTH, 24);
            c_curse.setRect(0, o_level.bottom() + GAP, WIDTH, 18);
            b_create.setRect(0, c_curse.bottom()+GAP, WIDTH, 16);
            resize(WIDTH, (int) b_create.bottom() + GAP);
        }

        private void updateText() {
            Class<? extends Item> item = ringList.get(0);
            if(category == RING_CAT){
                item = ringList.get(selected);
            }
            else if (category == WAND_CAT) {
                item = wandList.get(selected);
            }else if (category == WAND_SP){
                item = spwandList.get(selected);
            }else {
                item = sspWandList.get(selected);
            }
            t_selected.text(Messages.get(Generators_Ring.class, "selected", Messages.get(item, "name")));
            layout();
        }

        private void createImage() {
            float left;
            float top = GAP + o_category.bottom();
            int placed = 0;
            int length;
            switch (category){
                case RING_CAT:
                    length=ringList.size();
                    break;
                case WAND_CAT:
                    length=wandList.size();
                    break;
                case WAND_SP:
                    length=spwandList.size();
                    break;
                case WAND_SSP:
                    length = sspWandList.size();
                    break;
                default:
                    length=0;
            }
            int firstRow = (length % 2 == 0 ? length / 2 : (length / 2 + 1));
            for (int i = 0; i < length; ++i) {
                final int j = i;
                IconButton btn = new IconButton() {
                    @Override
                    protected void onClick() {
                        selected = Math.min(j,total(category)-1);
                        updateText();
                        super.onClick();
                    }
                };
                if (category == RING_CAT) {
                    Image im = new Image(Assets.Sprites.ITEM_ICONS);
                    im.frame(ItemSpriteSheet.Icons.film.get(Objects.requireNonNull(Reflection.newInstance(ringList.get(i))).icon));
                    im.scale.set(1f);
                    btn.icon(im);
                } else if (category == WAND_CAT) {
                    Image im = new Image(Assets.Sprites.ITEMS);
                    im.frame(ItemSpriteSheet.film.get(Objects.requireNonNull(Reflection.newInstance(wandList.get(i))).image));
                    im.scale.set(0.45f);
                    btn.icon(im);
                }else if (category == WAND_SP){
                    Image im = new Image(Assets.Sprites.ITEMS);
                    im.frame(ItemSpriteSheet.film.get(Objects.requireNonNull(Reflection.newInstance(spwandList.get(i))).image));
                    im.scale.set(0.45f);
                    btn.icon(im);
                }else {
                    Image im = new Image(Assets.Sprites.ITEMS);
                    im.frame(ItemSpriteSheet.film.get(
                            Objects.requireNonNull(
                                    Reflection.newInstance(sspWandList.get(i))
                            ).image
                    ));
                    im.scale.set(0.45f);
                    btn.icon(im);
                }

                if (i < firstRow) {
                    left = (WIDTH - BTN_SIZE * firstRow) / 2f;
                    btn.setRect(left + placed * BTN_SIZE, top, BTN_SIZE, BTN_SIZE);
                } else {
                    left = (WIDTH - BTN_SIZE * (length-firstRow)) / 2f;
                    btn.setRect(left + (placed - firstRow) * BTN_SIZE, top + GAP + BTN_SIZE, BTN_SIZE, BTN_SIZE);
                }
                add(btn);
                placed++;
                iconButtonsList.add(btn);
            }
        }

    }
}