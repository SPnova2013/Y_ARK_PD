package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.RingKit;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.NPC_TextSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.skins.TomimiSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Point;

public class RlyehText extends NPC {
    {
        spriteClass = NPC_TextSprite.class;
        properties.add(Char.Property.IMMOVABLE);
        properties.add(Property.NPC);
    }

    @Override
    public int defenseSkill(Char enemy) {
        return INFINITE_EVASION;
    }

    @Override
    public void damage(int dmg, Object src) {
    }

    @Override
    public boolean interact(Char c) {
        if(Dungeon.depth == 30) {
            sprite.turnTo(pos, c.pos);
            if (!(Dungeon.DLC == Dungeon.ROR)) {
                Dungeon.DLC = Dungeon.ROR;
                sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "go_ror"));
            } else {
                Dungeon.DLC = Dungeon.SIESTA;
                sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "cancel_ror"));
            }
        }
        if(Dungeon.depth == 1){
            if(Statistics.deepestFloor == 1) {
                Game.runOnRenderThread(new Callback() {
                    @Override
                    public void call() {
                        GameScene.show(new WndOptions(Messages.titleCase(RlyehText.this.name()),
                                Messages.get(RlyehText.this, "choose_ghost_mission"),
                                Messages.get(RlyehText.this, "level2"),
                                Messages.get(RlyehText.this, "level3"),
                                Messages.get(RlyehText.this, "level4")) {

                            @Override
                            protected void onSelect(int index) {
                                if (index < 2) {
                                    Ghost.SpawnConfig.setDecidedDepth(index + 2);
                                    Ghost.SpawnConfig.setTierChances(new float[]{
                                            0,//0阶
                                            0,//1阶
                                            10 + index,//2阶
                                            6 + 2*index,//3阶
                                            3 + index,//4阶
                                            1 + index});//5阶
                                    Ghost.SpawnConfig.setRewardLevelChances(new float[]{
                                            0.87f - index*0.05f,//+1
                                            0.10f + index*0.03f,//+2
                                            0.03f + index*0.02f});//+3
                                }
                            }

                            @Override
                            public void onBackPressed() {
                                super.onBackPressed();
                            }
                        });
                    }
                });
            }else{sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "left_level1"));}
        }
        if(Dungeon.depth == 6){
            if(Statistics.deepestFloor == 6){
                Game.runOnRenderThread(new Callback() {
                    @Override
                    public void call() {
                        GameScene.show(new WndOptions(Messages.titleCase(RlyehText.this.name()),
                                Messages.get(RlyehText.this, "choose_wandmaker_mission"),
                                Messages.get(RlyehText.this, "wandmaker_mission_1"),
                                Messages.get(RlyehText.this, "wandmaker_mission_2"),
                                Messages.get(RlyehText.this, "wandmaker_mission_3")) {

                            @Override
                            protected void onSelect(int index) {
                                if (index < 2) {
                                    Wandmaker.SpawnConfig.setMissionType(index+1);
                                }
                            }

                            @Override
                            public void onBackPressed() {
                                super.onBackPressed();
                            }
                        });
                    }
                });
            }else{sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "left_level6"));}
        }
        return true;
    }

    public static void spawn(Level level, int ppos) {
        RlyehText text = new RlyehText();
        do {
            text.pos = ppos;
        } while (text.pos == -1);
        level.mobs.add(text);
    }

    public static void spawn(Level level, Room room) {

        RlyehText npc = new RlyehText();
        boolean validPos;
        do {
            validPos = true;
            npc.pos = level.pointToCell(room.random());
            if (npc.pos == level.entrance){
                validPos = false;
            }
            for (Point door : room.connected.values()){
                if (level.trueDistance( npc.pos, level.pointToCell( door ) ) <= 1){
                    validPos = false;
                }
            }
            if (level.traps.get(npc.pos) != null){
                validPos = false;
            }
        } while (!validPos);
        level.mobs.add( npc );
    }
}
