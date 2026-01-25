package ui;

import org.lwjgl.glfw.GLFWKeyCallback;

import java.util.Timer;
import java.util.TimerTask;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class Keyboard extends GLFWKeyCallback {
    private static boolean available = true;
    public static boolean[] keys = new boolean[65536];

    public void invoke(long window, int key, int scancode, int action, int mods) {
        if (key >= 0)
            keys[key] = action != GLFW_RELEASE;
    }

    public static boolean isKeyDown(int keycode) {
        return keys[keycode];
    }

    public static boolean isAvailable() {
        return available;
    }

    public static void spamLock(SpamLockTime time) {
        available = false;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                available = true;
            }
        }, time == SpamLockTime.Short ? 100 : 500);
    }
}