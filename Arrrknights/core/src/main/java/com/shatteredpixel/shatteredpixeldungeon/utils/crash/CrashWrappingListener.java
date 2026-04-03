package com.shatteredpixel.shatteredpixeldungeon.utils.crash;

import com.badlogic.gdx.ApplicationListener;

public class CrashWrappingListener implements ApplicationListener {

    private final ApplicationListener delegate;

    public CrashWrappingListener(ApplicationListener delegate) {
        this.delegate = delegate;
    }

    @Override
    public void create() {
        try {
            delegate.create();
        } catch (Throwable t) {
            CrashCenter.recordFromRenderThread(t);
        } finally {
            CrashCenter.onGdxReady();
            CrashCenter.pumpUI();
        }
    }

    @Override
    public void resize(int width, int height) {
        safe(() -> delegate.resize(width, height));
    }

    @Override
    public void render() {
        safe(delegate::render);
        CrashCenter.pumpUI();
    }

    @Override
    public void pause() {
        safe(delegate::pause);
    }

    @Override
    public void resume() {
        safe(delegate::resume);
    }

    @Override
    public void dispose() {
        safe(delegate::dispose);
    }

    private void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable t) {
            CrashCenter.recordFromRenderThread(t);
        }
    }
}
