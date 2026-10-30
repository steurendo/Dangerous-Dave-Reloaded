package ui;

import game.Model;
import game.ModelScore;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import scenarios.Scenario;
import scenarios.ScenarioLevel;
import scenarios.ScenarioMenu;
import utils.ErrorDialog;
import utils.ResourceLoader;
import utils.Textures;
import utils.Timing;

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
    // Oltre questa durata un frame non viene "recuperato": il gioco rallenta invece di fare passi enormi
    private static final double MAX_FRAME_TIME = 1.0 / 20;
    private int refreshRate;
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
            //FREQUENZA DELLO SCHERMO
            refreshRate = detectRefreshRate();
            glfwSetWindowPosCallback(window, (w, x, y) -> refreshRate = detectRefreshRate());
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
        double previous = glfwGetTime();

        while (active) {
            glfwPollEvents();
            double now = glfwGetTime();
            Timing.setFrameTime(frameTime(now - previous));
            previous = now;

            Scenario scenario = switchScenario[model.getState()];
            scenario.commands();
            scenario.collisions();
            scenario.update();

            glClearColor(0, 0, 0, 0);
            glClear(GL_COLOR_BUFFER_BIT);
            switchScenario[model.getState()].render();
            glfwSwapBuffers(window);

            if (glfwGetTime() - now < 0.001) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignored) {}
            }

            if (glfwWindowShouldClose(window))
                active = false;
        }
        // Termina la sessione GLFW
        glfwDestroyWindow(window);
        glfwTerminate();
        input.free();
        System.exit(0);
    }

    private double frameTime(double measured) {
        double period = 1.0 / refreshRate;
        double frames = Math.max(1, Math.round(measured / period));
        if (Math.abs(measured - frames * period) < period / 4)
            measured = frames * period;
        return Math.min(measured, MAX_FRAME_TIME);
    }

    private int detectRefreshRate() {
        long monitor = glfwGetPrimaryMonitor();
        try (MemoryStack stack = stackPush()) {
            IntBuffer x = stack.mallocInt(1), y = stack.mallocInt(1);
            IntBuffer w = stack.mallocInt(1), h = stack.mallocInt(1);
            glfwGetWindowPos(window, x, y);
            glfwGetWindowSize(window, w, h);
            int centerX = x.get(0) + w.get(0) / 2, centerY = y.get(0) + h.get(0) / 2;
            PointerBuffer monitors = glfwGetMonitors();
            if (monitors != null) {
                for (int i = 0; i < monitors.limit(); i++) {
                    long m = monitors.get(i);
                    GLFWVidMode mode = glfwGetVideoMode(m);
                    if (mode == null) continue;
                    glfwGetMonitorPos(m, x, y);
                    if (centerX >= x.get(0) && centerX < x.get(0) + mode.width() &&
                            centerY >= y.get(0) && centerY < y.get(0) + mode.height()) {
                        monitor = m;
                        break;
                    }
                }
            }
        }
        GLFWVidMode mode = glfwGetVideoMode(monitor);
        return mode != null && mode.refreshRate() > 0 ? mode.refreshRate() : 60;
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