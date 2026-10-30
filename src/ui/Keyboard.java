package ui;

import org.lwjgl.glfw.GLFWKeyCallback;
import utils.Timing;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class Keyboard extends GLFWKeyCallback {
    private static final boolean[] keys = new boolean[65536];
    private static final boolean[] pressed = new boolean[65536];
    private static double lockTime = 0;

    public void invoke(long window, int key, int scancode, int action, int mods) {
        if (key < 0) return;
        keys[key] = action != GLFW_RELEASE;
        if (action == GLFW_PRESS) pressed[key] = true;
    }

    public static boolean isKeyDown(int keycode) {
        return keys[keycode] || pressed[keycode];
    }

    public static boolean isAvailable() {
        return lockTime <= 0;
    }

    public static void spamLock(SpamLockTime time) {
        lockTime = time == SpamLockTime.Short ? 0.1 : 0.5;
    }

    public static void endFrame() {
        Arrays.fill(pressed, false);
        if (lockTime > 0) lockTime -= Timing.scale() / 60;
    }
}