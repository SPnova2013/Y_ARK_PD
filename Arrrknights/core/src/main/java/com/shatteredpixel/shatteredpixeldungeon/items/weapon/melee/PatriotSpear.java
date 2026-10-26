package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;


import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.coversprite.SpearFlyingCover;
import com.shatteredpixel.shatteredpixeldungeon.effects.coversprite.SpearLandingCover;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.PlateArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMight;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfTenacity;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.PosTweener;
import com.watabou.utils.Callback;

public class PatriotSpear extends MeleeWeapon implements MeleeWeapon.BlockingWeapon {

    //the spear drops for this long, from the top of the map down to the target
    public static final float ABILITY_FLIGHT_TIME = 0.25f;
    //damage is damageRoll * this
    public static final float ABILITY_DAMAGE_MULT = 6f;

    {
        image = ItemSpriteSheet.REQUIEM;
        hitSound = Assets.Sounds.HIT_SPEAR;
        hitSoundPitch = 1.01f;

        tier = 5;
        DLY = 2f; // 공격속도 50%
    }

    @Override
    public int max(int lvl) {
        if (Dungeon.hero.belongings.armor instanceof PlateArmor) {
            if (Dungeon.hero.belongings.getItem(RingOfMight.class) != null && Dungeon.hero.belongings.getItem(RingOfTenacity.class) != null) {
                if (Dungeon.hero.belongings.getItem(RingOfTenacity.class).isEquipped(Dungeon.hero) && Dungeon.hero.belongings.getItem(RingOfMight.class).isEquipped(Dungeon.hero))
                    return 2 * (tier + 2) +    //14
                            lvl * (tier - 2);   // +3
            }
        }
        return  2*(tier) +    //10
                lvl*(tier-3);   // +2
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    public String abilityInfo() {
        if (levelKnown) {
            return Messages.get(this, "ability_desc",
                    augment.damageFactor(Math.round(min() * ABILITY_DAMAGE_MULT)),
                    augment.damageFactor(Math.round(max() * ABILITY_DAMAGE_MULT)));
        } else {
            return Messages.get(this, "typical_ability_desc",
                    Math.round(min(0) * ABILITY_DAMAGE_MULT),
                    Math.round(max(0) * ABILITY_DAMAGE_MULT));
        }
    }

    @Override
    protected void duelistAbility(final Hero hero, Integer target) {
        if (target == null) {
            return;
        }

        final Char enemy = Actor.findChar(target);
        if (enemy == null || enemy == hero || !Dungeon.level.heroFOV[target]) {
            GLog.w(Messages.get(this, "ability_no_target"));
            return;
        }

        hero.belongings.abilityWeapon = this;
        beforeAbilityUsed(hero, enemy);

        final int landing = target;

        hero.busy();
        hero.sprite.zap(landing);
        Sample.INSTANCE.play(Assets.Sounds.HIT_SPEAR, 1f, 0.8f);

        //the spear falls in from straight above, starting at the top edge of the map
        SpearFlyingCover flying = new SpearFlyingCover();
        flying.onFinish = new Callback() {
            @Override
            public void call() {
                //continue outside of the scene's update loop
                Game.runOnRenderThread(new Callback() {
                    @Override
                    public void call() {
                        landSpear(hero, landing);
                    }
                });
            }
        };
        flying.placeAndPlay(landing % Dungeon.level.width());

        if (flying.parent != null) {
            flying.parent.add(new PosTweener(flying, flying.worldToCamera(landing), ABILITY_FLIGHT_TIME));
        }
    }

    private void landSpear(Hero hero, int landing) {

        new SpearLandingCover().centerAndPlay(landing);
        Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
        Camera.main.shake(2, 0.2f);
        GameScene.flash(0x60FFFFFF);

        Char enemy = Actor.findChar(landing);
        if (enemy != null && enemy != hero && enemy.isAlive()) {
            enemy.damage(Math.round(damageRoll(hero) * ABILITY_DAMAGE_MULT), hero);
            if (enemy.sprite != null) {
                enemy.sprite.burst(CharSprite.NEGATIVE, 10);
            }
        }

        afterAbilityUsed(hero);
        hero.spendAndNext(Actor.TICK);
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {

        for (int count = 0; count<3; count++) {
            if (defender.isAlive()) {
                defender.damage(attacker.damageRoll() - defender.drRoll(), attacker);
                defender.sprite.burst(CharSprite.NEGATIVE, 10);
            }
        }

        return super.proc(attacker, defender, damage);
    }

    @Override
    public int defenseFactor( Char owner ) {
        return DRMax();
    }

    public int DRMax(){
        return DRMax(buffedLvl());
    }

    public int DRMax(int lvl){
        return 3 + 2 * lvl;
    }

    public String statsInfo(){
        if (isIdentified()){
            return Messages.get(this, "stats_desc", 3+2*buffedLvl());
        } else {
            return Messages.get(this, "typical_stats_desc", 3);
        }
    }

    @Override
    public String desc() {
        String info = Messages.get(this, "desc");
        if (Dungeon.hero.belongings.armor instanceof PlateArmor) {
            if (Dungeon.hero.belongings.getItem(RingOfMight.class) != null && Dungeon.hero.belongings.getItem(RingOfTenacity.class) != null) {
                if (Dungeon.hero.belongings.getItem(RingOfTenacity.class).isEquipped(Dungeon.hero) && Dungeon.hero.belongings.getItem(RingOfMight.class).isEquipped(Dungeon.hero))
                    info += "\n\n" + Messages.get(PatriotSpear.class, "setbouns");
            }
        }

        return info;
    }
}
