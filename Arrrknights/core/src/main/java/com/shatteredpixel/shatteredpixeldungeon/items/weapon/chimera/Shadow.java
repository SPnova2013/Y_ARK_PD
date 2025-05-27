package com.shatteredpixel.shatteredpixeldungeon.items.weapon.chimera;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChenCombo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Combo;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ChaliceOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Firmament;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.ShadowFirmament;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Random;

public class Shadow extends Weapon.Chimera {
    @Override
    public String beforeName() {
        return Messages.get(this, "name");
    }
    @Override
    public float dmgFactor(){
        return 0.65f;
    }
    private boolean doubleattack = true;
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        //武器为赤霄或影霄时，将连击交由它们自身处理
        if(weapon instanceof ShadowFirmament || weapon instanceof Firmament) return super.proc(weapon, attacker, defender, damage);
        if (doubleattack) {
            doubleattack = false;
            if (!attacker.attack(defender)) {
                doubleattack = true; }
            else {
                defender.sprite.bloodBurstA( defender.sprite.center(), 4 );
                defender.sprite.flash();
                if (attacker instanceof Hero && Dungeon.hero.subClassSet.contains(HeroSubClass.GLADIATOR)) {
                    Buff.affect(attacker, Combo.class).bounshit(defender);
                }
                else if (attacker instanceof Hero && Dungeon.hero.heroClass == HeroClass.CHEN) {
                    Buff.affect(attacker, ChenCombo.class).bounshit(defender);
                }
            }
        }
        else doubleattack = true;

        if (attacker instanceof Hero) {
            if (Dungeon.hero.belongings.getItem(ChaliceOfBlood.class) != null) {
                if (((Hero) attacker).belongings.getItem(ChaliceOfBlood.class).isEquipped(Dungeon.hero)) {
                    if (Random.Int(20) < 1)
                        damage *= 1.5f;
                }
            }
        }
        return damage;
    }
}
