package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.JessiSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.ArchSkinSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.AstesiaSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.FnovaSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.FrankaSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.FrostLeafSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.GraniSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.HinaSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.KayokoSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.LappySprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.LilithSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.MudrockSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.NeuroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.SchwarzSkinSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.SkadiSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.SpecterSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.SussurroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.TenmaSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.TlipocaSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.TomimiSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.WeedySkinSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.WisadelSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.talrufightSprite;

public class SkinModel extends NPC {
    {
        properties.add(Property.IMMOVABLE);
        properties.add(Property.NPC);
    }
    private final int MAX_SKIN = 22;
    public SkinModel()
    {
        super();
       switch (Dungeon.skin_ch) {
           default:
           case 0:
           spriteClass = talrufightSprite.class;
           break;
           case 1: spriteClass = FnovaSprite.class;
           break;
           case 2: spriteClass= SkadiSprite.class;
           break;
           case 3: spriteClass= SussurroSprite.class;
           break;
           case 4: spriteClass= GraniSprite.class;
           break;
           case 5: spriteClass= JessiSprite.class;
               break;
           case 6: spriteClass= LappySprite.class;
           break;
           case 7: spriteClass= FrostLeafSprite.class;
           break;
           case 8: spriteClass= MudrockSprite.class;
               break;
           case 9: spriteClass= AstesiaSprite.class;
               break;
           case 10: spriteClass= SpecterSprite.class;
               break;
           case 11: spriteClass= SchwarzSkinSprite.class;
               break;
           case 12: spriteClass= ArchSkinSprite.class;
               break;
               case 13: spriteClass= TomimiSprite.class;
               break;
           case 14: spriteClass= FrankaSprite.class;
               break;
           case 15: spriteClass= WeedySkinSprite.class;
               break;
           case 16: spriteClass= LilithSprite.class;
                break;
           case 17: spriteClass= HinaSprite.class;
               break;
           case 18: spriteClass= TlipocaSprite.class;
               break;
           case 19: spriteClass= KayokoSprite.class;
               break;
           case 20 : spriteClass= NeuroSprite.class;
               break;
           case 21: spriteClass = WisadelSprite.class;
           break;
           case MAX_SKIN: spriteClass = TenmaSprite.class;
           break;
       }
    }



    @Override
    public int defenseSkill( Char enemy ) {
        return INFINITE_EVASION;
    }

    @Override
    public void damage( int dmg, Object src ) {
    }

    @Override
    public boolean interact(Char c) {
        SkinChange();
        return true;
    }

    public static void spawn(Level level, int poss) {
        SkinModel Modle = new SkinModel();
        do {
            Modle.pos = poss;
        } while (Modle.pos == -1);
        level.mobs.add(Modle);
    }

    public void SkinChange()
    {
        Dungeon.skin_ch++;
        if (Dungeon.skin_ch > MAX_SKIN) Dungeon.skin_ch = 0;

        int ppos = this.pos;
        this.destroy();
        this.sprite.killAndErase();
        Dungeon.level.mobs.remove(this);

        SkinModel Model = new SkinModel();
        Model.pos = ppos;
        GameScene.add(Model, 0f);
    }
}
