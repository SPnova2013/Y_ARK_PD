package com.shatteredpixel.shatteredpixeldungeon.effects.particles;
import com.watabou.noosa.particles.Emitter;

public class CompositeParticleFactory extends Emitter.Factory {
    private Emitter.Factory[] factories;

    public CompositeParticleFactory(Emitter.Factory... factories) {
        this.factories = factories;
    }

    @Override
    public void emit(Emitter emitter, int index, float x, float y) {
        for (Emitter.Factory f : factories) {
            f.emit(emitter, index, x, y);
        }
    }

    @Override
    public boolean lightMode() {
        for (Emitter.Factory f : factories) {
            if (f.lightMode()) return true;
        }
        return false;
    }
}