package com.example.logitrack.memory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class GameScreen {

    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;

    // Colores para cada par de cartas (8 pares = 16 cartas)
    private static final Color[] COLORS = {
            Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
            Color.ORANGE, Color.PURPLE, Color.CYAN, Color.PINK
    };

    private int[] cardValues;      // valor de cada carta (0-7, indica el par)
    private boolean[] flipped;     // si la carta está boca arriba
    private boolean[] matched;     // si la carta ya está emparejada

    private int firstFlipped = -1;  // índice de la primera carta volteada
    private int secondFlipped = -1; // índice de la segunda carta volteada
    private float waitTimer = 0f;   // temporizador para ocultar cartas no emparejadas

    private static final int COLS = 4;
    private static final int ROWS = 4;
    private static final int TOTAL = COLS * ROWS; // 16 cartas

    private Rectangle[] cardRects;

    public void create() {
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Inicializar cartas
        cardValues = new int[TOTAL];
        flipped = new boolean[TOTAL];
        matched = new boolean[TOTAL];
        cardRects = new Rectangle[TOTAL];

        // Crear pares y mezclar
        int[] values = new int[TOTAL];
        for (int i = 0; i < TOTAL; i++) {
            values[i] = i / 2;
        }
        shuffle(values);
        cardValues = values;

        // Calcular posición y tamaño de cada carta
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
        // Limpiar pantalla
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        // Manejar espera entre cartas
        if (secondFlipped != -1) {
            waitTimer += Gdx.graphics.getDeltaTime();
            if (waitTimer > 1.2f) {
                // Comprobar si son pareja
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

        // Detectar toque
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

        // Dibujar cartas
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
    }

    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    public void dispose() {
        shapeRenderer.dispose();
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