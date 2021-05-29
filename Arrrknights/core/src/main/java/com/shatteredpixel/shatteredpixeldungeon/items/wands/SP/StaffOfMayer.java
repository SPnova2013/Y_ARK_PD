package com.shatteredpixel.shatteredpixeldungeon.items.wands.SP;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WardSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WardSprite_Mayer;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class StaffOfMayer extends Wand
{
    private static ItemSprite.Glowing COL = new ItemSprite.Glowing( 0xE9967A );
    {
        image = ItemSpriteSheet.WAND_WARDING;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return COL;
    }


    @Override
    protected int collisionProperties(int target) {
        if (Dungeon.level.heroFOV[target])  return Ballistica.STOP_TARGET;
        else                                return Ballistica.PROJECTILE;
    }

    private boolean wardAvailable = true;

    @Override
    public boolean tryToZap(Hero owner, int target) {

        int currentWardEnergy = 0;
        for (Char ch : Actor.chars()){
            if (ch instanceof StaffOfMayer.Ward){
                currentWardEnergy += ((StaffOfMayer.Ward) ch).tier;
            }
        }

        int maxWardEnergy = 0;
        for (Buff buff : curUser.buffs()){
            if (buff instanceof Wand.Charger){
                if (((Charger) buff).wand() instanceof StaffOfMayer){
                    maxWardEnergy += 2 + ((Charger) buff).wand().level();
                }
            }
        }

        wardAvailable = (currentWardEnergy < maxWardEnergy);

        Char ch = Actor.findChar(target);
        if (ch instanceof StaffOfMayer.Ward){
            if (!wardAvailable && ((StaffOfMayer.Ward) ch).tier <= 3){
                GLog.w( Messages.get(this, "no_more_wards"));
                return false;
            }
        } else {
            if ((currentWardEnergy + 1) > maxWardEnergy){
                GLog.w( Messages.get(this, "no_more_wards"));
                return false;
            }
        }

        return super.tryToZap(owner, target);
    }

    @Override
    protected void onZap(Ballistica bolt) {

        int target = bolt.collisionPos;
        Char ch = Actor.findChar(target);
        if (ch != null && !(ch instanceof StaffOfMayer.Ward)){
            if (bolt.dist > 1) target = bolt.path.get(bolt.dist-1);

            ch = Actor.findChar(target);
            if (ch != null && !(ch instanceof StaffOfMayer.Ward)){
                GLog.w( Messages.get(this, "bad_location"));
                Dungeon.level.pressCell(bolt.collisionPos);
                return;
            }
        }

        if (!Dungeon.level.passable[target]){
            GLog.w( Messages.get(this, "bad_location"));
            Dungeon.level.pressCell(target);

        } else if (ch != null){
            if (ch instanceof StaffOfMayer.Ward){
                if (wardAvailable) {
                    ((StaffOfMayer.Ward) ch).upgrade( buffedLvl() );
                } else {
                    ((StaffOfMayer.Ward) ch).wandHeal( buffedLvl() );
                }
                ch.sprite.emitter().burst(MagicMissile.WardParticle.UP, ((StaffOfMayer.Ward) ch).tier);
            } else {
                GLog.w( Messages.get(this, "bad_location"));
                Dungeon.level.pressCell(target);
            }

        } else {
            StaffOfMayer.Ward ward = new StaffOfMayer.Ward();
            ward.pos = target;
            ward.wandLevel = buffedLvl();
            GameScene.add(ward, 1f);
            Dungeon.level.occupyCell(ward);
            ward.sprite.emitter().burst(MagicMissile.WardParticle.UP, ward.tier);
            Dungeon.level.pressCell(target);

        }
    }

    @Override
    protected void fx(Ballistica bolt, Callback callback) {
        MagicMissile m = MagicMissile.boltFromChar(curUser.sprite.parent,
                MagicMissile.WARD,
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

        // lvl 0 - 20%
        // lvl 1 - 33%
        // lvl 2 - 43%
        if (Random.Int( level + 5 ) >= 4) {
            for (Char ch : Actor.chars()){
                if (ch instanceof StaffOfMayer.Ward){
                    ((StaffOfMayer.Ward) ch).wandHeal(staff.buffedLvl());
                    ch.sprite.emitter().burst(MagicMissile.WardParticle.UP, ((StaffOfMayer.Ward) ch).tier);
                }
            }
        }
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color( 0x8822FF );
        particle.am = 0.3f;
        particle.setLifespan(3f);
        particle.speed.polar(Random.Float(PointF.PI2), 0.3f);
        particle.setSize( 1f, 2f);
        particle.radiateXY(2.5f);
    }

    @Override
    public String statsDesc() {
        if (levelKnown)
            return Messages.get(this, "stats_desc", level()+2);
        else
            return Messages.get(this, "stats_desc", 2);
    }

    public static class Ward extends NPC {

        public int tier = 1;
        private int wandLevel = 1;

        public int totalZaps = 0;

        {
            spriteClass = WardSprite_Mayer.class;

            alignment = Alignment.ALLY;

            properties.add(Property.IMMOVABLE);
            properties.add(Property.INORGANIC);

            viewDistance = 4;
            state = WANDERING;
        }

        @Override
        public String name() {
            return Messages.get(this, "name_" + tier );
        }

        public void upgrade(int wandLevel ){
            if (this.wandLevel < wandLevel){
                this.wandLevel = wandLevel;
            }

            switch (tier){
                case 1: case 2: default:
                    break; //do nothing
                case 3:
                    HT = 20;
                    HP = 15 + (5-totalZaps)*4;
                    break;
                case 4:
                    HT = 36;
                    HP += 19;
                    break;
                case 5:
                    HT = 58;
                    HP += 30;
                    break;
                case 6:
                    wandHeal(wandLevel);
                    break;
            }

            if (tier < 6){
                tier++;
                viewDistance++;
                if (sprite != null){
                    ((WardSprite_Mayer)sprite).updateTier(tier);
                    sprite.place(pos);
                }
                GameScene.updateFog(pos, viewDistance+1);
            }

        }

        private void wandHeal( int wandLevel ){
            if (this.wandLevel < wandLevel){
                this.wandLevel = wandLevel;
            }

            int heal;
            switch(tier){
                default:
                    return;
                case 4:
                    heal = 9;
                    break;
                case 5:
                    heal = 12;
                    break;
                case 6:
                    heal = 16;
                    break;
            }

            HP = Math.min(HT, HP+heal);
            if (sprite != null) sprite.showStatus(CharSprite.POSITIVE, Integer.toString(heal));

        }

        @Override
        public int defenseSkill(Char enemy) {
            if (tier > 3){
                defenseSkill = 4 + Dungeon.depth;
            }
            return super.defenseSkill(enemy);
        }

        @Override
        public int drRoll() {
            if (tier > 3){
                return Math.round(Random.NormalIntRange(0, 3 + Dungeon.depth/2) / (7f - tier));
            } else {
                return 0;
            }
        }

        @Override
        protected float attackDelay() {
            if (tier > 3){
                return 1f;
            } else {
                return 2f;
            }
        }

        @Override
        protected boolean canAttack( Char enemy ) {
            return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
        }

        @Override
        protected boolean doAttack(Char enemy) {
            boolean visible = fieldOfView[pos] || fieldOfView[enemy.pos];
            if (visible) {
                sprite.zap( enemy.pos );
            } else {
                zap();
            }

            return !visible;
        }

        private void zap() {
            spend( 1f );

            //always hits
            int dmg = Random.NormalIntRange( 2 + wandLevel, 8 + 3*wandLevel );
            enemy.damage( dmg, this );
            if (enemy.isAlive()){
                Wand.processSoulMark(enemy, wandLevel, 1);
            }

            if (!enemy.isAlive() && enemy == Dungeon.hero) {
                Dungeon.fail( getClass() );
            }

            totalZaps++;
            switch(tier){
                case 1: case 2: case 3: default:
                    if (totalZaps >= (2*tier-1)){
                        die(this);
                    }
                    break;
                case 4:
                    damage(5, this);
                    break;
                case 5:
                    damage(6, this);
                    break;
                case 6:
                    damage(7, this);
                    break;
            }
        }

        public void onZapComplete() {
            zap();
            next();
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
            WardSprite_Mayer sprite = (WardSprite_Mayer) super.sprite();
            sprite.linkVisuals(this);
            return sprite;
        }

        @Override
        public void updateSpriteState() {
            super.updateSpriteState();
            ((WardSprite_Mayer)sprite).updateTier(tier);
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
                    GameScene.show(new WndOptions( Messages.get(StaffOfMayer.Ward.this, "dismiss_title"),
                            Messages.get(StaffOfMayer.Ward.this, "dismiss_body"),
                            Messages.get(StaffOfMayer.Ward.this, "dismiss_confirm"),
                            Messages.get(StaffOfMayer.Ward.this, "dismiss_cancel") ){
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
        public void die(Object cause) {
            super.die(cause);

            boolean heroKilled = false;
            for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                Char ch = findChar( pos + PathFinder.NEIGHBOURS8[i] );
                if (ch != null && ch.isAlive()) {
                    int damage = Random.NormalIntRange(wandLevel + (tier * 2), wandLevel* (2 * (tier - 1)));
                    damage = Math.max( 0,  damage - ch.drRoll());
                    ch.damage( damage, this );
                    if (ch == Dungeon.hero && !ch.isAlive()) {
                        heroKilled = true;
                    }
                }
            }

            if (Dungeon.level.heroFOV[pos]) {
                Sample.INSTANCE.play( Assets.Sounds.BONES );
            }

            if (heroKilled) {
                Dungeon.fail( getClass() );
                GLog.n( Messages.get(this, "explo_kill") );
            }
        }

        @Override
        public String description() {
            return Messages.get(this, "desc_" + tier, 2+wandLevel, 8 + 3*wandLevel, tier );
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
            viewDistance = 3 + tier;
            wandLevel = bundle.getInt(WAND_LEVEL);
            totalZaps = bundle.getInt(TOTAL_ZAPS);
        }

        {
            properties.add(Property.IMMOVABLE);
        }
    }
}