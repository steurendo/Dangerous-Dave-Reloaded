package ui;

import game.Model;
import game.ModelScore;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import scenarios.Scenario;
import scenarios.ScenarioLevel;
import scenarios.ScenarioMenu;
import utils.ErrorDialog;
import utils.ResourceLoader;
import utils.Textures;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

public class Game {
    public static final String WINDOW_TITLE = "Dangerous Dave - Reloaded";
    private long window;
    private int windowW;
    private int windowH;
    private GLFWKeyCallback input;
    private Model model;
    private Scenario[] switchScenario;
    private boolean active;

    public Game() {
        try {
            active = true;
            windowW = 1280;
            windowH = 800;
            if (!GLFW.glfwInit()) {
                ErrorDialog.show("Errore: unable to start GLFW (code: -1).");
                System.exit(-1);
            }
            GLFW.glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
            window = GLFW.glfwCreateWindow(windowW, windowH, "Dangerous Dave - Reloaded", 0, 0);
            if (window == 0) {
                ErrorDialog.show("Errore: unable to create window (code: -2).");
                System.exit(-2);
            }
            //ICONA
            setWindowIcon(this, window);
            //CONTROLLO PRESSIONE TASTI
            glfwSetKeyCallback(window, input = new Keyboard());
            //AZIONI IN SEGUITO AD UN RESIZE
            glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
                windowW = width;
                windowH = height;
                glViewport(0, 0, width, height);
            });
            //CENTRA LA FINESTRA
            try (MemoryStack stack = stackPush()) {
                IntBuffer pWidth = stack.mallocInt(1);
                IntBuffer pHeight = stack.mallocInt(1);
                glfwGetWindowSize(window, pWidth, pHeight);
                GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
                glfwSetWindowPos(
                        window,
                        (vidmode.width() - pWidth.get(0)) / 2,
                        (vidmode.height() - pHeight.get(0)) / 2
                );
            }
            //FINESTRA DI LAVORO CORRENTE
            glfwMakeContextCurrent(window);
            //V-SYNC
            glfwSwapInterval(1);
            //RENDE VISIBILE LA FINESTRA
            glfwShowWindow(window);
            //PERMETTE DI FAR INTERAGIRE OPENGL E GLFW
            GL.createCapabilities();
            //IMPOSTAZIONI
            glEnable(GL_TEXTURE_2D);
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            glOrtho(0, 1, 1, 0, 1, -1);
            glMatrixMode(GL_MODELVIEW);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            //TEXTURES
            //INIZIALIZZAZIONI VARIE
            Textures textures = new Textures();
            model = new Model(textures);
            Scenario scenarioMenu = new ScenarioMenu(model, textures);
            ModelScore modelScore = new ModelScore();
            Scenario scenarioLevel = new ScenarioLevel(model, modelScore, textures);
            switchScenario = new Scenario[]{scenarioMenu, scenarioLevel};
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        double t0, t1 = glfwGetTime(), deltaT, deltaTAvg = 0;
        while (active) {
            while (glfwGetTime() < t1 + 1f / 60) ;
            t0 = t1;
            t1 = glfwGetTime();
            deltaT = t1 - t0;
            if (deltaT > 10 * deltaTAvg) {
                deltaTAvg = (deltaTAvg + deltaT) / 2;
                continue;
            }
            deltaTAvg = (deltaTAvg + deltaT) / 2;
            glfwPollEvents();
            glClearColor(0, 0, 0, 0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            switchScenario[model.getState()].commands(deltaT);
            switchScenario[model.getState()].collisions(deltaT);
            switchScenario[model.getState()].update(deltaT);
            switchScenario[model.getState()].render(deltaT);
            glfwSwapBuffers(window);
            if (glfwWindowShouldClose(window)) {
                active = false;
                glfwSetWindowShouldClose(window, true);
            }
        }
        // Termina la sessione GLFW
        glfwDestroyWindow(window);
        glfwTerminate();
        input.free();
        System.exit(0);
    }

    public static void setWindowIcon(Object context, long window) throws IOException {
        BufferedImage image = ImageIO.read(ResourceLoader.load(context, "icon.png"));

        int width = image.getWidth();
        int height = image.getHeight();

        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);

        ByteBuffer buffer = memAlloc(width * height * 4);

        // GLFW vuole RGBA, top-left → bottom-left NON viene invertito
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pixels[y * width + x];

                buffer.put((byte) ((pixel >> 16) & 0xFF)); // R
                buffer.put((byte) ((pixel >> 8) & 0xFF));  // G
                buffer.put((byte) (pixel & 0xFF));         // B
                buffer.put((byte) ((pixel >> 24) & 0xFF)); // A
            }
        }

        buffer.flip();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            GLFWImage icon = GLFWImage.malloc(stack);
            icon.set(width, height, buffer);

            GLFWImage.Buffer icons = GLFWImage.malloc(1, stack);
            icons.put(0, icon);

            glfwSetWindowIcon(window, icons);
        }

        memFree(buffer);
    }
}