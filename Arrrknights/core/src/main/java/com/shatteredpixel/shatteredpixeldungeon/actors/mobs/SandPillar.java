package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gluttony;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RotHeartSprite;

public class SandPillar extends Mob {
    {
        spriteClass = RotHeartSprite.class;

        HP = HT = 80;
        defenseSkill = 0;

        EXP = 4;

        state = PASSIVE;

        properties.add(Property.IMMOVABLE);
        properties.add(Property.MINIBOSS);
        immunities.add(CorrosiveGas.class);

        loot = new Gluttony();
        lootChance = 1f;
    }

    @Override
    public int defenseProc(Char enemy, int damage) {
        GameScene.add(Blob.seed(pos, 20, CorrosiveGas.class));

        return super.defenseProc(enemy, damage);
    }
}
