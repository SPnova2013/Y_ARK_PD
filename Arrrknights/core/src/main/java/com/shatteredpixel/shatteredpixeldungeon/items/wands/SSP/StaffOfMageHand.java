package com.shatteredpixel.shatteredpixeldungeon.items.wands.SSP;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PinCushion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.WolfMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Skill.SkillBook;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfMight;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfAdrenalineSurge;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class StaffOfMageHand extends Wand {
    {
        image = ItemSpriteSheet.ARTIFACT_BEACON;
        collisionProperties = Ballistica.STOP_TARGET | Ballistica.STOP_SOLID;
    }
    @Override
    protected void onZap( Ballistica bolt ) {
        int before = Dungeon.level.map[bolt.collisionPos];

        Char ch = Actor.findChar( bolt.collisionPos );
        if (ch != null) {
            if(ch instanceof Mob) {
                //偷取物品
                if(ch.buff(MageHandStolenTracker.class) == null) {
                    ((Mob) ch).rollToDropLoot();
                    Buff.affect(ch, MageHandStolenTracker.class);
                    if((Mob)ch instanceof Mimic && ((Mimic) ch).items == null) ch.die(this);
                }
                //拿取投掷物
                while (ch.buff(PinCushion.class) != null) {
                    Item item = ch.buff(PinCushion.class).grabOne();

                    if (item.doPickUp(Dungeon.hero)) {
                        Dungeon.hero.spend(-Item.TIME_TO_PICK_UP); //casting the spell already takes a turn
                        GLog.i( Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", item.name())) );

                    } else {
                        GLog.w(Messages.get(this, "cant_grab"));
                        Dungeon.level.drop(item, ch.pos).sprite.drop();
                        return;
                    }
                }
                if(Dungeon.hero.buff(WolfMark.class)!=null){
                    WolfMark WM = Dungeon.hero.buff(WolfMark.class);
                    if(WM.object == ch.id()){
                        WM.detach();
                        Sample.INSTANCE.play( Assets.Sounds.ITEM );
                    }
                }
            }
        }

        //念力拾物
        Heap heap = Dungeon.level.heaps.get( bolt.collisionPos );
        if (heap != null) {
            switch (heap.type) {
                case HEAP:
                    transport( heap );
                    break;
                case CHEST:
                case MIMIC:
                case TOMB:
                case SKELETON:
                    heap.open( curUser );
                    break;
                default:
            }
        }
        Dungeon.level.pressCell( bolt.collisionPos);
        if (before == Terrain.OPEN_DOOR) {
            Level.set( bolt.collisionPos, Terrain.DOOR );
            GameScene.updateMap( bolt.collisionPos );
        } else if (before == Terrain.WATER) {
            GameScene.ripple( bolt.collisionPos );
        }

        //路径推动
        int beamdis = Dungeon.level.distance(Dungeon.hero.pos, bolt.collisionPos);
        wandattack(bolt.collisionPos, beamdis);
    }

    private void transport( Heap heap ) {
        while(!heap.isEmpty()){
            Item item = heap.pickUp();
            if (item.doPickUp( curUser )) {

                if (item instanceof Dewdrop) {
                    // Do nothing
                } else {
                    if (((item instanceof ScrollOfUpgrade || item instanceof ScrollOfEnchantment) && ((Scroll)item).isKnown()) ||
                            ((item instanceof PotionOfStrength || item instanceof ElixirOfMight || item instanceof PotionOfAdrenalineSurge) && ((Potion)item).isKnown())) {
                    } else {

                    }
                }
                GLog.i( Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", item.name())) );
            } else {
                Dungeon.level.drop( item, curUser.pos ).sprite.drop();
            }
        }
    }

    private void wandattack(int defender, int distance) {
        Ballistica beam = new Ballistica(Dungeon.hero.pos, defender, Ballistica.WONT_STOP);
        int maxDistance = distance;

        ArrayList<Char> chars = new ArrayList<>();

        for (int c : beam.subPath(1, maxDistance)) {
            Char ch;
            if ((ch = Actor.findChar( c )) != null) {
                chars.add( ch );
            }
        }

        for (Char ch : chars) {
            if (!ch.properties().contains(Char.Property.IMMOVABLE)) {
                processSoulMark(ch, chargesPerCast());
                Ballistica trajectory = new Ballistica(curUser.pos, ch.pos, Ballistica.STOP_TARGET);
                trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
                WandOfBlastWave.throwChar(ch, trajectory, 1 + this.buffedLvl()/2);
            }
        }
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        if (Dungeon.hero.belongings.getItem(SkillBook.class) != null) {
            SkillBook Item = Dungeon.hero.belongings.getItem(SkillBook.class);
            Item.SetCharge(1);
            SpellSprite.show(attacker, SpellSprite.CHARGE);
        }
    }

    public static class MageHandStolenTracker extends Buff{}
}
