package com.example.logitrack.memory;

import com.badlogic.gdx.ApplicationAdapter;

public class MemoryGame extends ApplicationAdapter {

    private GameScreen gameScreen;

    @Override
    public void create() {
        gameScreen = new GameScreen();
        gameScreen.create();
    }

    @Override
    public void render() {
        gameScreen.render();
    }

    @Override
    public void resize(int width, int height) {
        gameScreen.resize(width, height);
    }

    @Override
    public void dispose() {
        gameScreen.dispose();
    }
}