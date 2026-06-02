package com.example.logitrack.memory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class GameScreen {

    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private BitmapFont font;        // para dibujar texto
    private SpriteBatch batch;      // necesario para dibujar texto
    private boolean gameWon = false; // estado de victoria

    private static final Color[] COLORS = {
            Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
            Color.ORANGE, Color.PURPLE, Color.CYAN, Color.PINK, Color.BROWN
    };

    private int[] cardValues;
    private boolean[] flipped;
    private boolean[] matched;

    private int firstFlipped = -1;
    private int secondFlipped = -1;
    private float waitTimer = 0f;

    private static final int COLS = 4;
    private static final int ROWS = 4;
    private static final int TOTAL = COLS * ROWS;

    private Rectangle[] cardRects;

    public void create() {
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //inicializar font y batch
        font = new BitmapFont();
        font.getData().setScale(5f);
        batch = new SpriteBatch();

        cardValues = new int[TOTAL];
        flipped = new boolean[TOTAL];
        matched = new boolean[TOTAL];
        cardRects = new Rectangle[TOTAL];

        int[] values = new int[TOTAL];
        for (int i = 0; i < TOTAL; i++) {
            values[i] = i / 2;
        }
        shuffle(values);
        cardValues = values;

        float padding = 20f;
        float cardW = (Gdx.graphics.getWidth() - padding * (COLS + 1)) / COLS;
        float cardH = (Gdx.graphics.getHeight() - padding * (ROWS + 1)) / ROWS;

        for (int i = 0; i < TOTAL; i++) {
            int col = i % COLS;
            int row = i / COLS;
            float x = padding + col * (cardW + padding);
            float y = Gdx.graphics.getHeight() - padding - (row + 1) * cardH - row * padding;
            cardRects[i] = new Rectangle(x, y, cardW, cardH);
        }
    }

    public void render() {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        // Si ya ganó, solo mostrar pantalla de victoria
        if (gameWon) {
            mostrarVictoria();
            return;
        }

        if (secondFlipped != -1) {
            waitTimer += Gdx.graphics.getDeltaTime();
            if (waitTimer > 1.2f) {
                if (cardValues[firstFlipped] == cardValues[secondFlipped]) {
                    matched[firstFlipped] = true;
                    matched[secondFlipped] = true;
                } else {
                    flipped[firstFlipped] = false;
                    flipped[secondFlipped] = false;
                }
                firstFlipped = -1;
                secondFlipped = -1;
                waitTimer = 0f;
            }
        }

        if (Gdx.input.justTouched() && secondFlipped == -1) {
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);

            for (int i = 0; i < TOTAL; i++) {
                if (cardRects[i].contains(touch.x, touch.y) && !flipped[i] && !matched[i]) {
                    flipped[i] = true;
                    if (firstFlipped == -1) {
                        firstFlipped = i;
                    } else if (firstFlipped != i) {
                        secondFlipped = i;
                    }
                    break;
                }
            }
        }

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < TOTAL; i++) {
            Rectangle r = cardRects[i];
            if (matched[i]) {
                shapeRenderer.setColor(Color.DARK_GRAY);
            } else if (flipped[i]) {
                shapeRenderer.setColor(COLORS[cardValues[i]]);
            } else {
                shapeRenderer.setColor(Color.LIGHT_GRAY);
            }
            shapeRenderer.rect(r.x, r.y, r.width, r.height);
        }

        shapeRenderer.end();

        // comprobar si ha ganado tras dibujar las cartas
        comprobarVictoria();
    }

    // comprueba si todas las cartas están emparejadas
    private void comprobarVictoria() {
        for (boolean m : matched) {
            if (!m) return;
        }
        gameWon = true;
    }

    //dibuja la pantalla de victoria
    private void mostrarVictoria() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1f);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.setColor(Color.YELLOW);
        font.draw(batch, "HAS GANADO!",
                Gdx.graphics.getWidth() / 2f - 200,
                Gdx.graphics.getHeight() / 2f + 50);
        font.setColor(Color.WHITE);
        font.draw(batch, "Toca para volver a empezar",
                Gdx.graphics.getWidth() / 2f - 180,
                Gdx.graphics.getHeight() / 2f - 50);
        batch.end();

       // si toca la pantalla, reinicia el juego
        if (Gdx.input.justTouched()) {
            gameWon = false;
            create();
        }
    }

    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
        batch.dispose();
    }

    private void shuffle(int[] array) {
        java.util.Random rng = new java.util.Random();
        for (int i = array.length - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            int tmp = array[i];
            array[i] = array[j];
            array[j] = tmp;
        }
    }
}