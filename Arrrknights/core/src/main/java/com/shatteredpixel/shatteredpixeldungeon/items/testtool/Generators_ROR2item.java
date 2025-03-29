package com.shatteredpixel.shatteredpixeldungeon.items.testtool;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.APRounds;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.Aegis;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.ArmorPlate;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.Behemoth;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.Crowbar;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.FrostRelic;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.Gasoline;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.LightFluxPauldron;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.LuckyLeaf;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.OddOpal;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.Perforator;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.ROR2item;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.Raincoat;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.Recycler;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.StunGrenade;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.SymbioticScorpion;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.TitanicKnurl;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.TopazBrooch;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.TougherTimes;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.Transcendence;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.TriTipDagger;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.UnstableTeslaCoil;
import com.shatteredpixel.shatteredpixeldungeon.items.ror2items.WakeOfVultures;
import com.shatteredpixel.shatteredpixeldungeon.items.testtool.Generators;
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

public class Generators_ROR2item extends Generators {
    {
        image = ItemSpriteSheet.UNDONE_MARK;
    }
    private int level;
    private int selected;
    private boolean cursed;
    @Override
    public ArrayList<String> actions(Hero hero) {
        return super.actions(hero);
    }
    @Override
    public void execute(Hero hero, String action ) {
        super.execute( hero, action );
        if(action.equals(AC_GIVE)){
            GameScene.show(new SettingsWindow());
        }
    }
    private void createROR2item(){
        ROR2item a = Reflection.newInstance(idToROR2item(selected));
        if(a != null){
            if(Challenges.isItemBlocked(a)) return;
            a.identify();
            if(a.collect()){
                GLog.i(Messages.get(this, "collect_success", a.name()));
            }else{
                a.doDrop(curUser);
            }
        }
    }
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("selected", selected);
        bundle.put("is_cursed", cursed);
        bundle.put("level", level);
    }
    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        selected = bundle.getInt("selected");
        cursed = bundle.getBoolean("is_cursed");
        level = bundle.getInt("level");
    }

    private Class<? extends ROR2item> idToROR2item(int sel){
        switch(sel){
            //white
            case 0: return APRounds.class;
            case 1: return ArmorPlate.class;
            case 2: return Crowbar.class;
            case 3: return Gasoline.class;
            case 4: return OddOpal.class;
            case 5: return StunGrenade.class;
            case 6: return TopazBrooch.class;
            case 7: return TougherTimes.class;
            case 8: return TriTipDagger.class;
            //red
            case 9: return Aegis.class;
            case 10: return Behemoth.class;
            case 11: return LuckyLeaf.class;
            case 12: return Raincoat.class;
            case 13: return FrostRelic.class;
            case 14: return SymbioticScorpion.class;
            case 15: return UnstableTeslaCoil.class;
            case 16: return WakeOfVultures.class;
            //yellow
            case 17: return Perforator.class;
            case 18: return TitanicKnurl.class;
            //orange
            case 19: return Recycler.class;
            //blue
            case 20: return Transcendence.class;
            default:case 21: return LightFluxPauldron.class;
        }
    }
    private static ArrayList<Class<? extends ROR2item>> r2iList = new ArrayList<Class<? extends ROR2item>>();
    private void buildROR2itemArray(){
        if(!r2iList.isEmpty()) return;
        for(int i=0;i<22;++i){
            r2iList.add(idToROR2item(i));
        }
    }



    private class SettingsWindow extends Window {
        private static final int WIDTH = 140;
        private static final int BTN_SIZE = 16;
        private static final int GAP = 2;
        private RenderedTextBlock t_selected;
        private RedButton b_create;
        private ArrayList<IconButton> r2iSprites = new ArrayList<>();

        public SettingsWindow(){
            buildROR2itemArray();
            createROR2itemImage();
            t_selected = PixelScene.renderTextBlock("", 6);
            t_selected.text();
            add((t_selected));

            b_create = new RedButton(Messages.get(this, "create_button")) {
                @Override
                protected void onClick() {
                    createROR2item();
                }
            };
            add(b_create);

            updateText();
        }

        private void layout(){
            t_selected.setPos(0, 3*GAP + BTN_SIZE *3);
            b_create.setRect(0, t_selected.bottom() + GAP, WIDTH, 16);
            resize(WIDTH, (int)b_create.bottom() + GAP);
        }

        private void createROR2itemImage(){
            float left;
            float top = GAP;
            int placed = 0;
            int length = r2iList.size();
            for (int i = 0; i < length; ++i) {
                final int j = i;
                IconButton btn = new IconButton() {
                    @Override
                    protected void onClick() {
                        selected = j;
                        updateText();
                        super.onClick();
                    }
                };
                Image im = new Image(Assets.Sprites.ITEMS);
                im.frame(ItemSpriteSheet.film.get(Objects.requireNonNull(Reflection.newInstance(r2iList.get(i))).image));
                im.scale.set(0.5f);
                btn.icon(im);
                if(i<9) {
                    left = (WIDTH - BTN_SIZE * 9) / 2f;
                    btn.setRect(left + placed * BTN_SIZE, top, BTN_SIZE, BTN_SIZE);
                }else if (i < 18) {
                    left = (WIDTH - BTN_SIZE * 9) / 2f;
                    btn.setRect(left + (placed-9) * BTN_SIZE, top + GAP + BTN_SIZE, BTN_SIZE, BTN_SIZE);
                } else {
                    left = (WIDTH - BTN_SIZE * 9) / 2f;
                    btn.setRect(left + (placed-18) * BTN_SIZE, top + 2*GAP + 2*BTN_SIZE, BTN_SIZE, BTN_SIZE);
                }
                add(btn);
                placed++;
                r2iSprites.add(btn);
            }
        }

        private void updateText(){
            t_selected.text(Messages.get(Generators_ROR2item.class, "selected", Messages.get(idToROR2item(selected), "name")));
            layout();
        }
    }
}