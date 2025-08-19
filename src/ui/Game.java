package ui;

import game.Model;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import scenarios.*;
import utils.Textures;

import java.nio.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;

public class Game {
    private long window;
    private int windowW;
    private int windowH;
    private GLFWKeyCallback input;
    private Model model;
    private Scenario scenarioLevel;
    private Scenario scenarioTransition;
    private Scenario scenarioScore;
    private Scenario[] switchScenario;
    private boolean active;

    public Game() {
        try {
            active = true;
            windowW = 1280;
            windowH = 800;
            if (!GLFW.glfwInit())
                throw new Exception("Unable to start GLFW");
            GLFW.glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
            window = GLFW.glfwCreateWindow(windowW, windowH, "Dangerous Dave - Reloaded", 0, 0);
            if (window == 0)
                throw new Exception("Unable to create window");
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
            scenarioLevel = new ScenarioLevel(model, textures);
            scenarioTransition = new ScenarioTransition(model, textures);
            scenarioScore = new ScenarioScore(model, textures);
            switchScenario = new Scenario[]{scenarioMenu, scenarioLevel, scenarioTransition, scenarioScore};
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getWindowWidth() {
        return windowW;
    }

    public int getWindowHeight() {
        return windowH;
    }

    public void start() {
        double t0 = glfwGetTime(), t1 = glfwGetTime(), deltaT, inc = 0;
        while (active) {
            t0 = t1;
            t1 = glfwGetTime();
            deltaT = t1 - t0;
            inc += deltaT;
            if (inc >= 1) {
//                System.out.println("Tick " + inc);
                inc = 0;
            }
            glfwPollEvents();
            glClearColor(0, 0, 0, 0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            switchScenario[model.getState()].commands();
            switchScenario[model.getState()].collisions();
            switchScenario[model.getState()].update();
            switchScenario[model.getState()].render();
            glfwSwapBuffers(window);
            if (glfwWindowShouldClose(window)) {
                active = false;
                glfwSetWindowShouldClose(window, true);
            }
        }
        //TERMINA LA SESSIONE GLFW
        glfwDestroyWindow(window);
        glfwTerminate();
        input.free();
    }
}