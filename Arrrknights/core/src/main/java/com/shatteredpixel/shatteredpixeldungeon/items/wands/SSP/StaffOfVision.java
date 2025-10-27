package com.shatteredpixel.shatteredpixeldungeon.items.wands.SSP;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSight;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WardSprite_Vision;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

public class StaffOfVision extends Wand {
    {
        image = ItemSpriteSheet.WAND_VISION;
    }
    public int initialCharges() {
        return 3;
    }
    @Override
    protected int collisionProperties(int target) {
        return Ballistica.STOP_TARGET;
    }
    @Override
    public boolean tryToZap(Hero owner, int target) {
        int currentWardEnergy = 0;
        int maxWardEnergy = 0;
        for (Buff buff : curUser.buffs()){
            if (buff instanceof Wand.Charger){
                if (((Charger) buff).wand() instanceof StaffOfVision){
                    maxWardEnergy += 3 + ((Charger) buff).wand().level();
                }
            }
        }

        if ((currentWardEnergy + 1) > maxWardEnergy){
            GLog.w( Messages.get(this, "no_more_wards"));
            return false;
        }

        return super.tryToZap(owner, target);
    }

    @Override
    protected void onZap(Ballistica bolt) {

        int target = bolt.collisionPos;
        Char ch = Actor.findChar(target);
        if (ch != null){
            if (bolt.dist > 1) target = bolt.path.get(bolt.dist-1);

            ch = Actor.findChar(target);
            if (ch != null){
                GLog.w( Messages.get(this, "bad_location"));
                Dungeon.level.pressCell(bolt.collisionPos);
                return;
            }
        }

        if (!Dungeon.level.passable[target]){
            GLog.w( Messages.get(this, "bad_location"));
            Dungeon.level.pressCell(target);

        } else if (ch != null){
            GLog.w( Messages.get(this, "bad_location"));
            Dungeon.level.pressCell(target);
        } else {
            StaffOfVision.VisionWard ward = new StaffOfVision.VisionWard();
            ward.pos = target;
            ward.wandLevel = buffedLvl();
            GameScene.add(ward, 1f);
            Dungeon.level.occupyCell(ward);
            ward.sprite.emitter().burst(MagicMissile.WardParticle.UP, ward.tier);
            Dungeon.level.pressCell(target);
            ward.viewDistance = 6 + buffedLvl();
            ward.observe();
        }
    }

    @Override
    protected void fx(Ballistica bolt, Callback callback) {
        MagicMissile m = MagicMissile.boltFromChar(curUser.sprite.parent,
                MagicMissile.MAGIC_MISSILE,
                curUser.sprite,
                bolt.collisionPos,
                callback);

        if (bolt.dist > 10){
            m.setSpeed(bolt.dist*20);
        }
        Sample.INSTANCE.play(Assets.Sounds.ZAP);
    }
    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        int level = Math.max( 0, staff.buffedLvl() );
        Buff.prolong(attacker, MagicalSight.class, level);
    }
    @Override
    public String statsDesc() {
        if (levelKnown)
            return Messages.get(this, "stats_desc", level()+3);
        else
            return Messages.get(this, "stats_desc", 3);
    }

    public static class VisionWard extends NPC {

        public int tier = 1;
        private int wandLevel = 1;

        public int totalZaps = 0;

        {
            spriteClass = WardSprite_Vision.class;
            properties.add(Property.IMMOVABLE);
            properties.add(Property.INORGANIC);

            viewDistance = 6;
        }

        private void observe(){
            GameScene.updateFog(this.pos, 6+wandLevel);
            Dungeon.observe();
        }

        @Override
        public String name() {
            return Messages.get(this, "name");
        }

        @Override
        public void damage( int dmg, Object src ) {
        }
        @Override
        public void add( Buff buff ) {
        }
        @Override
        public int defenseSkill(Char enemy) {
            return INFINITE_EVASION;
        }

        @Override
        protected boolean getCloser(int target) {
            return false;
        }

        @Override
        protected boolean getFurther(int target) {
            return false;
        }

        @Override
        public CharSprite sprite() {
            WardSprite_Vision sprite = (WardSprite_Vision) super.sprite();
            sprite.linkVisuals(this);
            return sprite;
        }

        @Override
        public void updateSpriteState() {
            super.updateSpriteState();
            ((WardSprite_Vision)sprite).updateTier(tier);
            sprite.place(pos);
        }

        @Override
        public void destroy() {
            super.destroy();
            Dungeon.observe();
            GameScene.updateFog(pos, viewDistance+1);
        }

        @Override
        public boolean canInteract(Char c) {
            return true;
        }

        @Override
        public boolean interact( Char c ) {
            if (c != Dungeon.hero){
                return true;
            }
            Game.runOnRenderThread(new Callback() {
                @Override
                public void call() {
                    GameScene.show(new WndOptions( Messages.get(StaffOfVision.VisionWard.this, "dismiss_title"),
                            Messages.get(StaffOfVision.VisionWard.this, "dismiss_body"),
                            Messages.get(StaffOfVision.VisionWard.this, "dismiss_confirm"),
                            Messages.get(StaffOfVision.VisionWard.this, "dismiss_cancel") ){
                        @Override
                        protected void onSelect(int index) {
                            if (index == 0){
                                die(null);
                            }
                        }
                    });
                }
            });
            return true;
        }

        @Override
        public String description() {
            return Messages.get(this, "desc");
        }

        {
            immunities.add( Corruption.class );
        }

        private static final String TIER = "tier";
        private static final String WAND_LEVEL = "wand_level";
        private static final String TOTAL_ZAPS = "total_zaps";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(TIER, tier);
            bundle.put(WAND_LEVEL, wandLevel);
            bundle.put(TOTAL_ZAPS, totalZaps);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            tier = bundle.getInt(TIER);
            viewDistance = 6 + wandLevel;
            wandLevel = bundle.getInt(WAND_LEVEL);
            totalZaps = bundle.getInt(TOTAL_ZAPS);
        }

        {
            properties.add(Property.IMMOVABLE);
        }
    }
}
